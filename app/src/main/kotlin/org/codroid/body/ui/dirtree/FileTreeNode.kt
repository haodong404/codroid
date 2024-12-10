package org.codroid.body.ui.dirtree

import java.io.File

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