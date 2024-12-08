package org.codroid.body.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import com.google.android.material.elevation.SurfaceColors
import org.codroid.body.R
import org.codroid.body.databinding.ViewInfoBarBinding
import org.codroid.body.dip2px
import org.codroid.body.ui.main.StatusInfoAdapter
import org.codroid.body.ui.main.StatusTagData
import org.codroid.body.ui.main.StatusTagLayoutManager
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class BalancingLayout : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxHeight = 0
        var usedWidth = 0
        // See how wide everyone is. Also remember max height.
        children.forEach { view ->
            measureChildWithMargins(view, widthMeasureSpec, usedWidth, heightMeasureSpec, 0)
            maxHeight = max(maxHeight, view.measuredHeight)
            usedWidth += view.measuredWidth
        }
        maxHeight += paddingTop + paddingBottom
        var availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        children.forEachIndexed { index, view ->
            val heightSize = maxHeight - paddingTop - paddingBottom
            val lp = view.layoutParams
            val heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
            var width = availableWidth
            if (index == 0) {
                width = min(width, availableWidth / 2)
            }
            var widthSpecMode = MeasureSpec.AT_MOST
            if (lp.width != LayoutParams.WRAP_CONTENT) {
                widthSpecMode = MeasureSpec.EXACTLY
                if (lp.width != LayoutParams.MATCH_PARENT) {
                    width = lp.width
                }
            }
            view.measure(MeasureSpec.makeMeasureSpec(width, widthSpecMode), heightSpec)
            availableWidth -= (view.measuredWidth + view.paddingLeft + view.paddingRight)
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxHeight)
    }
}

class InfoBar : LinearLayout {

    private val mBinding: ViewInfoBarBinding =
        ViewInfoBarBinding.inflate(LayoutInflater.from(context), this, true)

    private val mStatusRecyclerAdapter: StatusInfoAdapter = StatusInfoAdapter()

    constructor(context: Context) : super(context)
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

    private var isHitDividerHandle = false
    private var mLastX = 0
    private var mSatisfiedLeftWidth = 0
    private val mBounceBackAnimator = ValueAnimator.ofInt(0, 0).apply {
        duration = 150
        addUpdateListener {
            val params = mBinding.infoBarTitleRoot.layoutParams
            params.width = it.animatedValue as Int
            mBinding.infoBarTitleRoot.layoutParams = params
        }
    }

    private val mLeftBound: Int
        get() = width / 6

    private val mRightBound: Int
        get() = 3 * width / 5

    private fun initialize(context: Context, attrs: AttributeSet?) {
        val tv = context.obtainStyledAttributes(attrs, R.styleable.InfoBar)
        mBinding.title = tv.getString(R.styleable.InfoBar_android_title) ?: "Title"
        mBinding.subtitle = tv.getString(R.styleable.InfoBar_android_subtitle) ?: "Subtitle"
        mBinding.infoBarStatusRv.adapter = mStatusRecyclerAdapter
        mBinding.infoBarRoot.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(context))

        mStatusRecyclerAdapter.add(StatusTagData("UTF-8"))
        mStatusRecyclerAdapter.add(StatusTagData("LN2"))
        mStatusRecyclerAdapter.add(StatusTagData("Kotlin1"))
        val icon =
            ResourcesCompat.getDrawable(context.resources, R.drawable.ic_action_name, context.theme)
                ?.toBitmap()
        mStatusRecyclerAdapter.add(
            StatusTagData(
                "Kotlin2", icon
            )
        )
        mStatusRecyclerAdapter.add(StatusTagData("Kotlin3"))
        mStatusRecyclerAdapter.add(StatusTagData("Kotlin4"))
        mStatusRecyclerAdapter.add(StatusTagData("Kotlin5"))
        mStatusRecyclerAdapter.add(StatusTagData("Kotlin6"))
        mStatusRecyclerAdapter.add(StatusTagData("Kotlin7"))
        mStatusRecyclerAdapter.add(StatusTagData("Kotlin8"))
        mStatusRecyclerAdapter.add(StatusTagData("Kotlin9"))
        mBinding.infoBarStatusRv.layoutManager =
            StatusTagLayoutManager(2, context.dip2px(4f).toInt(), mBinding.infoBarOverflowedBadge) {
                mBinding.infoBarOverflowedBadge.setNumber(it.size)
            }

        mBinding.infoBarStatusRv.itemAnimator = null
        mBinding.infoBarStatusRv.stopScroll()
        tv.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mSatisfiedLeftWidth == 0) {
            mSatisfiedLeftWidth = mBinding.infoBarTitleRoot.width
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isHitDividerHandle =
                        isHitDividerHandle(ev.rawX.roundToInt(), ev.rawY.roundToInt())
                    if (isHitDividerHandle) {
                        return true
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (isHitDividerHandle) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isHitDividerHandle) {
            return false
        }
        event?.let { e ->
            return when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    mLastX = event.rawX.toInt()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val current = event.rawX.toInt()
                    moveHandle(current - mLastX)
                    mLastX = current
                    true
                }
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    if (mBinding.infoBarTitleRoot.width != mSatisfiedLeftWidth) {
                        mBounceBackAnimator.setIntValues(
                            mBinding.infoBarTitleRoot.width,
                            mSatisfiedLeftWidth
                        )
                        mBounceBackAnimator.start()
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
        return false
    }

    private fun moveHandle(diff: Int) {
        val params = mBinding.infoBarTitleRoot.layoutParams
        params.width = mBinding.infoBarTitleRoot.width + diff
        if (diff < 0) {
            if (params.width < mLeftBound) {
                return
            }
        } else if (diff > 0) {
            if (params.width > mRightBound) {
                return
            }
        }
        mBinding.infoBarTitleRoot.layoutParams = params
    }

    private fun isHitDividerHandle(x: Int, y: Int): Boolean {
        val rect = Rect()
        mBinding.infoBarDividerHandle.getGlobalVisibleRect(rect)
        return x in rect.left..rect.right && y in rect.top..rect.bottom
    }
}