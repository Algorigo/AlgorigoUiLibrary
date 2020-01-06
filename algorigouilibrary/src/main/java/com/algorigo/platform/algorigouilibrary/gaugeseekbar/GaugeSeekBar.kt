package com.algorigo.newsmartchair.ui.custom.gaugeseekbar

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.algorigo.platform.algorigouilibrary.R
import com.algorigo.platform.algorigouilibrary.gaugeseekbar.GoalEntity
import com.algorigo.platform.algorigouilibrary.gaugeseekbar.TrackDrawable
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class GaugeSeekBar : View {

    private companion object {
        private const val DEFAULT_START_ANGLE_DEG = 30f
        private const val DEFAULT_THUMB_RADIUS_DP = 11
        private const val DEFAULT_TRACK_WIDTH_DP = 8
    }

    interface GaugeSeekBarListener {
        fun onProgressChanged(gaugeSeekBar: GaugeSeekBar, progress: Int)
    }

    private lateinit var trackDrawable: TrackDrawable
    private lateinit var progressDrawable: ProgressDrawable
    private lateinit var thumbEntity: ThumbEntity
    private lateinit var goalEntity: GoalEntity

    private var startAngle = DEFAULT_START_ANGLE_DEG

    private var trackWidth = DEFAULT_TRACK_WIDTH_DP * resources.displayMetrics.density
    @ColorInt
    private var trackColor: Int = ContextCompat.getColor(context, R.color.light_sky_blue)
    private var trackSplit: Int = 0

    private var showProgress: Boolean = true
    private var progressWidth = DEFAULT_TRACK_WIDTH_DP * resources.displayMetrics.density
    @ColorInt
    private var progressColor = ContextCompat.getColor(context, R.color.sky_blue)
    private var useProgressColors = true
    private var progressColorArray = intArrayOf()
    private var progressLimitArray = intArrayOf()
    private var progressAnimation = true
    private var progressAnimationDurationMs = 1000

    private var showThumb: Boolean = true
    private var thumbDrawable: Drawable? = null
    private var thumbRadius = DEFAULT_THUMB_RADIUS_DP * resources.displayMetrics.density

    private var showGoal: Boolean = false
    @DrawableRes
    private var goalDrawableId: Int = 0
    @DrawableRes
    private var activeGoalDrawableId: Int = 0
    private var goalValue: Int = 60

    private var minValue = 0
        set(value) {
            field = value
            calculateProgressRatio()
        }

    private var maxValue = 100
        set(value) {
            field = value
            calculateProgressRatio()
        }
    private var progressValue = 0
        set(value) {
            field = value
            calculateProgressRatio()
        }

    private var progressRatio = 0f

    var gaugeSeekBarListener: GaugeSeekBarListener? = null

//    private val paint = Paint().apply {
//        color = Color.BLACK
//        textSize = 24f
//    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyAttributes(context.obtainStyledAttributes(attrs, R.styleable.GaugeSeekBar, 0, 0))
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        applyAttributes(context.obtainStyledAttributes(attrs, R.styleable.GaugeSeekBar, 0, 0))
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        isEnabled = false
    }

    private fun applyAttributes(attributes: TypedArray) {
        try {
            startAngle = attributes.getFloat(R.styleable.GaugeSeekBar_startAngleDegrees, startAngle)

            minValue = attributes.getInteger(R.styleable.GaugeSeekBar_min, 0)
            maxValue = attributes.getInteger(R.styleable.GaugeSeekBar_max, 100)
            progressValue = attributes.getInteger(R.styleable.GaugeSeekBar_progress, 0)

            trackWidth = attributes.getDimension(R.styleable.GaugeSeekBar_trackWidth, trackWidth)
            trackColor = attributes.getColor(R.styleable.GaugeSeekBar_trackColor, trackColor)
            trackSplit = attributes.getInt(R.styleable.GaugeSeekBar_trackSplit, trackSplit)

            showProgress = attributes.getBoolean(R.styleable.GaugeSeekBar_showProgress, showProgress)
            progressWidth = attributes.getDimension(R.styleable.GaugeSeekBar_progressWidth, progressWidth)
            progressColor = attributes.getColor(R.styleable.GaugeSeekBar_progressColor, ContextCompat.getColor(context, R.color.sky_blue))
            useProgressColors = attributes.getBoolean(R.styleable.GaugeSeekBar_useProgressColors, true)
            val progressColorArrayId = attributes.getResourceId(R.styleable.GaugeSeekBar_progressColorArray, R.array.gauge_colors_array)
            progressColorArray = resources.getIntArray(progressColorArrayId)
            val progressLimitArrayId = attributes.getResourceId(R.styleable.GaugeSeekBar_progressLimitArray, R.array.gauge_value_array)
            progressLimitArray = resources.getIntArray(progressLimitArrayId)
            progressAnimation = attributes.getBoolean(R.styleable.GaugeSeekBar_progressAnimation, true)
            progressAnimationDurationMs = attributes.getInteger(R.styleable.GaugeSeekBar_progressAnimationDurationMs, 1000)

            showThumb = attributes.getBoolean(R.styleable.GaugeSeekBar_showThumb, showThumb)
            thumbDrawable = attributes.getDrawable(R.styleable.GaugeSeekBar_thumbDrawable)
            thumbRadius = attributes.getDimension(R.styleable.GaugeSeekBar_thumbRadius, thumbRadius)

            showGoal = attributes.getBoolean(R.styleable.GaugeSeekBar_showGoal, showGoal)
            goalDrawableId = attributes.getResourceId(R.styleable.GaugeSeekBar_goalDrawable, R.drawable.ic_goal_default)
            activeGoalDrawableId = attributes.getResourceId(R.styleable.GaugeSeekBar_activeGoalDrawable, R.drawable.ic_goal_active)
            goalValue = attributes.getResourceId(R.styleable.GaugeSeekBar_goal, 60)
        } finally {
            attributes.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        initComponents()
    }

    private fun initComponents() {
        val centerX = width / 2f
        val centerY = height / 2f
        val centerPosition = PointF(centerX, centerY)
        val radiusPx = min(centerX, centerY)
        val margin = max(thumbRadius, trackWidth / 2f)

        trackDrawable = TrackDrawable(centerPosition, radiusPx, margin, startAngle, trackWidth, trackSplit, trackColor)

        val color = getProgressColorByLimit(progressValue)

        progressDrawable = ProgressDrawable(centerPosition, progressRatio, radiusPx, margin, startAngle, progressWidth, color)

        thumbEntity = ThumbEntity(centerPosition, progressRatio, startAngle,
            thumbDrawable,
            thumbRadius,
            color)

        val ratioGoalThreshold = (goalValue-minValue).toFloat()/(maxValue-minValue)

        goalEntity = GoalEntity(centerPosition, startAngle,
            ContextCompat.getDrawable(context, goalDrawableId),
            ContextCompat.getDrawable(context, activeGoalDrawableId),
            ratioGoalThreshold, thumbRadius)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        trackDrawable.draw(canvas)
        if (showProgress) {
            progressDrawable.draw(canvas, progressRatio)
        }
        if (showThumb) {
            thumbEntity.draw(canvas, progressRatio)
        }
        if (showGoal) {
            goalEntity.draw(canvas)
        }
//        drawText("0", progressWidth*2 + 60, height.toFloat() / 2 - 90, paint)
//        drawText("100", width - progressWidth*3 - 60, height.toFloat() / 2 - 90, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    performClick()
                    return handleMotionEvent(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!progressAnimation) {
                        return handleMotionEvent(event)
                    }
                }
                else -> {}
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleMotionEvent(event: MotionEvent): Boolean {
        val relativeX = measuredWidth / 2f - event.x
        val relativeY = event.y - measuredHeight / 2f
        val angle = Math.toDegrees(atan2(relativeX.toDouble(), relativeY.toDouble()))
        val ratio = angleToProgressRatio(if (angle > 0) angle else angle + 360f)
        setProgress((ratio*(maxValue-minValue)).roundToInt()+minValue)
        gaugeSeekBarListener?.onProgressChanged(this, progressValue)
        return true
    }

    private fun angleToProgressRatio(angle: Double): Float {
        val availableAngle = 360 - 2 * startAngle
        val relativeAngle = angle - startAngle
        return (relativeAngle / availableAngle).toFloat()
    }

    fun setTrackWidth(trackWidth: Float) {
        this.trackWidth = trackWidth
        trackDrawable.setTrackWidth(trackWidth)
        invalidate()
    }

    fun setTrackColor(@ColorInt color: Int) {
        trackColor = color
        trackDrawable.setTrackColor(color)
        invalidate()
    }

    fun setShowProgress(showProgress: Boolean) {
        this.showProgress = showProgress
        invalidate()
    }

    fun isShowProgress() = showProgress

    fun setProgressWidth(progressWidth: Float) {
        this.progressWidth = progressWidth
        progressDrawable.setProgressWidth(progressWidth)
        invalidate()
    }

    fun setProgressColor(@ColorInt color: Int) {
        progressColor = color
        useProgressColors = false
        progressDrawable.setProgressColor(color)
        thumbEntity.changeThumbColor(color)
        invalidate()
    }

    fun setProgressColors(progressColorArray: IntArray, progressLimitArray: IntArray) {
        if (progressColorArray.size != progressLimitArray.size+1) {
            throw IllegalStateException("arrays size is wrong")
        }
        useProgressColors = true
        this.progressColorArray = progressColorArray
        this.progressLimitArray = progressLimitArray

        val color = getProgressColorByLimit(progressValue)
        progressDrawable.setProgressColor(color)
        thumbEntity.changeThumbColor(color)
        invalidate()
    }

    fun setShowThumb(showThumb: Boolean) {
        this.showThumb = showThumb
        invalidate()
    }

    fun setThumbDefault() {
        thumbEntity.changeThumb(null)
        invalidate()
    }

    fun setThumbDrawable(drawable: Drawable) {
        thumbEntity.changeThumb(drawable)
        invalidate()
    }

    fun setMin(min: Int) {
        minValue = min
        invalidate()
    }

    fun getMin(): Int {
        return minValue
    }

    fun setMax(max: Int) {
        maxValue = max
        invalidate()
    }

    fun getMax(): Int {
        return maxValue
    }

    fun setProgress(progress: Int) {
        progressValue = when {
            progress < minValue -> minValue
            progress > maxValue -> maxValue
            else -> progress
        }

        val color = getProgressColorByLimit(progress)

        progressDrawable.setProgressColor(color)
        thumbEntity.changeThumbColor(color)
        goalEntity.setActive(progress >= goalValue)

        if (progressAnimation) {
            ValueAnimator.ofInt(minValue, progressValue).apply {
                duration = progressAnimationDurationMs.toLong()
                addUpdateListener { animation ->
                    this@GaugeSeekBar.progressValue = animation.animatedValue as Int
                    invalidate()
                }
            }.start()
        } else {
            invalidate()
        }
    }

    fun getProgress() = progressValue

    fun setGaugeSeekBarListener(callback: (gaugeSeekBar: GaugeSeekBar, progress: Int) -> Unit) {
        gaugeSeekBarListener = object : GaugeSeekBarListener {
            override fun onProgressChanged(gaugeSeekBar: GaugeSeekBar, progress: Int) {
                callback(gaugeSeekBar, progress)
            }
        }
    }

    private fun calculateProgressRatio() {
        if (maxValue > minValue) {
            progressRatio = (progressValue - minValue).toFloat() / (maxValue - minValue)
        } else {
            progressRatio = 0f
        }
    }

    @ColorInt
    private fun getProgressColorByLimit(value: Int): Int {
        if (useProgressColors && progressLimitArray.size+1 == progressColorArray.size) {
            for (index in progressLimitArray.indices) {
                val progressLimit = progressLimitArray[index]
                if (value < progressLimit) {
                    return progressColorArray.get(index)
                }
            }
            return progressColorArray.last()
        }
        return progressColor
    }
}
