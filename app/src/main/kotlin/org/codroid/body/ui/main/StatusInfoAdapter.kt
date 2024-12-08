package org.codroid.body.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import org.codroid.body.databinding.ItemStatusInfoBinding
import org.codroid.body.widgets.Badge

class StatusInfoAdapter :
    BaseQuickAdapter<StatusTagData, DataBindingHolder<ItemStatusInfoBinding>>() {

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemStatusInfoBinding>,
        position: Int,
        item: StatusTagData?
    ) {
        holder.binding.run {
            val tag = itemStatusInfoTag
            item?.text?.let {
                tag.setText(it)
            }
            item?.icon?.let {
                tag.setBitmap(it)
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemStatusInfoBinding> = DataBindingHolder(
        ItemStatusInfoBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
    )
}

class StatusTagLayoutManager(
    private val maxLine: Int,
    private val gap: Int = 0,
    private val badge: Badge? = null,
    private val overflow: ((List<View>) -> Unit)? = null
) :
    RecyclerView.LayoutManager() {

    init {
        badge?.setMaxNumber(99)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )

    override fun isAutoMeasureEnabled(): Boolean = true

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ) {
        recycler?.let { detachAndScrapAttachedViews(it) }
        var lineCount = 1
        var currentLeft = 0
        var currentTop = 0
        var isFirstInALine = true
        badge?.visibility = View.GONE
        for (i in 0 until itemCount) {
            recycler?.getViewForPosition(i)?.let { view ->
                measureChildWithMargins(view, 0, 0)
                val viewWidth = getDecoratedMeasuredWidth(view)
                val viewHeight = getDecoratedMeasuredHeight(view)
                var right = currentLeft + viewWidth

                var rightBound = width - 10
                // If current is the last view, we don't need to hide it.
                if (lineCount == maxLine && i + 1 < itemCount) {
                    rightBound -= badge?.measuredWidth ?: 0
                }
                // Place it if the space is enough.
                if (right < rightBound) {
                    if (isFirstInALine) {
                        isFirstInALine = false
                    } else {
                        currentLeft += gap
                        right += gap
                    }
                    addView(view)
                    layoutDecorated(
                        view,
                        currentLeft,
                        currentTop,
                        right,
                        currentTop + viewHeight
                    )
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
                            onOverflowed(overflowedViews.size)
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

    private fun onOverflowed(size: Int) {
        badge?.run {
            visibility = View.VISIBLE
            setNumber(size)
        }
    }

}

data class StatusTagData(val text: String? = null, val icon: Bitmap? = null)