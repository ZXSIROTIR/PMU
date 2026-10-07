package com.ip312.labs

import android.graphics.PointF

enum class BugType(
    val baseSpeed: Float,
    val size: Float,
    val points: Int
) {
    NORMAL(
        baseSpeed = 150f,
        size = 40f,
        points = 10
    ),

    FAST(
        baseSpeed = 260f,
        size = 30f,
        points = 20
    ),

    RARE(
        baseSpeed = 100f,
        size = 52f,
        points = 50
    )
}

data class Bug(
    val id: Long,
    val position: PointF,
    val speed: Float,
    val size: Float,
    val type: BugType,
    val points: Int,
    var directionX: Float,
    var directionY: Float
)