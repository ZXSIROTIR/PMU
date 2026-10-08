package com.ip312.labs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

interface GameListener {

    fun onBugHit(points: Int)

    fun onMiss(penalty: Int)

    fun onTimeChanged(seconds: Long)

    fun onGameFinished()
}

class Buggame @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var listener: GameListener? = null

    private var durationMs = 60_000L
    private var startTime = 0L

    private val bugs = mutableListOf<Bug>()

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Цвет фона поля берётся из текущей темы
    private val fieldColor: Int = run {
        val value = TypedValue()
        if (context.theme.resolveAttribute(
                android.R.attr.colorBackground,
                value,
                true
            )
        ) {
            value.data
        } else {
            Color.WHITE
        }
    }

    private val isDarkField: Boolean =
        ColorUtils.calculateLuminance(fieldColor) < 0.5

    private val random = Random.Default

    private var nextId = 1L
    private var running = false

    private var lastFrameTime = 0L
    private var lastSpawnTime = 0L

    private var maxBugs = 5
    private var speedMultiplier = 1f
    private var sizeMultiplier = 1f

    fun startGame(
        durationMs: Long,
        maxBugs: Int,
        speedMultiplier: Float,
        sizeMultiplier: Float
    ) {
        this.durationMs = durationMs
        this.maxBugs = maxBugs
        this.speedMultiplier = speedMultiplier
        this.sizeMultiplier = sizeMultiplier

        bugs.clear()

        nextId = 1L
        running = true

        startTime = System.currentTimeMillis()
        lastFrameTime = startTime
        lastSpawnTime = startTime

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

        val bugSize = type.size * sizeMultiplier

        val margin = bugSize

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
            size = bugSize,
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
            BugType.NORMAL ->
                if (isDarkField) Color.rgb(210, 210, 210)
                else Color.rgb(60, 60, 60)

            BugType.FAST ->
                if (isDarkField) Color.rgb(100, 160, 255)
                else Color.rgb(40, 90, 200)

            BugType.RARE ->
                if (isDarkField) Color.rgb(230, 120, 230)
                else Color.rgb(180, 70, 180)
        }

        canvas.drawCircle(
            x,
            y,
            bug.size * 0.55f,
            bodyPaint
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (!running) {
            return true
        }

        if (event.action == MotionEvent.ACTION_DOWN) {

            val touchX = event.x
            val touchY = event.y

            var hitBug: Bug? = null

            for (i in bugs.indices.reversed()) {

                val bug = bugs[i]

                val dx = touchX - bug.position.x
                val dy = touchY - bug.position.y

                val distance = sqrt(dx * dx + dy * dy)

                if (distance <= bug.size) {
                    hitBug = bug
                    break
                }
            }

            if (hitBug != null) {

                bugs.remove(hitBug)

                listener?.onBugHit(hitBug.points)

            } else {

                listener?.onMiss(5)
            }

            invalidate()
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(fieldColor)

        val currentTime = System.currentTimeMillis()

        if (running) {

            val elapsed = currentTime - startTime
            val remaining = durationMs - elapsed
            val seconds = (remaining / 1000L).coerceAtLeast(0L)

            listener?.onTimeChanged(seconds)

            if (remaining <= 0L) {
                running = false
                listener?.onGameFinished()
                invalidate()
                return
            }

            val deltaTime =
                if (lastFrameTime == 0L) {
                    0f
                } else {
                    (currentTime - lastFrameTime) / 1000f
                }

            lastFrameTime = currentTime

            updateBugs(deltaTime)

            if (currentTime - lastSpawnTime >= 1000L) {
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