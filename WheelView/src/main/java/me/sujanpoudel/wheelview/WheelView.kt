package me.sujanpoudel.wheelview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toRect
import androidx.core.view.GestureDetectorCompat
import me.sujanpoudel.wheelview.helpers.dpToPx
import me.sujanpoudel.wheelview.helpers.inside
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


data class Arc(val text: String, val selected: Boolean, val startAngle: Float, val sweepAngle: Float)
data class Circle(val cx: Float, val cy: Float, val radius: Float)
data class ImageArc(val image: Bitmap?, val selected: Boolean, val startAngle: Float, val sweepAngle: Float)

class WheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    enum class Mode {
        ANIMATE_TO_ANCHOR,
        STATIC,
    }

    private var _mode: Mode;
    private var _anchorAngle: Float
    private var _startAngle: Float
    private var _centerIconPadding: Float
    private var _dividerStrokeWidth: Float
    private var _arcBackgroundColor: Int
    private var _selectedArcBackgroundColor: Int
    private var _centerIconTint: Int
    private var _textSize: Int
    private var _textColor: Int
    private var _selectedTextColor: Int
    private var _animationduration: Long
    private var _centerIcon: Drawable?
    private var _titles = listOf<String>()
    private var _userInputMode: Boolean

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.WheelView, 0, 0)
            .apply {
                _mode = Mode.values()[getInt(R.styleable.WheelView_wheelMode, Mode.ANIMATE_TO_ANCHOR.ordinal)]
                _anchorAngle = getFloat(R.styleable.WheelView_wheelAnchorAngle, 270f)
                _startAngle = getFloat(R.styleable.WheelView_wheelStartAngle, 0f)
                _dividerStrokeWidth = getDimensionPixelSize(R.styleable.WheelView_wheelDividerStrokeWidth, dpToPx(12f)).toFloat()
                _arcBackgroundColor = getColor(R.styleable.WheelView_wheelArcBackgroundColor, Color.parseColor("#F7F8FB"))
                _selectedArcBackgroundColor = getColor(R.styleable.WheelView_wheelSelectedArcBackgroundColor, Color.parseColor("#48AEBF"))
                _centerIconTint = getColor(R.styleable.WheelView_wheelCenterIconTint, Color.WHITE)
                _centerIcon = getDrawable(R.styleable.WheelView_wheelCenterIcon)
                _centerIconPadding = getDimensionPixelSize(R.styleable.WheelView_wheelCenterIconPadding, dpToPx(12f)).toFloat()
                _textSize = getDimensionPixelSize(R.styleable.WheelView_wheelTextSize, dpToPx(14f))
                _textColor = getColor(R.styleable.WheelView_wheelTextColor, Color.BLACK)
                _selectedTextColor = getColor(R.styleable.WheelView_wheelSelectedTextColor, Color.WHITE)
                _animationduration = getFloat(R.styleable.WheelView_wheelAnimationDuration, 500f).toLong()
                _userInputMode = getBoolean(R.styleable.WheelView_userInputMode, true)
            }
            .recycle()
    }

    var titles
        get() = _titles
        set(value) {
            _titles = value
            arcs = _titles.map { ImageArc(image = null, selected = false, startAngle = 0f, sweepAngle = 0f) }
            refresh()
        }

