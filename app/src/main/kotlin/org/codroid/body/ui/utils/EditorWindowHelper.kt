package org.codroid.body.ui.utils

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import org.codroid.body.ui.main.EditorWindowAdapter
import org.codroid.body.ui.main.EditorWindowFragment
import org.codroid.body.ui.main.WindowTabAdapter
import java.nio.file.Path

/**
 * This is a helper class that combines changes and close events between window and tab.
 */
class EditorWindowHelper(private val viewpager: ViewPager2, private val tab: RecyclerView) {

    private var mCurrentPosition = 0

    private var tabAdapter: WindowTabAdapter = tab.adapter as WindowTabAdapter

    private var closeListener: ((Path, Int) -> Unit)? = null
    private var changedListener: ((Int, Int) -> Unit)? =
        null //The first int is the new position and the other is the old position.

    init {
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                change(position)
            }
        })

        tabAdapter.setOnItemClickListener { _, _, position ->
            change(position)
        }

        tabAdapter.setOnCloseListener { path, position ->
            removeWindow(position)
            closeListener?.invoke(path, position)
        }
    }

    /**
     * When the position has been changed.
     */
    fun change(position: Int) {
        if (mCurrentPosition != position) {
            changedListener?.invoke(position, mCurrentPosition)
            this.mCurrentPosition = position
            changePage()
        }
    }

    fun change(path: Path) {
        (viewpager.adapter as EditorWindowAdapter).findPositionByPath(path).let {
            if (it != -1) {
                change(it)
            }
        }
    }

    fun getCurrentPage() = getWindowAdapter().findFragmentByPosition(mCurrentPosition)

    private fun changePage() {
        if (viewpager.currentItem != mCurrentPosition)
            viewpager.setCurrentItem(mCurrentPosition, false)

        if (tabAdapter.currentPosition != mCurrentPosition) {
            var position = mCurrentPosition + (mCurrentPosition - tabAdapter.currentPosition)
            if (position < 0) {
                position = 0
            }
            tab.smoothScrollToPosition(position)
            tabAdapter.select(mCurrentPosition)
        }
    }

    private fun getWindowAdapter() = viewpager.adapter as EditorWindowAdapter

    /**
     * Add a new window.
     * @param path
     */
    fun newWindow(path: Path) {
        if (!isExists(path)) {
            getWindowAdapter().addFragments(listOf(EditorWindowFragment(path)))
            tabAdapter.add(path)
            shouldHidTabView()
        }
    }

    /**
     * Remove a window by position.
     */
    fun removeWindow(position: Int) {
        tabAdapter.removeAt(position)
        (viewpager.adapter as EditorWindowAdapter).removeAt(position)
        shouldHidTabView()
    }

    fun shouldHidTabView() {
        if (tabAdapter.itemCount > 1) {
            if (!tab.isVisible) {
                tab.visibility = View.VISIBLE
            }
        } else {
            if (tab.isVisible) {
                tab.visibility = View.GONE
            }
        }
    }

    fun isExists(path: Path): Boolean {
        return tabAdapter.items.contains(path)
    }
}