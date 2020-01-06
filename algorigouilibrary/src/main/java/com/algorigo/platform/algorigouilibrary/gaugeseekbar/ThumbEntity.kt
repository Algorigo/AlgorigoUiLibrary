package com.algorigo.newsmartchair.ui.custom.gaugeseekbar

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

internal class ThumbEntity(
    private val centerPosition: PointF,
    private var progress: Float,
    private val startAngle: Float,
    private var thumbDrawable: Drawable?,
    private val thumbRadius: Float,
    @ColorInt private var thumbColor: Int
) {

    companion object {
        private const val DEGREE_TO_RADIAN_RATIO = 0.0174533
    }

    private class ThumbDrawable(thumbColor: Int) : Drawable() {
        private val whitePaint = Paint().apply {
            color = Color.WHITE
            alpha = 255
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        private val thumbInnerPaint = Paint().apply {
            isAntiAlias = true
            color = thumbColor
        }

        override fun draw(canvas: Canvas) {
            val centerX = bounds.exactCenterX()
            val centerY = bounds.exactCenterY()
            val radius = centerX - bounds.left

            canvas.apply {
                //            drawCircle(centerX, centerY, radius, thumbOuterPaint)
                drawCircle(centerX, centerY, radius / 2f, thumbInnerPaint)
                drawCircle(centerX, centerY, radius / 5f, whitePaint)
            }
        }

        override fun setAlpha(alpha: Int) {}

        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

        override fun setColorFilter(colorFilter: ColorFilter?) {}

        fun setThumbColor(@ColorInt color: Int) {
            thumbInnerPaint.color = color
        }
    }

    init {
        if (thumbDrawable == null) {
            thumbDrawable = ThumbDrawable(thumbColor)
        }
        layoutDrawable(progress)
    }

    private fun layoutDrawable(progress: Float) {
        val seekbarRadius = min(centerPosition.x, centerPosition.y) - thumbRadius

        val angle = (startAngle + (360 - 2 * startAngle) * progress) * DEGREE_TO_RADIAN_RATIO

        val indicatorX = centerPosition.x - sin(angle) * seekbarRadius
        val indicatorY = cos(angle) * seekbarRadius + centerPosition.y

//        val tmp = trophyIndicatorX -centerPosition.x

        thumbDrawable?.setBounds(
            (indicatorX - thumbRadius).toInt(),
            (indicatorY - thumbRadius).toInt(),
            (indicatorX + thumbRadius).toInt(),
            (indicatorY + thumbRadius).toInt()
        )
    }

    fun changeThumb(thumbDrawable: Drawable?) {
        if (thumbDrawable != null) {
            this.thumbDrawable = thumbDrawable
        } else {
            this.thumbDrawable = ThumbDrawable(thumbColor)
        }
    }

    fun changeThumbColor(@ColorInt color: Int) {
        thumbColor = color
        if (thumbDrawable is ThumbDrawable) {
            (thumbDrawable as? ThumbDrawable)?.setThumbColor(color)
        }
    }

    fun draw(canvas: Canvas, progress: Float) {
        this.progress = progress
        layoutDrawable(progress)

        thumbDrawable?.draw(canvas)
    }
}