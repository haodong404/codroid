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

package org.codroid.editor.ui.dirtree

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import kotlinx.coroutines.*
import org.codroid.editor.R
import org.codroid.editor.databinding.ItemDirTreeBinding
import org.codroid.editor.ui.FileItem
import org.codroid.editor.widgets.DirTreeItemView
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.evnet.EventCenter
import org.codroid.interfaces.evnet.editor.ProjectStructItemLoadEvent
import org.codroid.interfaces.evnet.entities.ProjectStructItemEntity
import java.lang.Exception

class DirTreeAdapter() :
    BaseQuickAdapter<FileTreeNode, BaseDataBindingHolder<ItemDirTreeBinding>>(R.layout.item_dir_tree) {

    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        var dirStandardBitmap: Bitmap? = null
        var fileStandardBitmap: Bitmap? = null
    }

    override fun convert(
        holder: BaseDataBindingHolder<ItemDirTreeBinding>,
        item: FileTreeNode
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

        var addon: ProjectStructItemEntity? = null

        val itemView = holder.dataBinding?.dirTreeItem

        var type = DirTreeItemView.FILE

        if (item.element?.isDirectory == true) {
            type = DirTreeItemView.DIRECTORY
        }

        holder.dataBinding?.item =
            item.element?.name?.let {
                FileItem(it, null, Color.BLACK, type, item.isExpanded, item.level)
            }

        scope.launch {
            withContext(Dispatchers.Default) {
                AddonManager.get().eventCenter()
                    .executeStream<ProjectStructItemLoadEvent>(EventCenter.EventsEnum.PROJECT_STRUCT_ITEM_LOAD)
                    .forEach {
                        try {
                            it?.beforeLoading(item.element)?.let { entity ->
                                addon = entity
                            }
                        } catch (e: Exception) {
                            AddonManager.get().logger.e("An event: ${it.javaClass.name} execute failed! ( ${e.message} )")
                            e.printStackTrace()
                        }
                    }

                if (addon?.icon != null) {
                    addon?.icon?.let {
                        itemView?.setImageBitmap(it.toBitmap())
                    }
                } else {
                    itemView?.setImageBitmap(initIcon(type))
                }

                addon?.let { it ->

                    if (addon?.tagIcon != null) {
                        itemView?.setTagBitmap(addon?.tagIcon?.toBitmap())
                    } else {
                        itemView?.setTagBitmap(null)
                    }

                    it.title?.let { str ->
                        itemView?.setTitle(str)
                    }
                }
            }
        }
    }

    private fun initIcon(type: String): Bitmap {
        // Init the icon bitmap
        return when (type) {
            DirTreeItemView.DIRECTORY -> dirStandardBitmap!!
            else -> fileStandardBitmap!!
        }
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        scope.cancel()
    }

    fun close(position: Int) {
        var now = position + 1
        val parentNode = getItem(position)
        while (now > 0) {
            if (getItem(now).level > parentNode.level) {
                removeAt(now)
            } else {
                break
            }
        }
        parentNode.isExpanded = false
    }

    fun expand(position: Int, leaf: List<FileTreeNode>) {
        addData(position + 1, leaf)
        getItem(position).isExpanded = true
    }
}