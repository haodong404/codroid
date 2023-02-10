package org.codroid.body.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import org.codroid.body.R

class Divider : View {
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

    private val mPaint = Paint()
    private val mRect = RectF()

    private var mLineSize = 0

    private fun initialize(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Divider)
        mPaint.color = ta.getColor(R.styleable.Divider_backgroundColor, Color.BLACK)
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mLineSize = ta.getDimensionPixelSize(R.styleable.Divider_lineSize, 10)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = mLineSize + paddingLeft + paddingRight
        var height = mLineSize + paddingTop + paddingBottom
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = widthSize.coerceAtMost(width)
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = heightSize.coerceAtMost(height)
        }
        mRect.left = paddingLeft.toFloat()
        mRect.top = paddingTop.toFloat()
        mRect.right = width - paddingRight.toFloat()
        mRect.bottom = height - paddingBottom.toFloat()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            drawRoundRect(mRect, 10f, 10f, mPaint)
        }
    }
}