//    var titles
//        get() = _titles
//        set(value) {
//            _titles = value // create arcs from titles
//            arcs = _titles.map { Arc(it, false, 0f, 0f) }
//            refresh()
//        }

    var mode = _mode
    var animationDuration = _animationduration
    var anchorAngle
        get() = _anchorAngle
        set(value) {
            _anchorAngle = value
            refresh()
        }

    var textSize
        get() = _textSize
        set(value) {
            _textSize = value
            refresh()
        }

    var textColor
        get() = _textColor
        set(value) {
            _textColor = value
            refresh()
        }

    var selectedTextColor
        get() = _selectedTextColor
        set(value) {
            _selectedTextColor = value
            refresh()
        }

    var userInputMode
        get() = _userInputMode
        set(value) {
            _userInputMode = value
        }

    var startAngle
        get() =
            when (mode) {
                // get start angle so currently focused index is in center of anchor angle
                // 360s to normalize angle between 0 to 360
                Mode.ANIMATE_TO_ANCHOR -> (anchorAngle - (focusedIndex * 360f / _titles.size + 360f / (2 * _titles.size)) + 360) % 360
                Mode.STATIC -> _startAngle
            }
        set(value) {
            _startAngle = value
            refresh()
        }
    var dividerStrokeWidth
        get() = _dividerStrokeWidth
        set(value) {
            _dividerStrokeWidth = value
            refresh()
        }

    var arcBackgroundColor
        get() = _arcBackgroundColor
        set(value) {
            _arcBackgroundColor = value
            refresh()
        }
    var selectedArcBackgroundColor
        get() = _selectedArcBackgroundColor
        set(value) {
            _selectedArcBackgroundColor = value
            refresh()
        }

    var centerIconTint
        get() = _centerIconTint
        set(value) {
            _centerIconTint = value
            refresh()
        }

    var centerIcon
        get() = _centerIcon
        set(value) {
            _centerIcon = value
            refresh()
        }

    var focusedIndex = 0
        set(value) {
            if (value == field) return
            val prevStartAngle = startAngle
            field = value // set the value
            if (mode == Mode.ANIMATE_TO_ANCHOR)
                animateWheelToNewAngle(prevStartAngle)
            else
                refresh()
        }

    //listeners
    var selectListener: ((Int) -> Unit)? = null
    var centerClickListener: (() -> Unit)? = null

    //mask filter for shadow
    private val blurMaskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)

    //paints
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = dpToPx(14f).toFloat()
    }
    private val bgCircleShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        maskFilter = blurMaskFilter
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#2C000000")
    }

    //shapes
    var arcs = listOf<ImageArc>()
    private lateinit var bgCircle: Circle
    private lateinit var centerCircle: Circle
    private lateinit var arcStrokeRect: RectF
    private lateinit var arcRect: RectF

    // detect click in the wheel
    private val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?) = true
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            onClickOnWheel(e!!)
            return true
        }
    })

    private fun animateWheelToNewAngle(prevStartAngle: Float) {
        val startAngle = this.startAngle.let { abgle ->
            val paths = listOf( // all the possible angles
                abgle,
                abgle + 360,
                (abgle + 360) % 360,
                abgle - 360,
                (abgle - 360) % 360
            )
            paths.map { it to abs(prevStartAngle - it) }
                .minBy { it.second }!!
                .first // find the min possible path to prev start angle
        }

        //animate between prev value and new value
        ValueAnimator.ofFloat(prevStartAngle, startAngle).apply {
            duration = animationDuration
            interpolator = LinearInterpolator()
            start()
            addUpdateListener {
                refresh(it.animatedValue as Float)
            }
        }
    }



    fun refresh(sAngle: Float = startAngle) {
        val backgroundCircleRadius = min(width, height) / 2.1f  //2.1 to make some space for the bg shadow
        val arcStrokeRadius = backgroundCircleRadius - dividerStrokeWidth / 2
        val centerX = width / 2f
        val centerY = height / 2f

        arcRect = RectF(centerX - backgroundCircleRadius, centerY - backgroundCircleRadius, centerX + backgroundCircleRadius, centerY + backgroundCircleRadius)
        arcStrokeRect = RectF(0f,0f,0f,0f)
//            RectF(centerX - arcStrokeRadius, centerY - arcStrokeRadius, centerX + arcStrokeRadius, centerY + arcStrokeRadius)
        var angle = sAngle
        val sweepAngle = 360f / _titles.size
        arcs = arcs.mapIndexed { index, arc ->
            angle += sweepAngle
            ImageArc(
                image = arc.image,
                selected = index == focusedIndex,
                startAngle = angle - sweepAngle,
                sweepAngle = sweepAngle
            )
        }
        bgCircle = Circle(centerX, centerY, backgroundCircleRadius)
        centerCircle = Circle(centerX, centerY, backgroundCircleRadius * .3f).also {
            val width = it.radius - dividerStrokeWidth / 2 - _centerIconPadding
            centerIcon?.bounds = RectF(it.cx - width / 2, it.cy - width / 2, it.cx + width / 2, it.cy + width / 2).toRect()
        }
        centerIcon?.apply {
            DrawableCompat.setTint(this, centerIconTint)
        }
        invalidate()
        requestLayout()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        refresh()
    }

