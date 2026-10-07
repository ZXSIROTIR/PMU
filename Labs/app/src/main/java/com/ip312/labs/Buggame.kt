package com.ip312.labs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Buggame @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val bugs = mutableListOf<Bug>()

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val random = Random.Default

    private var nextId = 1L
    private var running = false

    private var lastFrameTime = 0L
    private var lastSpawnTime = 0L

    private var maxBugs = 5
    private var speedMultiplier = 1f

    fun startGame(
        maxBugs: Int,
        speedMultiplier: Float
    ) {
        this.maxBugs = maxBugs
        this.speedMultiplier = speedMultiplier

        bugs.clear()

        nextId = 1L
        running = true

        lastFrameTime = System.currentTimeMillis()
        lastSpawnTime = lastFrameTime

        repeat(minOf(3, maxBugs)) {
            createBug()
        }

        invalidate()
    }

    fun stopGame() {
        running = false
    }

    private fun createBug() {

        if (!running || bugs.size >= maxBugs) {
            return
        }

        if (width <= 0 || height <= 0) {
            return
        }

        val type = when (random.nextInt(100)) {
            in 0..59 -> BugType.NORMAL
            in 60..89 -> BugType.FAST
            else -> BugType.RARE
        }

        val margin = type.size

        val x = margin +
                random.nextFloat() *
                (width - margin * 2)
                    .coerceAtLeast(1f)

        val y = margin +
                random.nextFloat() *
                (height - margin * 2)
                    .coerceAtLeast(1f)

        val angle = random.nextFloat() *
                Math.PI.toFloat() * 2f

        val directionX = cos(angle)
        val directionY = sin(angle)

        val bug = Bug(
            id = nextId++,
            position = PointF(x, y),
            speed = type.baseSpeed * speedMultiplier,
            size = type.size,
            type = type,
            points = type.points,
            directionX = directionX,
            directionY = directionY
        )

        bugs.add(bug)
    }

    private fun updateBugs(deltaTime: Float) {

        for (bug in bugs) {

            bug.position.x +=
                bug.directionX *
                        bug.speed *
                        deltaTime

            bug.position.y +=
                bug.directionY *
                        bug.speed *
                        deltaTime

            if (bug.position.x < bug.size) {
                bug.position.x = bug.size
                bug.directionX *= -1
            }

            if (bug.position.x > width - bug.size) {
                bug.position.x =
                    width - bug.size

                bug.directionX *= -1
            }

            if (bug.position.y < bug.size) {
                bug.position.y = bug.size
                bug.directionY *= -1
            }

            if (bug.position.y > height - bug.size) {
                bug.position.y =
                    height - bug.size

                bug.directionY *= -1
            }
        }
    }

    private fun drawBug(
        canvas: Canvas,
        bug: Bug
    ) {
        val x = bug.position.x
        val y = bug.position.y

        bodyPaint.color = when (bug.type) {
            BugType.NORMAL -> Color.rgb(60, 60, 60)
            BugType.FAST -> Color.rgb(40, 90, 200)
            BugType.RARE -> Color.rgb(180, 70, 180)
        }

        canvas.drawCircle(
            x,
            y,
            bug.size * 0.55f,
            bodyPaint
        )

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.WHITE)

        val currentTime = System.currentTimeMillis()

        if (running) {

            val deltaTime =
                if (lastFrameTime == 0L) {
                    0f
                } else {
                    (currentTime - lastFrameTime) / 1000f
                }

            lastFrameTime = currentTime

            updateBugs(deltaTime)

            if (
                currentTime - lastSpawnTime >= 1000L
            ) {
                createBug()
                lastSpawnTime = currentTime
            }
        }

        for (bug in bugs) {
            drawBug(canvas, bug)
        }

        if (running) {
            postInvalidateOnAnimation()
        }
    }
}