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

package org.codroid.editor.ui.projectstruct

import com.chad.library.adapter.base.entity.node.BaseNode
import java.io.File
import java.util.Comparator

class FileTreeNode(element: File) {
    var element: File? = element

    var children: List<FileTreeNode>? = null

    var level = -1
    var isExpanded = false

    fun extendLeaf(leaf: FileTreeNode, data: List<File>) {
        element?.let {
            leaf.children = data.sortedWith(NatureComparable())
                .map {
                    val temp = FileTreeNode(it)
                    temp.level = leaf.level + 1
                    temp
                }
        }
    }

inner class NatureComparable : Comparator<File> {
    override fun compare(o1: File, o2: File): Int {
        if (o1.isDirectory && o2.isFile) {
            return -1
        } else if (o1.isFile && o2.isDirectory) {
            return 1
        }
        return o1.name.compareTo(o2.name)
    }
}

}