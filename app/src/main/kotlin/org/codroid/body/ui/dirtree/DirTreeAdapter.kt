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

package org.codroid.body.ui.dirtree

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import kotlinx.coroutines.*
import org.codroid.body.R
import org.codroid.body.databinding.ItemDirTreeBinding
import org.codroid.body.ui.FileItem
import org.codroid.body.widgets.DirTreeItemView
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.evnet.EventCenter
import org.codroid.interfaces.evnet.editor.DirTreeItemLoadEvent
import org.codroid.interfaces.evnet.entities.DirTreeItemEntity

class DirTreeAdapter :
    BaseQuickAdapter<FileTreeNode, DataBindingHolder<ItemDirTreeBinding>>() {


    class ViewHolder(
        parent: ViewGroup, val binding: ItemDirTreeBinding = ItemDirTreeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(parent)

    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        var dirStandardBitmap: Bitmap? = null
        var fileStandardBitmap: Bitmap? = null
    }

    private fun initIcon(type: String): Bitmap {
        // Init the icon bitmap
        return when (type) {
            DirTreeItemView.DIRECTORY -> dirStandardBitmap!!
            else -> fileStandardBitmap!!
        }
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemDirTreeBinding>,
        position: Int,
        item: FileTreeNode?
    ) {
        if (dirStandardBitmap == null) {
            dirStandardBitmap = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_twotone_folder_open_24,
                context.theme
            )?.toBitmap()!!
        }

        if (fileStandardBitmap == null) {
            fileStandardBitmap = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_twotone_description_24,
                context.theme
            )?.toBitmap()!!
        }

        var addon: DirTreeItemEntity? = null

        val itemView = holder.binding.dirTreeItem

        var type = DirTreeItemView.FILE

        if (item?.element?.isDirectory == true) {
            type = DirTreeItemView.DIRECTORY
        }

        holder.binding.item =
            item?.element?.name?.let {
                FileItem(it, null, Color.BLACK, type, item.isExpanded, item.level)
            }

        scope.launch {
            withContext(Dispatchers.Default) {
                AddonManager.get().eventCenter()
                    .executeStream<DirTreeItemLoadEvent>(EventCenter.EventsEnum.PROJECT_STRUCT_ITEM_LOAD)
                    .forEach {
                        try {
                            it?.beforeLoading(item?.element)?.let { entity ->
                                addon = entity
                            }
                        } catch (e: Exception) {
                            AddonManager.get().logger.e("An event: ${it.javaClass.name} execute failed! ( ${e.message} )")
                            e.printStackTrace()
                        }
                    }

                if (addon?.icon != null) {
                    addon?.icon?.let {
                        itemView.setImageBitmap(it.toBitmap())
                    }
                } else {
                    itemView.setImageBitmap(initIcon(type))
                }

                addon?.let { it ->

                    if (addon?.tagIcon != null) {
                        itemView.setTagBitmap(addon?.tagIcon?.toBitmap())
                    } else {
                        itemView.setTagBitmap(null)
                    }

                    it.title?.let { str ->
                        itemView.setTitle(str)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemDirTreeBinding> = DataBindingHolder(
        ItemDirTreeBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
    )

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        scope.cancel()
    }

    fun close(position: Int) {
        val now = position + 1
        val parentNode = getItem(position)
        while (now > 0) {
            if ((getItem(now)?.level ?: 0) > (parentNode?.level ?: 0)) {
                removeAt(now)
            } else {
                break
            }
        }
        parentNode?.isExpanded = false
    }

    fun expand(position: Int, leaf: List<FileTreeNode>) {
        addAll(position + 1, leaf)
        getItem(position)?.isExpanded = true
    }
}