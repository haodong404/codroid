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
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.*
import org.codroid.editor.*

class DirTreeItemView : View {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DirTreeItemView)
        configure.title = typedArray.getString(R.styleable.DirTreeItemView_title) ?: ""
        configure.titleColor =
            typedArray.getColor(R.styleable.DirTreeItemView_titleColor, Color.BLACK)
        type = typedArray.getString(R.styleable.DirTreeItemView_type) ?: ""
        level = typedArray.getInteger(R.styleable.DirTreeItemView_level, 0)
        isExpanded = typedArray.getBoolean(R.styleable.DirTreeItemView_isExpanded, false)
        typedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    companion object {
        const val DIRECTORY = "directory"
        const val FILE = "file"
    }

    private var mExpandMoreBitmap: Bitmap? = null

    private var mIconBitmap: Bitmap? = null

    private var mTagBitmap: Bitmap? = null
    private val mTagRectF: RectF by lazy {
        RectF()
    }

    private val mExpandMoreMatrix = Matrix()
    private val mIconRectF: RectF by lazy {
        RectF()
    }

    private lateinit var mTitlePaint: Paint
    private var mTitleTopSpace = 0F
    private var mTitleHalfHeight = 0F
    private var mTitleMarginLeft = 0

    private val alphaPaint: Paint by lazy {
        Paint().apply {
            alpha = 0x5F
        }
    }

    private lateinit var mIndentLinePaint: Paint
    private var type = DIRECTORY

    lateinit var configure: Configure
    private var level = 0
    var isExpanded = false
    var isInit = false

    private fun init() {
        // Create a default configure
        configure = Configure(
            leftPadding = context.dip2px(4F),
            title = "Hello",
            titleColor = context.getAttrColor(android.R.attr.textColorPrimary),
            titleSize = context.sp2px(12F),
            iconSize = context.dip2px(24F),
            tagSize = context.dip2px(14F),
            indentLineColor = Color.GRAY,
            indentLineStroke = context.dip2px(0.6F)
        )

        // Create the expand-more bitmap if it's a directory
        if (isDir()) {
            mExpandMoreBitmap =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_expand_more_24,
                    context.theme
                )?.toBitmap()!!

        }

        // Create the title's paint
        mTitlePaint = Paint().apply {
            style = Paint.Style.FILL
            color = configure.titleColor
            alpha = (0xFF * 0.87).toInt()
            textSize = configure.titleSize
        }

        mTitleHalfHeight =
            (mTitlePaint.fontMetrics.bottom - mTitlePaint.fontMetrics.top) / 2 - mTitlePaint.fontMetrics.bottom

        // Create the indent line paint
        mIndentLinePaint = Paint().apply {
            style = Paint.Style.STROKE
            color = configure.indentLineColor
            strokeWidth = configure.indentLineStroke
            pathEffect = DashPathEffect(floatArrayOf(10F, 10F), 1F)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!isInit) return

        var levelMargin = 0F
        for (i in 1..level) {
            levelMargin += configure.iconSize
        }

        // Measure the expand more icon
        val top = measuredHeight / 2F - configure.iconSize / 2F
        if (mExpandMoreBitmap != null || isDir()) {
            mExpandMoreMatrix.setTranslate(configure.leftPadding + levelMargin, top)
            var degrees = 270F
            if (isExpanded) {
                degrees = 0F
            }
            mExpandMoreMatrix.preRotate(degrees, configure.iconSize / 2, configure.iconSize / 2)
        }

        val iconMarginLeft = levelMargin + configure.leftPadding + configure.iconSize +
                context.dip2px(4F) // icon margin left
        mIconRectF.set(
            iconMarginLeft,
            top,
            iconMarginLeft + configure.iconSize,
            top + configure.iconSize
        )

        if (configure.tagSize != 0F) {
            val tagLeft = mIconRectF.right - configure.tagSize
            val tagTop = mIconRectF.bottom - configure.tagSize
            mTagRectF.set(
                tagLeft,
                tagTop,
                tagLeft + configure.tagSize,
                tagTop + configure.tagSize
            )
        }

        mTitleTopSpace = mTitleHalfHeight + measuredHeight / 2F
        mTitleMarginLeft = (iconMarginLeft + configure.iconSize).toInt()
        if (mTitleMarginLeft + mTitlePaint.measureText(configure.title) > measuredWidth) {
            setMeasuredDimension((mTitleMarginLeft + mTitlePaint.measureText(configure.title)).toInt() + 50, measuredHeight)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isInit) return

        mExpandMoreBitmap?.let { // Draw the expand icon if it is a directory
            if (isDir()) {
                canvas?.drawBitmap(it, mExpandMoreMatrix, alphaPaint)
            }
        }
        mIconBitmap?.let {
            if (!mIconBitmap?.isRecycled!!) {
                canvas?.drawBitmap(mIconBitmap!!, null, mIconRectF, null)
            }
        }

        mTagBitmap?.let {
            if (!mTagBitmap?.isRecycled!!) {
                canvas?.drawBitmap(mTagBitmap!!, null, mTagRectF, null)
            }
        }

        canvas?.drawText(
            configure.title,
            mTitleMarginLeft + configure.leftPadding,
            mTitleTopSpace,
            mTitlePaint
        )

        var indentLineX = configure.leftPadding + configure.iconSize / 2
        for (i in 1..level) {
            canvas?.drawLine(indentLineX, 0F, indentLineX, height.toFloat(), mIndentLinePaint)
            indentLineX += configure.iconSize
        }
    }


    suspend fun setImageBitmap(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            mIconBitmap = bitmap.zoom(configure.iconSize / bitmap.width)
            postInvalidate()
        }
    }

    suspend fun setTagBitmap(bitmap: Bitmap?) {
        withContext(Dispatchers.IO) {
            if (bitmap == null) {
                if (mTagBitmap != null) {
                    mTagBitmap?.recycle()
                    mTagBitmap = null
                }
            }
            bitmap?.let {
                mTagBitmap = it.zoom(configure.tagSize / bitmap.width)
            }
            postInvalidate()
        }
    }

    fun expand() {
        if (!isExpanded && type == DIRECTORY) {
            mExpandMoreMatrix.preRotate(-270F, configure.iconSize / 2, configure.iconSize / 2)
            postInvalidate()
            isExpanded = true
        }
    }

    fun collapse() {
        if (isExpanded && type == DIRECTORY) {
            mExpandMoreMatrix.preRotate(270F, configure.iconSize / 2, configure.iconSize / 2)
            invalidate()
            isExpanded = false
        }
    }

    fun changeStatus() {
        if (type == DIRECTORY) {
            if (isExpanded) {
                collapse()
            } else {
                expand()
            }
        }
    }

    fun setTitle(text: String) {
        configure.title = text
        invalidate()
    }

    fun setTitle(text: CharSequence) {
        setTitle(text.toString())
    }

    fun setTitle(resId: Int) {
        setTitle(context.getText(resId))
    }

    fun setLevel(level: Int) {
        if (this.level == level) return
        this.level = level
    }


    fun setTitleColor(color: Int) {
        configure.titleColor = color
    }

    fun setType(type: String) {
        this.type = type
    }

    fun setIsExpanded(isExpanded: Boolean) {
        this.isExpanded = isExpanded
        this.isInit = true
        requestLayout()
    }

    fun isDir(): Boolean {
        return type == DIRECTORY
    }


    data class Configure(
        var leftPadding: Float,
        var title: String,
        var titleColor: Int,
        var titleSize: Float,
        var iconSize: Float, // It's the side length of the icon square.
        var tagSize: Float,
        var indentLineColor: Int,
        var indentLineStroke: Float
    )

    inner class ExpandIconRotation : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
        }

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)

        }
    }
}