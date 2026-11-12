package com.ip312.labs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

interface GameListener {

    fun onBugHit(points: Int)

    fun onMiss(penalty: Int)

    fun onTimeChanged(seconds: Long)

    fun onGameFinished()

    fun onBonusCollected()
 
    fun onBonusTick(secondsLeft: Long)
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

    private val skins: Map<BugType, Drawable?> = mapOf(
        BugType.NORMAL to ContextCompat.getDrawable(context, R.drawable.bug_normal),
        BugType.FAST to ContextCompat.getDrawable(context, R.drawable.bug_fast),
        BugType.RARE to ContextCompat.getDrawable(context, R.drawable.bug_rare)
    )

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

    private var bonusIntervalMs = 15_000L

    private var useSkins = true

    private class Bonus(
        val position: PointF,
        val size: Float,
        val spawnTime: Long
    )

    private var bonus: Bonus? = null
    private var lastBonusTime = 0L

    private var tiltUntil = 0L
    private var lastTiltSeconds = 0L


    private var tiltX = 0f
    private var tiltY = 0f

    private val bonusPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bonusRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    private companion object {
        const val BONUS_SIZE = 48f
        const val BONUS_LIFETIME_MS = 5_000L

        const val SKIN_SCALE = 0.9f

        const val TILT_DURATION_MS = 10_000L

        const val TILT_ACCEL = 60f

        const val TILT_MAX_DRIFT = 600f

        const val TILT_FULL = 4f
        const val TILT_MIN_SELF_FACTOR = 0.25f

        const val DRIFT_DECAY = 3f
    }

    fun startGame(
        durationMs: Long,
        maxBugs: Int,
        speedMultiplier: Float,
        sizeMultiplier: Float,
        bonusIntervalMs: Long = 15_000L,
        useSkins: Boolean = true
    ) {
        this.durationMs = durationMs
        this.maxBugs = maxBugs
        this.speedMultiplier = speedMultiplier
        this.sizeMultiplier = sizeMultiplier
        this.bonusIntervalMs = bonusIntervalMs
        this.useSkins = useSkins

        bugs.clear()

        nextId = 1L
        running = true

        startTime = System.currentTimeMillis()
        lastFrameTime = startTime
        lastSpawnTime = startTime

        bonus = null
        lastBonusTime = startTime

        tiltUntil = 0L
        notifyBonusTick(startTime)

        repeat(minOf(3, maxBugs)) {
            createBug()
        }

        invalidate()
    }

