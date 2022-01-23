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
        return data[position].path
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