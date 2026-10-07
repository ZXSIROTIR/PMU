package com.ip312.labs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class Game : Fragment(),
    GameListener {

    private lateinit var gameView: Buggame
    private lateinit var textScore: TextView
    private lateinit var textHits: TextView
    private lateinit var textMisses: TextView

    private var score = 0
    private var hits = 0
    private var misses = 0

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
        gameView =
            view.findViewById(R.id.gameView)

        textScore =
            view.findViewById(R.id.textScore)

        textHits =
            view.findViewById(R.id.textHits)

        textMisses =
            view.findViewById(R.id.textMisses)

        gameView.listener = this

        startGame()
    }

    private fun startGame() {

        score = 0
        hits = 0
        misses = 0

        updateStatistics()

        gameView.startGame(
            maxBugs = 5,
            speedMultiplier = 1f
        )
    }

    private fun updateStatistics() {

        textScore.text =
            "Очки: $score"

        textHits.text =
            "Попадания: $hits"

        textMisses.text =
            "Промахи: $misses"
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

    override fun onDestroyView() {
        gameView.stopGame()
        super.onDestroyView()
    }
}