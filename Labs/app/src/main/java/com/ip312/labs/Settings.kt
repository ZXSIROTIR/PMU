package com.ip312.labs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class Settings : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.settings,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val seekSpeed =
            view.findViewById<SeekBar>(R.id.seekSpeed)

        val textSpeed =
            view.findViewById<TextView>(R.id.textSpeed)

        val seekCockroaches =
            view.findViewById<SeekBar>(R.id.seekCockroaches)

        val textCockroaches =
            view.findViewById<TextView>(R.id.textCockroaches)

        val seekBonus =
            view.findViewById<SeekBar>(R.id.seekBonus)

        val textBonus =
            view.findViewById<TextView>(R.id.textBonus)

        val seekRound =
            view.findViewById<SeekBar>(R.id.seekRound)

        val textRound =
            view.findViewById<TextView>(R.id.textRound)

        seekSpeed.setOnSeekBarChangeListener(
            createListener { progress ->
                textSpeed.text =
                    "Скорость игры: ${progress + 1}"
            }
        )

        seekCockroaches.setOnSeekBarChangeListener(
            createListener { progress ->
                textCockroaches.text =
                    "Максимум тараканов: ${progress + 1}"
            }
        )

        seekBonus.setOnSeekBarChangeListener(
            createListener { progress ->
                textBonus.text =
                    "Интервал бонусов: ${progress + 1} сек."
            }
        )

        seekRound.setOnSeekBarChangeListener(
            createListener { progress ->
                textRound.text =
                    "Длительность раунда: ${progress + 1} мин."
            }
        )
    }

    private fun createListener(
        action: (Int) -> Unit
    ): SeekBar.OnSeekBarChangeListener {

        return object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                action(progress)
            }

            override fun onStartTrackingTouch(
                seekBar: SeekBar?
            ) {
            }

            override fun onStopTrackingTouch(
                seekBar: SeekBar?
            ) {
            }
        }
    }
}