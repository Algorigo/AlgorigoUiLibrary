package com.algorigo.platform.algorigouilibrary.gaugeseekbar

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.drawable.Drawable
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

internal class GoalEntity(
    private val centerPosition: PointF,
    private val startAngle: Float,
    private var goalDrawable: Drawable?,
    private var activeGoalDrawable: Drawable?,
    private var goalAngle: Float,
    private var goalMargin: Float
) {

    companion object {
        private const val DEGREE_TO_RADIAN_RATIO = 0.0174533
    }

    private var active = false
    private var drawable: Drawable? = null

    init {
        layoutDrawables()
        drawable = goalDrawable
    }

    private fun layoutDrawables() {
        val seekbarRadius = min(centerPosition.x, centerPosition.y) - goalMargin

        val trophyAngle = (startAngle + (360 - 2 * startAngle) * goalAngle) * DEGREE_TO_RADIAN_RATIO

        val trophyIndicatorX = centerPosition.x - sin(trophyAngle) * seekbarRadius
        val trophyIndicatorY = cos(trophyAngle) * seekbarRadius + centerPosition.y

        goalDrawable?.let {
            it.setBounds(
                (trophyIndicatorX - it.minimumWidth).roundToInt(),
                (trophyIndicatorY + goalMargin / 2).roundToInt(),
                (trophyIndicatorX).roundToInt(),
                (trophyIndicatorY + goalMargin / 2 + it.minimumHeight).roundToInt()
            )
        }
        activeGoalDrawable?.let {
            it.setBounds(
                (trophyIndicatorX - it.minimumWidth).roundToInt(),
                (trophyIndicatorY + goalMargin / 2).roundToInt(),
                (trophyIndicatorX).roundToInt(),
                (trophyIndicatorY + goalMargin / 2 + it.minimumHeight).roundToInt()
            )
        }
    }

    fun changeTrophy(trophyDrawable: Drawable?, activeTrophyDrawable: Drawable?) {
        this.goalDrawable = trophyDrawable
        this.activeGoalDrawable = activeTrophyDrawable
        if (active) drawable = activeTrophyDrawable else drawable = trophyDrawable
    }

    fun changeTrophyAngle(trophyAngle: Float) {
        this.goalAngle = trophyAngle
        layoutDrawables()
    }

    fun setActive(active: Boolean) {
        this.active = active
        if (active) drawable = activeGoalDrawable else drawable = goalDrawable
    }

    fun isActive(): Boolean {
        return active
    }

    fun draw(canvas: Canvas) {
        drawable?.draw(canvas)
    }

}