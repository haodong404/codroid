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

package org.codroid.editor.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import org.codroid.editor.R
import org.codroid.editor.dip2px
import org.codroid.editor.getAttrColor


class WindowTab : View {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    companion object {
        var mCloseBitmap: Bitmap? = null
    }

    private val mCloseRectF: RectF by lazy {
        RectF()
    }

    private val mIconRectF: RectF by lazy {
        RectF()
    }

    private lateinit var mTitlePaint: Paint

    private var titleLeft = 0F

    var mTitle = ""
    var mTitleSize = 0F
    var mTitleColor = 0

    var mTitleHalfHeight = 0F
    var mTitleTop = 0F

    var mIconBitmap: Bitmap? = null

    var mCloseListener: OnClickListener? = null

    private fun init() {
        mTitleSize = context.dip2px(12F)
        mTitleColor = context.getAttrColor(android.R.attr.textColorPrimary)
        mTitlePaint = Paint().apply {
            style = Paint.Style.FILL
            textSize = mTitleSize
            color = mTitleColor
            strokeWidth = context.dip2px(1F)
        }

        if (mCloseBitmap == null) {
            mCloseBitmap = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_close_24,
                context.theme
            )?.toBitmap()
        }

        mTitleHalfHeight =
            (mTitlePaint.fontMetrics.bottom - mTitlePaint.fontMetrics.top) / 2 - mTitlePaint.fontMetrics.bottom
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measuredWidth = paddingLeft.toFloat()
        mIconBitmap?.let {
            measuredWidth += it.width
            val top = measuredHeight / 2F - it.height / 2F
            mIconRectF.set(
                paddingLeft.toFloat(),
                top,
                it.width.toFloat() + paddingLeft,
                top + it.height
            )
        }

        measuredWidth += context.dip2px(4F) // margin left

        titleLeft = measuredWidth

        measuredWidth += mTitlePaint.measureText(mTitle)

        mCloseBitmap?.let {
            val top = measuredHeight / 2F - it.height / 2F
            mCloseRectF.set(
                measuredWidth + context.dip2px(4F),
                top,
                measuredWidth + it.width + context.dip2px(4F),
                top + it.height
            )
            measuredWidth += it.width
        }
        mTitleTop = measuredHeight / 2F + mTitleHalfHeight
        measuredWidth += paddingLeft + paddingRight
        setMeasuredDimension(measuredWidth.toInt(), this.measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mIconBitmap?.let {
            canvas?.drawBitmap(it, null, mIconRectF, null)
        }
        canvas?.drawText(mTitle, titleLeft, mTitleTop, mTitlePaint)

        mCloseBitmap?.let {
            canvas?.drawBitmap(it, null, mCloseRectF, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isInRect(mCloseRectF, event.x, event.y)
            }

            MotionEvent.ACTION_UP -> {
                if (isInRect(mCloseRectF, event.x, event.y)) {
                    mCloseListener?.let {
                        mCloseListener?.onClick(this)
                    }
                    true
                } else {
                    false
                }
            }

            else -> {
                false
            }
        }
    }

    fun setOnCloseListener(listener: OnClickListener) {
        this.mCloseListener = listener
    }

    fun setTitle(title: String) {
        this.mTitle = title
    }

    fun setIconBitmap(bitmap: Bitmap) {
        this.mIconBitmap = bitmap
        requestLayout()
        invalidate()
    }

    fun isInRect(rect: RectF, x: Float, y: Float): Boolean {
        if (x >= rect.left && x <= rect.right) {
            if (y >= rect.top && y <= rect.bottom) {
                return true
            }
        }
        return false
    }
}