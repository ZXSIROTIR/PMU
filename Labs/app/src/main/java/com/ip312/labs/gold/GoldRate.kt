package com.ip312.labs.gold

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

data class GoldRate(
    val rubPerGram: Double,
    val date: String
) {

    fun formattedRate(): String =
        NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU"))
            .apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }
            .format(rubPerGram)

    fun points(): Int =
        (rubPerGram * POINTS_PER_RUB).roundToInt().coerceAtLeast(1)

    companion object {
        const val POINTS_PER_RUB = 0.01
    }
}
