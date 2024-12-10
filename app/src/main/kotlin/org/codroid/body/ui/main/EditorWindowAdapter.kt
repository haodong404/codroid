package org.codroid.body.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.nio.file.Path

class EditorWindowAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val data: MutableList<EditorWindowFragment> by lazy {
        mutableListOf()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun createFragment(position: Int): Fragment {
        return data[position]
    }

    fun addFragments(fragments: List<EditorWindowFragment>) {
        data.addAll(fragments)
        notifyItemInserted(data.size)
    }

    fun removeAt(position: Int) {
        if (position > data.size) {
            return
        }
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size - position)
    }

    override fun containsItem(itemId: Long): Boolean {
        return data.map {
            it.hashCode().toLong()
        }.contains(itemId)
    }

    override fun getItemId(position: Int): Long {
        return data[position].hashCode().toLong()
    }

    fun findPathByPosition(position: Int): Path {
        return findFragmentByPosition(position).path
    }

    fun findFragmentByPosition(position: Int): EditorWindowFragment {
        return data[position]
    }

    fun findPositionByPath(path: Path): Int {
        for (i in 0 until data.size){
            if (data[i].path == path) {
                return i
            }
        }
        return -1
    }
}