package org.codroid.body.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.codroid.body.ui.dirtree.FileTreeNode
import java.io.File

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