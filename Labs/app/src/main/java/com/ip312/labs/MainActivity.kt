package com.ip312.labs

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.SeekBar
import android.widget.CalendarView
import android.widget.ArrayAdapter
import android.widget.TextView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val editName = findViewById<EditText>(R.id.editName)
        val radioGender = findViewById<RadioGroup>(R.id.radioGender)
        val spinnerCourse = findViewById<Spinner>(R.id.spinnerCourse)
        val courses = arrayOf(
            "1 курс",
            "2 курс",
            "3 курс",
            "4 курс"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            courses
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerCourse.adapter = adapter

        val textDifficulty = findViewById<TextView>(R.id.textDifficulty)
        val seekDifficulty = findViewById<SeekBar>(R.id.seekDifficulty)
        seekDifficulty.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    textDifficulty.text = "Уровень: ${progress + 1}"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
        val calendarBirth = findViewById<CalendarView>(R.id.calendarBirth)
    }
}