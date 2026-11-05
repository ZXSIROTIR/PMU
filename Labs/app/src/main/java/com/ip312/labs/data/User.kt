package com.ip312.labs.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Зарегистрированный игрок. ФИО уникально. */
@Entity(
    tableName = "users",
    indices = [Index(value = ["name"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: String,
    val zodiac: String
)
