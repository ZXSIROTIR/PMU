package com.ip312.labs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class Settings : Fragment() {

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
            view.findViewById<SeekBar>(
                R.id.seekCockroaches
            )

        val textCockroaches =
            view.findViewById<TextView>(
                R.id.textCockroaches
            )

        val seekBonus =
            view.findViewById<SeekBar>(
                R.id.seekBonus
            )

        val textBonus =
            view.findViewById<TextView>(
                R.id.textBonus
            )

        val seekRound =
            view.findViewById<SeekBar>(
                R.id.seekRound
            )

        val textRound =
            view.findViewById<TextView>(
                R.id.textRound
            )

        seekSpeed.progress =
            prefs.getInt("speed", 1)

        seekCockroaches.progress =
            prefs.getInt("cockroaches", 4)

        seekBonus.progress =
            prefs.getInt("bonus", 9)

        seekRound.progress =
            prefs.getInt("round", 4)

        textSpeed.text =
            "Скорость игры: ${seekSpeed.progress + 1}"

        textCockroaches.text =
            "Максимум тараканов: ${seekCockroaches.progress + 1}"

        textBonus.text =
            "Интервал бонусов: ${seekBonus.progress + 1} сек."

        textRound.text =
            "Длительность раунда: ${seekRound.progress + 1} мин."

        seekSpeed.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    textSpeed.text =
                        "Скорость игры: ${progress + 1}"

                    prefs.edit()
                        .putInt("speed", progress)
                        .apply()
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
        )

        seekCockroaches.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    textCockroaches.text =
                        "Максимум тараканов: ${progress + 1}"

                    prefs.edit()
                        .putInt("cockroaches", progress)
                        .apply()
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
        )

        seekBonus.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    textBonus.text =
                        "Интервал бонусов: ${progress + 1} сек."

                    prefs.edit()
                        .putInt("bonus", progress)
                        .apply()
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
        )

        seekRound.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    textRound.text =
                        "Длительность раунда: ${progress + 1} мин."

                    prefs.edit()
                        .putInt("round", progress)
                        .apply()
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
        )
    }
}