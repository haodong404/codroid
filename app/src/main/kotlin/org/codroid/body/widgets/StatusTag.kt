package org.codroid.body.widgets

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.color.MaterialColors
import org.codroid.body.R
import org.codroid.body.dip2px
import kotlin.math.abs

class StatusTag : View {

    private lateinit var mPaint: Paint
    private lateinit var mBackgroundPaint: Paint
    private lateinit var mBorderPaint: Paint
    private lateinit var mText: String
    private var mBackgroundColor = 0
    private var mBorderColor = 0
    private var mBorderWidth = 1f
    private var mRadius = context.dip2px(4f)

    private var mPaddingHorizontal = context.dip2px(6f).toInt()
    private var mPaddingVertical = context.dip2px(4f).toInt()

    private var mTextLineHeight = 0

    private var mBitmap: Bitmap? = null
    private var mIconRect: Rect = Rect()
    private var mGap = 0
    private val mIconColorFilter = PorterDuffColorFilter(
        MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnSurfaceVariant,
            Color.GRAY
        ), PorterDuff.Mode.SRC_IN
    )

    constructor(context: Context) : super(context) {
        initialize(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StatusTag)
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.LEFT
        mPaint.color = ta.getColor(R.styleable.StatusTag_android_textColor, Color.BLACK)
        ta.getResourceId(R.styleable.StatusTag_android_fontFamily, 0).let {
            if (it != 0) {
                mPaint.typeface = Typeface.create(
                    ResourcesCompat.getFont(context, it),
                    ta.getInt(R.styleable.StatusTag_android_textStyle, 0)
                )
            } else {
                ta.getInt(R.styleable.StatusTag_android_textStyle, 0).let { i ->
                    if (i != 0) {
                        mPaint.typeface = Typeface.create(Typeface.DEFAULT, i)
                    }
                }
            }
        }
        mPaint.textSize = ta.getDimension(R.styleable.StatusTag_android_textSize, 30f)
        mText = ta.getString(R.styleable.StatusTag_android_text) ?: ""

        mBackgroundColor =
            ta.getColor(R.styleable.StatusTag_android_background, Color.TRANSPARENT)
        mBorderColor = ta.getColor(R.styleable.StatusTag_borderColor, Color.BLACK)
        mBorderWidth = ta.getDimension(R.styleable.StatusTag_borderWidth, 4f)
        val drawable = ta.getDrawable(R.styleable.StatusTag_android_drawable)
        mBitmap = drawable?.toBitmap()

        mBackgroundPaint = Paint()
        mBackgroundPaint.color = mBackgroundColor
        mBackgroundPaint.style = Paint.Style.FILL

        mBorderPaint = Paint()
        mBorderPaint.color = ta.getColor(R.styleable.StatusTag_borderColor, Color.BLACK)
        mBorderPaint.isAntiAlias = true
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = mBorderWidth
        mGap = mPaddingVertical
        ta.recycle()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        if (mText.isBlank()) {
            setMeasuredDimension(0, 0)
            return
        }

        mPaint.fontMetrics.let {
            mTextLineHeight = abs(it.ascent + it.descent + it.leading).toInt()
            if (mBitmap != null) {
                val left = mPaddingHorizontal + mBorderWidth.toInt()
                val top = mPaddingVertical + mBorderWidth.toInt()
                mIconRect.left = left
                mIconRect.right = left + mTextLineHeight
                mIconRect.top = top
                mIconRect.bottom = top + mTextLineHeight
            }
        }

        var width = (mPaddingHorizontal * 2 + mPaint.measureText(mText) + mBorderWidth * 2).toInt()
        if (mBitmap != null) {
            width += mTextLineHeight + mGap
        }
        val height = mTextLineHeight + mPaddingVertical * 2 + mBorderWidth.toInt() * 2
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            if (mText.isBlank()) {
                return
            }
            val width = measuredWidth.toFloat()
            val height = measuredHeight.toFloat()
            if (mBackgroundPaint.color != Color.TRANSPARENT) {
                drawRoundRect(
                    0f,
                    0f,
                    width,
                    height,
                    mRadius + 2,
                    mRadius + 2,
                    mBackgroundPaint
                )
            }
            val half = mBorderWidth / 2

            drawRoundRect(
                half,
                half,
                width - half,
                height - half,
                mRadius,
                mRadius,
                mBorderPaint
            )

            var offset = mPaddingHorizontal + mBorderWidth

            mBitmap?.let {
                offset += mTextLineHeight + mGap
                mPaint.colorFilter = mIconColorFilter
                drawBitmap(
                    it,
                    null,
                    mIconRect,
                    mPaint
                )
                mPaint.colorFilter = null
            }

            drawText(
                mText,
                offset,
                mPaddingVertical.toFloat() + mTextLineHeight + mBorderWidth,
                mPaint
            )
        }
    }

    fun setText(str: String) {
        this.mText = str
        requestLayout()
    }

    fun setDrawable(drawable: Drawable?) {
        this.mBitmap = drawable?.toBitmap()
        requestLayout()
    }

    fun setBitmap(bitmap: Bitmap) {
        this.mBitmap = bitmap
        requestLayout()
    }
}