    fun setTilt(gravityX: Float, gravityY: Float) {
        tiltX = gravityX
        tiltY = gravityY
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

    private fun createBonus(now: Long) {

        if (width <= 0 || height <= 0) {
            return
        }

        val size = BONUS_SIZE
        val margin = size * 1.5f

        val x = margin +
                random.nextFloat() *
                (width - margin * 2)
                    .coerceAtLeast(1f)

        val y = margin +
                random.nextFloat() *
                (height - margin * 2)
                    .coerceAtLeast(1f)

        bonus = Bonus(PointF(x, y), size, now)
    }

    private fun updateBonus(now: Long) {

        val current = bonus
        val lifetime = minOf(BONUS_LIFETIME_MS, bonusIntervalMs)

        if (current != null && now - current.spawnTime >= lifetime) {
            bonus = null
        }

        if (now - lastBonusTime >= bonusIntervalMs) {
            lastBonusTime = now
            createBonus(now)
        }
    }

    private fun drawBonus(
        canvas: Canvas,
        bonus: Bonus,
        now: Long
    ) {
        val lifetime = minOf(BONUS_LIFETIME_MS, bonusIntervalMs)
        val remaining = lifetime - (now - bonus.spawnTime)

        if (remaining < 1500L && (now / 150L) % 2L == 0L) {
            return
        }

        val pulse = 1f + 0.12f * sin(now / 150f)
        val radius = bonus.size * pulse

        val x = bonus.position.x
        val y = bonus.position.y

        val lineColor =
            if (isDarkField) Color.WHITE else Color.BLACK

        bonusPaint.color = Color.rgb(46, 204, 113)

        canvas.drawCircle(x, y, radius, bonusPaint)

        bonusRingPaint.color = lineColor
        canvas.drawCircle(x, y, radius, bonusRingPaint)

        detailPaint.color = lineColor
        detailPaint.style = Paint.Style.STROKE
        detailPaint.strokeWidth = 6f

        val r = radius * 0.5f

        canvas.drawLine(x - r, y - r, x + r, y + r, detailPaint)
        canvas.drawLine(x + r, y + r, x + r * 0.1f, y + r, detailPaint)
        canvas.drawLine(x + r, y + r, x + r, y + r * 0.1f, detailPaint)
    }

    private fun notifyBonusTick(now: Long) {

        val seconds =
            if (tiltUntil > now) ceil((tiltUntil - now) / 1000.0).toLong()
            else 0L

        if (seconds != lastTiltSeconds) {
            lastTiltSeconds = seconds
            listener?.onBonusTick(seconds)
        }
    }

    private fun updateBugs(
        deltaTime: Float,
        tiltActive: Boolean
    ) {

        val tiltStrength =
            (sqrt(tiltX * tiltX + tiltY * tiltY) / TILT_FULL)
                .coerceIn(0f, 1f)

        val selfFactor =
            if (tiltActive) {
                1f - (1f - TILT_MIN_SELF_FACTOR) * tiltStrength
            } else {
                1f
            }

        for (bug in bugs) {

            if (tiltActive) {

                bug.driftX += tiltX * TILT_ACCEL * deltaTime
                bug.driftY += tiltY * TILT_ACCEL * deltaTime

                val drift = sqrt(
                    bug.driftX * bug.driftX +
                            bug.driftY * bug.driftY
                )

                if (drift > TILT_MAX_DRIFT) {
                    val k = TILT_MAX_DRIFT / drift
                    bug.driftX *= k
                    bug.driftY *= k
                }

            } else {

                val k = (1f - DRIFT_DECAY * deltaTime).coerceAtLeast(0f)
                bug.driftX *= k
                bug.driftY *= k
            }

            bug.position.x +=
                (bug.directionX * bug.speed * selfFactor + bug.driftX) *
                        deltaTime

            bug.position.y +=
                (bug.directionY * bug.speed * selfFactor + bug.driftY) *
                        deltaTime

            if (bug.position.x < bug.size) {
                bug.position.x = bug.size
                bug.directionX = abs(bug.directionX)
                if (bug.driftX < 0f) bug.driftX = 0f
            }

            if (bug.position.x > width - bug.size) {
                bug.position.x = width - bug.size
                bug.directionX = -abs(bug.directionX)
                if (bug.driftX > 0f) bug.driftX = 0f
            }

            if (bug.position.y < bug.size) {
                bug.position.y = bug.size
                bug.directionY = abs(bug.directionY)
                if (bug.driftY < 0f) bug.driftY = 0f
            }

            if (bug.position.y > height - bug.size) {
                bug.position.y = height - bug.size
                bug.directionY = -abs(bug.directionY)
                if (bug.driftY > 0f) bug.driftY = 0f
            }
        }
    }

    private fun drawSkin(
        canvas: Canvas,
        bug: Bug,
        skin: Drawable
    ) {
        val x = bug.position.x
        val y = bug.position.y

        val half = (bug.size * SKIN_SCALE).toInt()

        val velocityX = bug.directionX * bug.speed + bug.driftX
        val velocityY = bug.directionY * bug.speed + bug.driftY

        val angle =
            atan2(velocityY, velocityX) * 180f / Math.PI.toFloat() + 90f

        canvas.save()
        canvas.rotate(angle, x, y)

        skin.setBounds(
            (x - half).toInt(),
            (y - half).toInt(),
            (x + half).toInt(),
            (y + half).toInt()
        )
        skin.draw(canvas)

        canvas.restore()
    }

    private fun drawBug(
        canvas: Canvas,
        bug: Bug
    ) {
        val skin = if (useSkins) skins[bug.type] else null

        if (skin != null) {
            drawSkin(canvas, bug, skin)
            return
        }

        val x = bug.position.x
        val y = bug.position.y

        bodyPaint.color = when (bug.type) {
            BugType.NORMAL ->
                if (isDarkField) Color.rgb(210, 210, 210)
                else Color.rgb(60, 60, 60)

            BugType.FAST ->
                if (isDarkField) Color.rgb(255, 0, 0)
                else Color.rgb(255, 0, 0)

            BugType.RARE ->
                if (isDarkField) Color.rgb(250, 255, 0)
                else Color.rgb(250, 255, 0)
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

            val touchedBonus = bonus

            if (touchedBonus != null) {

                val bdx = touchX - touchedBonus.position.x
                val bdy = touchY - touchedBonus.position.y

                if (sqrt(bdx * bdx + bdy * bdy) <= touchedBonus.size * 1.3f) {
                    bonus = null
                    tiltUntil = System.currentTimeMillis() + TILT_DURATION_MS
                    listener?.onBonusCollected()
                    invalidate()
                    return true
                }
            }

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
                bonus = null
                tiltUntil = 0L
                notifyBonusTick(currentTime)
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

            val tiltActive = tiltUntil > currentTime

            notifyBonusTick(currentTime)
            updateBugs(deltaTime, tiltActive)

            if (currentTime - lastSpawnTime >= 1000L) {
                createBug()
                lastSpawnTime = currentTime
            }

            updateBonus(currentTime)
        }

        for (bug in bugs) {
            drawBug(canvas, bug)
        }

        bonus?.let { drawBonus(canvas, it, currentTime) }

        if (running) {
            postInvalidateOnAnimation()
        }
    }
}