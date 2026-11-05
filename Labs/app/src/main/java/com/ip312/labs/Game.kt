package com.ip312.labs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ip312.labs.data.AppDatabase
import com.ip312.labs.data.GameRecord
import com.ip312.labs.data.User
import kotlinx.coroutines.launch

class Game : Fragment(),
    GameListener {

    private lateinit var viewModel: MainViewModel

    private lateinit var gameView: Buggame
    private lateinit var textScore: TextView
    private lateinit var textHits: TextView
    private lateinit var textMisses: TextView
    private lateinit var textTime: TextView

    private lateinit var resultPanel: View
    private lateinit var resultScore: TextView
    private lateinit var resultHits: TextView
    private lateinit var resultMisses: TextView
    private lateinit var resultAccuracy: TextView
    private lateinit var resultSaved: TextView
    private lateinit var buttonRestart: Button

    private lateinit var startPanel: View
    private lateinit var textStartPlayer: TextView
    private lateinit var buttonStart: Button

    private var score = 0
    private var hits = 0
    private var misses = 0

    private var activePlayer: User? = null
    private var roundMinutes = 1

    private val prefs by lazy {
        requireContext().getSharedPreferences(
            "game_settings",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.game,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        gameView = view.findViewById(R.id.gameView)
        textScore = view.findViewById(R.id.textScore)
        textHits = view.findViewById(R.id.textHits)
        textMisses = view.findViewById(R.id.textMisses)
        textTime = view.findViewById(R.id.textTime)

        resultPanel = view.findViewById(R.id.resultPanel)
        resultScore = view.findViewById(R.id.resultScore)
        resultHits = view.findViewById(R.id.resultHits)
        resultMisses = view.findViewById(R.id.resultMisses)
        resultAccuracy = view.findViewById(R.id.resultAccuracy)
        resultSaved = view.findViewById(R.id.resultSaved)
        buttonRestart = view.findViewById(R.id.buttonRestart)

        startPanel = view.findViewById(R.id.startPanel)
        textStartPlayer = view.findViewById(R.id.textStartPlayer)
        buttonStart = view.findViewById(R.id.buttonStart)

        gameView.listener = this

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                textStartPlayer.text =
                    "Игрок не выбран.\nЗарегистрируйтесь или выберите игрока в меню (⋮)."
                buttonStart.isEnabled = false
            } else {
                textStartPlayer.text = "Игрок: ${user.name}"
                buttonStart.isEnabled = true
            }
        }

        buttonStart.setOnClickListener {
            if (startGame()) {
                startPanel.visibility = View.GONE
            }
        }

        buttonRestart.setOnClickListener {
            startGame()
        }
    }

    private fun startGame(): Boolean {

        val player = viewModel.currentUser.value ?: return false

        activePlayer = player

        resultPanel.visibility = View.GONE
        resultSaved.text = ""

        score = 0
        hits = 0
        misses = 0

        updateStatistics()

        val speedLevel = prefs.getInt("speed", 1) + 1
        val maxBugs = prefs.getInt("cockroaches", 4) + 1
        roundMinutes = prefs.getInt("round", 4) + 1
        val sizeLevel = prefs.getInt("bugSize", 4) + 1

        val speedMultiplier = 0.5f + speedLevel * 0.15f
        val sizeMultiplier = 0.5f + sizeLevel * 0.1f

        gameView.startGame(
            durationMs = roundMinutes * 60_000L,
            maxBugs = maxBugs,
            speedMultiplier = speedMultiplier,
            sizeMultiplier = sizeMultiplier
        )

        return true
    }

    private fun updateStatistics() {
        textScore.text = "Очки: $score"
        textHits.text = "Попадания: $hits"
        textMisses.text = "Промахи: $misses"
    }

    override fun onBugHit(points: Int) {
        score += points
        hits++
        updateStatistics()
    }

    override fun onMiss(penalty: Int) {
        score -= penalty
        misses++
        updateStatistics()
    }

    override fun onTimeChanged(seconds: Long) {
        textTime.text = "Время: $seconds"
    }

    override fun onGameFinished() {
        textTime.text = "Время: 0"

        val totalTaps = hits + misses
        val accuracy =
            if (totalTaps == 0) 0
            else hits * 100 / totalTaps

        resultScore.text = "Очки: $score"
        resultHits.text = "Попадания: $hits"
        resultMisses.text = "Промахи: $misses"
        resultAccuracy.text = "Точность: $accuracy%"

        resultPanel.visibility = View.VISIBLE

        saveRecord()
    }

    private fun saveRecord() {
        val player = activePlayer ?: return

        val record = GameRecord(
            userId = player.id,
            score = score,
            hits = hits,
            misses = misses,
            difficulty = player.difficulty,
            roundMinutes = roundMinutes,
            playedAt = System.currentTimeMillis()
        )

        val dao = AppDatabase
            .getInstance(requireContext())
            .recordDao()

        resultSaved.text = "Сохранение результата…"

        lifecycleScope.launch {
            dao.insert(record)
            resultSaved.text =
                "Результат игрока «${player.name}» сохранён во вкладке «Рекорды»"
        }
    }

    override fun onDestroyView() {
        gameView.stopGame()
        super.onDestroyView()
    }
}
