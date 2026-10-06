package com.ip312.labs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment

class Registration : Fragment() {

    data class Player(
        val name: String,
        val gender: String,
        val course: String,
        val difficulty: Int,
        val birthDate: String,
        val zodiac: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.registration,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val editName = view.findViewById<EditText>(R.id.editName)

        val radioGender =
            view.findViewById<RadioGroup>(R.id.radioGender)

        val spinnerCourse =
            view.findViewById<Spinner>(R.id.spinnerCourse)

        val courses = arrayOf(
            "1 курс",
            "2 курс",
            "3 курс",
            "4 курс"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            courses
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCourse.adapter = adapter

        val textDifficulty =
            view.findViewById<TextView>(R.id.textDifficulty)

        val seekDifficulty =
            view.findViewById<SeekBar>(R.id.seekDifficulty)

        seekDifficulty.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    textDifficulty.text =
                        "Уровень: ${progress + 1}"
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

        val calendarBirth =
            view.findViewById<CalendarView>(R.id.calendarBirth)

        var selectedDay = 3
        var selectedMonth = 9
        var selectedYear = 2004

        calendarBirth.setOnDateChangeListener {
                _, year, month, dayOfMonth ->

            selectedYear = year
            selectedMonth = month + 1
            selectedDay = dayOfMonth
        }

        val buttonRegister =
            view.findViewById<Button>(R.id.buttonRegister)

        val textResult =
            view.findViewById<TextView>(R.id.textResult)

        val imageZodiac =
            view.findViewById<ImageView>(R.id.imageZodiac)

        buttonRegister.setOnClickListener {

            val name = editName.text.toString()

            if (name.isBlank()) {
                editName.error = "Введите ФИО"
                return@setOnClickListener
            }

            val gender = when (radioGender.checkedRadioButtonId) {
                R.id.radioMale -> "Мужской"
                R.id.radioFemale -> "Женский"
                else -> "Не указан"
            }

            val course =
                spinnerCourse.selectedItem.toString()

            val difficulty =
                seekDifficulty.progress + 1

            val birthDate =
                "$selectedDay.$selectedMonth.$selectedYear"

            val zodiac =
                getZodiac(selectedDay, selectedMonth)

            val player = Player(
                name = name,
                gender = gender,
                course = course,
                difficulty = difficulty,
                birthDate = birthDate,
                zodiac = zodiac
            )

            val zodiacImage = when (player.zodiac) {
                "Овен" -> R.drawable.aries
                "Телец" -> R.drawable.taurus
                "Близнецы" -> R.drawable.gemini
                "Рак" -> R.drawable.cancer
                "Лев" -> R.drawable.leo
                "Дева" -> R.drawable.virgo
                "Весы" -> R.drawable.libra
                "Скорпион" -> R.drawable.scorpio
                "Стрелец" -> R.drawable.sagittarius
                "Козерог" -> R.drawable.capricorn
                "Водолей" -> R.drawable.aquarius
                "Рыбы" -> R.drawable.pisces
                else -> 0
            }

            if (zodiacImage != 0) {
                imageZodiac.setImageResource(zodiacImage)
            }

            textResult.text = """
                ФИО: ${player.name}
                Пол: ${player.gender}
                Курс: ${player.course}
                Уровень игры: ${player.difficulty}
                Дата рождения: ${player.birthDate}
                Знак зодиака: ${player.zodiac}
            """.trimIndent()
        }
    }

    private fun getZodiac(
        day: Int,
        month: Int
    ): String {
        return when {
            month == 1 && day >= 20 ||
                    month == 2 && day <= 18 -> "Водолей"

            month == 2 && day >= 19 ||
                    month == 3 && day <= 20 -> "Рыбы"

            month == 3 && day >= 21 ||
                    month == 4 && day <= 19 -> "Овен"

            month == 4 && day >= 20 ||
                    month == 5 && day <= 20 -> "Телец"

            month == 5 && day >= 21 ||
                    month == 6 && day <= 20 -> "Близнецы"

            month == 6 && day >= 21 ||
                    month == 7 && day <= 22 -> "Рак"

            month == 7 && day >= 23 ||
                    month == 8 && day <= 22 -> "Лев"

            month == 8 && day >= 23 ||
                    month == 9 && day <= 22 -> "Дева"

            month == 9 && day >= 23 ||
                    month == 10 && day <= 22 -> "Весы"

            month == 10 && day >= 23 ||
                    month == 11 && day <= 21 -> "Скорпион"

            month == 11 && day >= 22 ||
                    month == 12 && day <= 21 -> "Стрелец"

            else -> "Козерог"
        }
    }
}