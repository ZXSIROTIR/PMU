package com.ip312.labs.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Результат одной сыгранной партии. */
@Entity(
    tableName = "records",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true), Index("score")]
)
data class GameRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val score: Int,
    val hits: Int,
    val misses: Int,
    /** Уровень сложности игрока (1..10) на момент партии */
    val difficulty: Int,
    val roundMinutes: Int,
    /** Время окончания партии, мс since epoch */
    val playedAt: Long
)
