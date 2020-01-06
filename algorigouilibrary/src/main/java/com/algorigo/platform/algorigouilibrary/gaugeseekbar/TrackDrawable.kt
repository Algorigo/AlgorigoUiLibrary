package com.algorigo.platform.algorigouilibrary.gaugeseekbar

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

internal class TrackDrawable(
        private val centerPosition: PointF,
        private val radiusPx: Float,
        private val margin: Float,
        private val startAngle: Float,
        private val trackWidthPx: Float,
        private val trackSplit: Int,
        @ColorInt trackColor: Int
): Drawable() {

    private val trackPaint = Paint().apply {
        strokeWidth = trackWidthPx
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        color = trackColor
    }

//    private fun createSweepGradient(): SweepGradient {
//        val shader = SweepGradient(centerPosition.x, centerPosition.y, gradientArray, getGradientPositions())
//        val gradientRotationMatrix = Matrix()
//        gradientRotationMatrix.preRotate(90f + startAngle - 5, centerPosition.x, centerPosition.y)
//        shader.setLocalMatrix(gradientRotationMatrix)
//        return shader
//    }
//
//    private fun getGradientPositions(): FloatArray {
//        val normalizedStartAngle = startAngle / 360f
//        val normalizedAvailableSpace = 1f - 2 * normalizedStartAngle
//
//        return FloatArray(gradientArray.size) {
//            normalizedStartAngle + normalizedAvailableSpace * gradientPositionsArray[it]
//        }
//    }

    override fun draw(canvas: Canvas) {
        val angle = (360 - (startAngle * 2))
        val rect = RectF(
                centerPosition.x - radiusPx + margin,
                centerPosition.y - radiusPx + margin,
                centerPosition.x + radiusPx - margin,
                centerPosition.y + radiusPx - margin
        )

        val splitNumber = trackSplit
        val spaceNumber = splitNumber - 1
        val spaceWidth = 8

        if (splitNumber == 0) {
            canvas.drawArc(
                    rect,
                    90f + startAngle,
                    angle,
                    false,
                    trackPaint
            )
        } else {
            for (i in 0..spaceNumber) {
                canvas.drawArc(
                        rect,
                        90f + startAngle + (angle - spaceNumber * spaceWidth) / splitNumber * i + spaceWidth * i,
                        (angle - spaceNumber * spaceWidth) / splitNumber,
                        false,
                        trackPaint
                )
            }
        }
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    fun setTrackColor(@ColorInt color: Int) {
        trackPaint.color = color
    }

    fun setTrackWidth(width: Float) {
        trackPaint.strokeWidth = width
    }
}