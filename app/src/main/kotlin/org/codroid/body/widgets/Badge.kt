package org.codroid.body.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import org.codroid.body.R
import org.codroid.body.dip2px
import kotlin.math.abs
import kotlin.math.min

class Badge : View {
    constructor(context: Context?) : super(context)
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

    private val mPaddingVertical = context.dip2px(4f)
    private val mPaddingHorizontal = context.dip2px(6f)
    private var mTextLineHeight: Int = 0
    private val mTextPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val mBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private var mText = 0

    private var mMaxNumber = 0

    private val mRect = RectF()

    private fun initialize(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Badge)
        mTextPaint.color = ta.getColor(R.styleable.Badge_android_textColor, Color.GRAY)
        ta.getResourceId(R.styleable.StatusTag_android_fontFamily, 0).let {
            if (it != 0) {
                mTextPaint.typeface = Typeface.create(
                    ResourcesCompat.getFont(context, it),
                    ta.getInt(R.styleable.StatusTag_android_textStyle, 0)
                )
            } else {
                ta.getInt(R.styleable.StatusTag_android_textStyle, 0).let { i ->
                    if (i != 0) {
                        mTextPaint.typeface = Typeface.create(Typeface.DEFAULT, i)
                    }
                }
            }
        }
        mTextPaint.textSize = ta.getDimension(R.styleable.Badge_android_textSize, 30f)

        mText = (ta.getString(R.styleable.Badge_android_text) ?: "0").toInt()

        mBackgroundPaint.color = ta.getColor(com.google.android.material.R.styleable.Badge_backgroundColor, Color.TRANSPARENT)

        ta.recycle()
    }

    fun setMaxNumber(num: Int) {
        mMaxNumber = num
        requestLayout()
    }

    fun setNumber(num: Int) {
        mText = num
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mText = min(mMaxNumber, mText)
        mTextPaint.fontMetrics.let {
            mTextLineHeight = abs(it.ascent + it.descent + it.leading).toInt()
        }
        val width = (mPaddingHorizontal * 2 + mTextPaint.measureText(mText.toString()))
        val height = (mTextLineHeight + mPaddingVertical * 2)
        mRect.left = 0f
        mRect.top = 0f
        mRect.right = width
        mRect.bottom = height
        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        canvas.run {
            drawRoundRect(mRect, 100f, 100f, mBackgroundPaint)
            drawText(
                mText.toString(),
                mPaddingHorizontal,
                mPaddingVertical + mTextLineHeight,
                mTextPaint
            )
        }
    }
}