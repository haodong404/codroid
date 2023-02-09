package org.codroid.body.widgets

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
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

    private var mDrawable: Drawable? = null
    private var mDrawableRect: Rect = Rect()
    private var mGap = 0

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
        mDrawable = ta.getDrawable(R.styleable.StatusTag_android_drawable)

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
        mPaint.fontMetrics.let {
            mTextLineHeight = abs(it.ascent + it.descent + it.leading).toInt()
            if (mDrawable != null) {
                val left = mPaddingHorizontal + mBorderWidth.toInt()
                val top = mPaddingVertical + mBorderWidth.toInt()
                mDrawableRect.left = left
                mDrawableRect.right = left + mTextLineHeight
                mDrawableRect.top = top
                mDrawableRect.bottom = top + mTextLineHeight
            }
        }

        var width = (mPaddingHorizontal * 2 + mPaint.measureText(mText) + mBorderWidth * 2).toInt()
        if (mDrawable != null) {
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

            mDrawable?.let {
                offset += mTextLineHeight + mGap
                drawBitmap(
                    it.toBitmap(),
                    null,
                    mDrawableRect,
                    mPaint
                )
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

    fun setDrawable(drawable: Drawable) {
        this.mDrawable = drawable
        requestLayout()
    }

    fun setBitmap(bitmap: Bitmap) {
        this.mDrawable = bitmap.toDrawable(context.resources)
        requestLayout()
    }
}

class StatusTagLayoutManager(
    val maxLine: Int,
    val gap: Int = 0,
    val overflow: ((List<View>) -> Unit)? = null
) :
    RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler?.let { detachAndScrapAttachedViews(it) }
        var lineCount = 1
        var currentLeft = 0
        var currentTop = 0
        var isFirstInALine = true
        for (i in 0 until itemCount) {
            recycler?.getViewForPosition(i)?.let { view ->
                measureChildWithMargins(view, 0, 0)
                val viewWidth = getDecoratedMeasuredWidth(view)
                val viewHeight = getDecoratedMeasuredHeight(view)
                var right = currentLeft + viewWidth
                if (right <= width - 5) {
                    if (isFirstInALine) {
                        isFirstInALine = false
                    } else {
                        currentLeft += gap
                        right += gap
                    }
                    addView(view)
                    layoutDecorated(view, currentLeft, currentTop, right, currentTop + viewHeight)
                    currentLeft += viewWidth
                } else {
                    lineCount++
                    currentTop += gap
                    if (lineCount > maxLine) {
                        if (overflow != null) {
                            val overflowedViews = mutableListOf<View>()
                            for (j in i until itemCount) {
                                recycler.getViewForPosition(j).let {
                                    overflowedViews.add(it)
                                }
                            }
                            overflow.invoke(overflowedViews)
                        }
                        return
                    } else {
                        addView(view)
                        currentTop += viewHeight
                        currentLeft = 0
                        layoutDecorated(
                            view,
                            currentLeft,
                            currentTop,
                            viewWidth,
                            currentTop + viewHeight
                        )
                        currentLeft += viewWidth
                    }
                }
            }
        }
    }

}

data class StatusTagData(val text: String? = null, val icon: Drawable? = null)