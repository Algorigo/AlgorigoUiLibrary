package com.algorigo.newsmartchair.ui.custom.gaugeseekbar

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

internal class ProgressDrawable(
    private var centerPosition: PointF,
    private var progressRatio: Float,
    private val radiusPx: Float,
    private val margin: Float,
    private val startAngle: Float,
    private val trackWidthPx: Float,
    @ColorInt progressColor: Int
) : Drawable() {

    private val progressPaint = Paint().apply {
        strokeWidth = trackWidthPx
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        color = progressColor
    }

    fun draw(canvas: Canvas, ratio: Float) {
        this.progressRatio = ratio
        draw(canvas)
    }

    override fun draw(canvas: Canvas) {
        val angle = ((360 - (startAngle * 2)) * progressRatio)
        if (angle > 0) {
            val rect = RectF(
                    centerPosition.x - radiusPx + margin,
                    centerPosition.y - radiusPx + margin,
                    centerPosition.x + radiusPx - margin,
                    centerPosition.y + radiusPx - margin
            )
            canvas.drawArc(
                    rect,
                    90f + startAngle,
                    angle,
                    false,
                    progressPaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    fun setProgressColor(@ColorInt color: Int) {
        progressPaint.color = color
    }

    fun setProgressWidth(width: Float) {
        progressPaint.strokeWidth = width
    }
}