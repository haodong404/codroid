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

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import org.codroid.body.databinding.ItemWindowTabBinding
import org.codroid.body.widgets.WindowTab
import java.nio.file.Path

class WindowTabAdapter :
    BaseQuickAdapter<Path, DataBindingHolder<ItemWindowTabBinding>>() {

    var currentPosition = 0
    private var closeListener: ((Path, Int) -> Unit?)? = null

    fun setOnCloseListener(listener: (path: Path, position: Int) -> Unit) {
        this.closeListener = listener
    }

    fun select(position: Int) {
        notifyItemChanged(position)
        notifyItemChanged(currentPosition)
        this.currentPosition = position
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemWindowTabBinding>,
        position: Int,
        item: Path?
    ) {
        holder.binding.path = item
        (holder.binding.root as WindowTab).apply {
            setIsSelected(holder.layoutPosition == currentPosition)
            this.setOnCloseListener {
                item?.let { path ->
                    closeListener?.invoke(path, holder.layoutPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemWindowTabBinding> = DataBindingHolder(
        ItemWindowTabBinding.inflate(
            LayoutInflater.from(context),parent, false
        )
    )

}