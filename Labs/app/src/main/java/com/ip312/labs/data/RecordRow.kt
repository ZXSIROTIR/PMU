package com.ip312.labs.data

/** Строка таблицы рекордов: результат + имя игрока (JOIN records и users). */
data class RecordRow(
    val id: Long,
    val name: String,
    val score: Int,
    val difficulty: Int,
    val hits: Int,
    val misses: Int,
    val roundMinutes: Int,
    val playedAt: Long
)