//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//
//        val save = canvas!!.save()
//
//        bgCircle.let {
//            canvas.drawCircle(it.cx, it.cy, it.radius, bgCircleShadowPaint)
//            canvas.drawCircle(it.cx, it.cy, it.radius, bgCirclePaint)
//        }
//
//        arcs.forEach {
//            canvas.drawArc(arcRect, it.startAngle, it.sweepAngle, true, arcPaint.apply {
//                color = if (it.selected) selectedArcBackgroundColor else arcBackgroundColor
//                style = Paint.Style.FILL
//            })
//            canvas.drawArc(arcStrokeRect, it.startAngle, it.sweepAngle, true, arcPaint.apply {
//                color = Color.WHITE
//                strokeWidth = dividerStrokeWidth
//                style = Paint.Style.STROKE
//            })
//
//        }
//
//        centerCircle.let {
//            canvas.drawCircle(it.cx, it.cy, it.radius, arcPaint.apply {
//                color = selectedArcBackgroundColor
//                style = Paint.Style.FILL
//            })
//            canvas.drawCircle(it.cx, it.cy, it.radius, arcPaint.apply {
//                color = Color.WHITE
//                strokeWidth = dividerStrokeWidth
//                style = Paint.Style.STROKE
//            })
//        }
//
//        arcs.forEach {
//            val radius = bgCircle.radius * .65f
//            val angle = Math.toRadians(it.startAngle + it.sweepAngle / 2.0)
//            val x = arcRect.centerX() + radius * cos(angle).toFloat()
//            val y = arcRect.centerY() + radius * sin(angle).toFloat()
//            val textLayout = StaticLayout(it.text, textPaint, 200, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)
//            textPaint.color = if (it.selected) selectedTextColor else textColor
//            textPaint.textSize = _textSize.toFloat()
//            canvas.save()
//            canvas.translate(x - textLayout.width / 2, y - textLayout.height / 2)
//            textLayout.draw(canvas)
//            canvas.restore()
//        }
//
//        centerIcon?.draw(canvas)
//        canvas.restoreToCount(save)
//    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val save = canvas!!.save()

        bgCircle.let {
            canvas.drawCircle(it.cx, it.cy, it.radius, bgCircleShadowPaint)
            canvas.drawCircle(it.cx, it.cy, it.radius, bgCirclePaint)
        }

        arcs.forEach {
            canvas.drawArc(arcRect, it.startAngle, it.sweepAngle, true, arcPaint.apply {
                color = if (it.selected) selectedArcBackgroundColor else arcBackgroundColor
                style = Paint.Style.FILL
            })
            canvas.drawArc(arcStrokeRect, it.startAngle, it.sweepAngle, true, arcPaint.apply {
                color = Color.WHITE
                strokeWidth = dividerStrokeWidth
                style = Paint.Style.STROKE
            })

            it.image?.let { image ->

                val bitmapToDraw = if (it.selected) getTintedBitmap(image, Color.WHITE) else image

                val radius = bgCircle.radius * .8f
                val angle = Math.toRadians(it.startAngle + it.sweepAngle / 2.0)
                val x = arcRect.centerX() + radius * cos(angle).toFloat()
                val y = arcRect.centerY() + radius * sin(angle).toFloat()

                canvas.save()
                canvas.translate(x - bitmapToDraw.width / 2, y - bitmapToDraw.height / 2)
                canvas.drawBitmap(bitmapToDraw, 0f, 0f, null)
                canvas.restore()
            }
        }

        centerCircle.let {
            canvas.drawCircle(it.cx, it.cy, it.radius * 2f, arcPaint.apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            })
            canvas.drawCircle(it.cx, it.cy, it.radius * 2f, arcPaint.apply {
                color = Color.WHITE
                strokeWidth = dividerStrokeWidth
                style = Paint.Style.STROKE
            })
        }

        centerIcon?.draw(canvas)
        canvas.restoreToCount(save)
    }


    private fun getTintedBitmap(original: Bitmap, tintColor: Int): Bitmap {
        val tintedBitmap = Bitmap.createBitmap(original.width, original.height, original.config)
        val canvas = Canvas(tintedBitmap)
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(original, 0f, 0f, paint)
        return tintedBitmap
    }

    private fun onClickOnWheel(event: MotionEvent) {
        if (!event.inside(centerCircle.cx, centerCircle.cy, bgCircle.radius)) return
        if (event.inside(centerCircle.cx, centerCircle.cy, centerCircle.radius)) {
            centerClickListener?.let { it() }
            return
        }
        for ((index, arc) in arcs.withIndex()) { // check the arcs
            if (event.inside(arcRect, arc.startAngle, arc.sweepAngle)) {
                focusedIndex = index
                selectListener?.let { it(index) }
                break
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (_userInputMode) gestureDetector.onTouchEvent(event)
        else super.onTouchEvent(event)
    }

}