/*
 *     Copyright (c) 2021 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.body.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.codroid.body.R
import org.codroid.body.dip2px
import org.codroid.editor.utils.Timer
import kotlin.math.abs


class WindowTab : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
        val typeArr = context.obtainStyledAttributes(attrs, R.styleable.WindowTab)
        mTitle = typeArr.getString(R.styleable.WindowTab_android_text) ?: "None"
        typeArr.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    companion object {
        private var mCloseBitmap: Bitmap? = null
        private var mTitleColor = 0
        private var mBackgroundColor = 0

        private var mTitleColorSelected = 0
        private var mBackgroundColorSelected = 0
        private var mRadius = 0f
        private var mGap = 0f

        private var mMinTitleWidth = -1F
        private var mMaxTitleLength = -1F
    }

    private val mIconRectF: RectF by lazy {
        RectF()
    }

    private val mRectF = RectF()

    private val mTitlePaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = context.dip2px(12F)
    }
    private val mBackgroundPaint: Paint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.FILL
    }

    private var mTitle = ""
    private var mTitleHeight = 0

    private var mIconBitmap: Bitmap? = null
    private var mIsSelect: Boolean = false

    private var mCurrentTitleColor = mTitleColor
    private var mCurrentBackgroundColor = mBackgroundColor

    private var mTitleLeft = 0F
    private var mCloseIconLeft = 0F
    private var mCloseIconTop = 0F

    private var mCloseColorFilter: PorterDuffColorFilter? = null

    private val mTitleColorAnimator: ValueAnimator by lazy {
        ValueAnimator.ofArgb(mTitleColor, mTitleColorSelected).apply {
            duration = 150
            addUpdateListener {
                mCurrentTitleColor = it.animatedValue as Int
                mCloseColorFilter =
                    PorterDuffColorFilter(it.animatedValue as Int, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    private val mBackgroundAnimator: ValueAnimator by lazy {
        ValueAnimator.ofArgb(mBackgroundColor, mBackgroundColorSelected).apply {
            duration = 150
            addUpdateListener {
                mCurrentBackgroundColor = it.animatedValue as Int
                postInvalidateOnAnimation()
            }
        }
    }


    private var mOnCloseListener: OnClickListener? = null

    init {
        if (mTitleColor == 0) {
            mTitleColor =
                MaterialColors.getColor(context, R.attr.colorOnTertiaryContainer, Color.BLACK)
            mBackgroundColor =
                MaterialColors.getColor(context, R.attr.colorTertiaryContainer, Color.WHITE)

            mTitleColorSelected =
                MaterialColors.getColor(context, R.attr.colorOnTertiary, Color.BLACK)
            mBackgroundColorSelected =
                MaterialColors.getColor(context, R.attr.colorTertiary, Color.CYAN)
            mRadius = context.dip2px(4f)
            mGap = context.dip2px(4f)
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.WindowTab)
        ta.getResourceId(R.styleable.WindowTab_android_fontFamily, 0).let {
            if (it != 0) {
                mTitlePaint.typeface = Typeface.create(
                    ResourcesCompat.getFont(context, it),
                    ta.getInt(R.styleable.WindowTab_android_textStyle, 0)
                )
            } else {
                ta.getInt(R.styleable.WindowTab_android_textStyle, 0).let { i ->
                    if (i != 0) {
                        mTitlePaint.typeface = Typeface.create(Typeface.DEFAULT, i)
                    }
                }
            }
        }
        if (mCloseBitmap == null) {
            mCloseBitmap = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_close_24,
                context.theme
            )?.apply {
                setTint(MaterialColors.getColor(context, R.attr.colorError, Color.BLACK))
            }?.toBitmap()
        }
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = paddingLeft.toFloat()
        var height = 0f
        mIconBitmap?.let {
            width += it.width
            val top = measuredHeight / 2F - it.height / 2F
            mIconRectF.set(
                paddingLeft.toFloat(),
                top,
                it.width.toFloat() + paddingLeft,
                top + it.height
            )
            width += mGap
        }

        mTitleHeight = mTitlePaint.fontMetrics.let {
            return@let abs(it.ascent + it.descent + it.leading).toInt()
        }
        mTitleLeft = width
        width += mTitlePaint.measureText(mTitle)
        height += mTitleHeight + paddingTop + paddingBottom

        mCloseBitmap?.let {
            mCloseIconLeft = width + mGap
            width += it.width + mGap
            mCloseIconTop = height / 2 - it.height / 2
        }

        width += paddingRight

        mRectF.left = 0f
        mRectF.top = 0f
        mRectF.right = width
        mRectF.bottom = height

        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            drawRoundRect(mRectF, mRadius, mRadius, getBackgroundPaint())
            mIconBitmap?.let {
                drawBitmap(it, null, mIconRectF, null)
            }
            drawText(
                mTitle,
                mTitleLeft,
                paddingTop + mTitleHeight.toFloat(),
                getTitlePaint()
            )

            mCloseBitmap?.let {
                mTitlePaint.colorFilter = mCloseColorFilter
                drawBitmap(it, mCloseIconLeft, mCloseIconTop, mTitlePaint)
                mTitlePaint.colorFilter = null
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.run {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (hitCloseButton(event)) {
                        mOnCloseListener?.onClick(this@WindowTab)
                    } else {
                        return performClick()
                    }
                    return true
                }
                else -> {}
            }
        }
        return false
    }

    private fun hitCloseButton(event: MotionEvent): Boolean {
        val leftBound = (mCloseIconLeft - mGap).toInt()
        return event.x.toInt() in leftBound..measuredWidth
    }

    private fun getTitlePaint(): Paint {
        mTitlePaint.color = mCurrentTitleColor
        return mTitlePaint
    }

    private fun getBackgroundPaint(): Paint {
        mBackgroundPaint.color = mCurrentBackgroundColor
        return mBackgroundPaint
    }

    fun setOnCloseListener(listener: OnClickListener) {
        this.mOnCloseListener = listener
    }

    fun setText(title: String) {
        this.mTitle = title
        requestLayout()
    }

    fun setIconBitmap(bitmap: Bitmap) {
        this.mIconBitmap = bitmap
        requestLayout()
        invalidate()
    }

    fun setIsSelected(isSelected: Boolean) {
        if (mIsSelect != isSelected) {
            mIsSelect = isSelected
            if (!isSelected) {
                mTitleColorAnimator.reverse()
                mBackgroundAnimator.reverse()
            } else {
                mTitleColorAnimator.start()
                mBackgroundAnimator.start()
            }
        }
    }
}