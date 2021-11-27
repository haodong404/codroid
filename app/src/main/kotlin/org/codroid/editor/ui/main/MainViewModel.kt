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

package org.codroid.editor.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.chad.library.adapter.base.provider.BaseNodeProvider
import kotlinx.coroutines.delay
import org.codroid.editor.ui.projectstruct.FileTreeNode
import org.codroid.interfaces.utils.PathUtils
import java.io.File
import java.nio.file.Paths

class MainViewModel : ViewModel() {

    private var currentDir = ""
    private lateinit var rootNode: FileTreeNode

    fun openDir(rootPath: String) = liveData {
        rootNode = FileTreeNode(File(rootPath))
        currentDir = rootPath
        File(rootPath).apply {
            if (exists()) {
                listFiles()?.let {
                    rootNode.extendLeaf(rootNode, it.toList())
                    emit(rootNode.children)
                }
            }
        }
    }


    fun nextDir(currentNode: FileTreeNode) = liveData {
        currentNode.element?.let {
            File(it.path).apply {
                if (exists()) {
                    listFiles()?.let { it ->
                        rootNode.extendLeaf(currentNode, it.toList())
                        emit(currentNode.children)
                    }
                }
            }
        }
    }
}