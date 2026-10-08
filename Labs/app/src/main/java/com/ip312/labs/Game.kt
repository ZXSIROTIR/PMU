package com.ip312.labs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class Game : Fragment(),
    GameListener {

    private lateinit var gameView: Buggame
    private lateinit var textScore: TextView
    private lateinit var textHits: TextView
    private lateinit var textMisses: TextView
    private lateinit var textTime: TextView

    private var score = 0
    private var hits = 0
    private var misses = 0

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
        gameView = view.findViewById(R.id.gameView)
        textScore = view.findViewById(R.id.textScore)
        textHits = view.findViewById(R.id.textHits)
        textMisses = view.findViewById(R.id.textMisses)
        textTime = view.findViewById(R.id.textTime)

        gameView.listener = this

        view.post { startGame() }
    }

    private fun startGame() {

        score = 0
        hits = 0
        misses = 0

        updateStatistics()

        val speedLevel = prefs.getInt("speed", 1) + 1
        val maxBugs = prefs.getInt("cockroaches", 4) + 1
        val roundMinutes = prefs.getInt("round", 4) + 1

        val speedMultiplier = 0.5f + speedLevel * 0.15f

        gameView.startGame(
            durationMs = roundMinutes * 60_000L,
            maxBugs = maxBugs,
            speedMultiplier = speedMultiplier
        )
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
        Toast.makeText(
            requireContext(),
            "Игра окончена! Очки: $score",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        gameView.stopGame()
        super.onDestroyView()
    }
}