package org.codroid.editor

import org.codroid.editor.ui.projectstruct.FileTreeNode
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val root = FileTreeNode(1)
        val left = mutableListOf<Int>()
        for (i in 0..9) {
            left.add(i + 10)
        }

        root.insertAt(root, left.toList())

        left.clear()
        for (i in 0..9) {
            left.add(i + 20)
        }
        root.children?.get(0)?.let {
            root.insertAt(it, left)
        }
        print(root)
    }
}