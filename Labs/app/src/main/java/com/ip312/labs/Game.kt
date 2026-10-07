package com.ip312.labs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Game : Fragment() {

    private lateinit var gameView: Buggame

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

        gameView.startGame(
            maxBugs = 5,
            speedMultiplier = 1f
        )
    }

    override fun onDestroyView() {
        gameView.stopGame()
        super.onDestroyView()
    }
}