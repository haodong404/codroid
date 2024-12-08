package org.codroid.editor.algorithm

import org.codroid.editor.algorithm.linearr.LineArray
import org.codroid.editor.utils.makePair
import org.junit.Assert.assertEquals
import org.junit.Test

class LineArrayTest {

    private val mLineArray = LineArray(
        this.javaClass.classLoader!!.getResourceAsStream("AddonException")
    )

    @Test
    fun rowAt() {
        assertEquals("package org.codroid.interfaces.exceptions;", mLineArray.rowAt(0))
        assertEquals("}", mLineArray.rowAt(24))
        assertEquals("", mLineArray.rowAt(25))
    }


    @Test
    fun insert() {
        mLineArray.insert("Hello", 1, 0)
        assertEquals("Hello", mLineArray.rowAt(1))
        assertEquals(675, mLineArray.length())
        mLineArray.insert("World", 1, 5)
        assertEquals("HelloWorld", mLineArray.rowAt(1))
        assertEquals(680, mLineArray.length())

        mLineArray.insert("LINE", 42)
        assertEquals("package org.codroid.interfaces.exceptions;LINE", mLineArray.rowAt(0))
        assertEquals(684, mLineArray.length())
        mLineArray.insert("NEW", 47)
        assertEquals("NEWHelloWorld", mLineArray.rowAt(1))
        assertEquals(687, mLineArray.length())

        mLineArray.insert("END", mLineArray.length())
        assertEquals("END", mLineArray.rowAt(mLineArray.rows() - 1))
        assertEquals(690, mLineArray.length())
        mLineArray.insert("\n", mLineArray.length())
        assertEquals(691, mLineArray.length())
    }

    @Test
    fun insertionWithNewLines() {
        mLineArray.insert("Line1\nLine2", 0)
        assertEquals(27, mLineArray.rows())
        assertEquals(681, mLineArray.length())
        assertEquals("Line1", mLineArray.rowAt(0))
        assertEquals("Line2package org.codroid.interfaces.exceptions;", mLineArray.rowAt(1))

        mLineArray.insert("Line3\nLine4\nLine5\nLine6", mLineArray.length())
        assertEquals(30, mLineArray.rows())
        assertEquals(704, mLineArray.length())
        assertEquals("Line6", mLineArray.rowAt(29))
        assertEquals("Line5", mLineArray.rowAt(28))
        assertEquals("Line4", mLineArray.rowAt(27))
        assertEquals("Line3", mLineArray.rowAt(26))
    }

    @Test
    fun delete() {
        mLineArray.delete(0 until 42)
        assertEquals(26, mLineArray.rows())
        assertEquals(628, mLineArray.length())
        assertEquals("", mLineArray.rowAt(0))

        mLineArray.delete(0 until 1)
        assertEquals(25, mLineArray.rows())
        assertEquals(627, mLineArray.length())

        mLineArray.delete(0 until 2)
        assertEquals(23, mLineArray.rows())
        assertEquals(625, mLineArray.length())
        assertEquals("import org.codroid.interfaces.log.Logger;", mLineArray.rowAt(0))

        mLineArray.delete(33 until 58)
        assertEquals(20, mLineArray.rows())
        assertEquals(600, mLineArray.length())
        assertEquals(
            "import org.codroid.interfaces.loga superclass that should only be inherited by the exceptions about addon.",
            mLineArray.rowAt(0)
        )
    }


    @Test
    fun replace() {
        mLineArray.replace("Hello ", 0 until 0)
        assertEquals("Hello package org.codroid.interfaces.exceptions;", mLineArray.rowAt(0))
        assertEquals(26, mLineArray.rows())
        assertEquals(676, mLineArray.length())

        mLineArray.replace("LINE", 49 until 49)
        assertEquals("LINE", mLineArray.rowAt(1))
        assertEquals(26, mLineArray.rows())
        assertEquals(680, mLineArray.length())

        mLineArray.replace("Line2\nLine3\nLine4", 55 until 61)
        assertEquals(28, mLineArray.rows())
        assertEquals(691, mLineArray.length())
        assertEquals("Line2", mLineArray.rowAt(3))
        assertEquals("Line3", mLineArray.rowAt(4))
        assertEquals("Line4 org.codroid.interfaces.log.Logger;", mLineArray.rowAt(5))

        mLineArray.replace("RUIN", 0 until mLineArray.length())
        assertEquals(1, mLineArray.rows())
        assertEquals(4, mLineArray.length())
    }


    @Test
    fun length() {
        assertEquals(670, mLineArray.length())
    }

    @Test
    fun rows() {
        assertEquals(26, mLineArray.rows())
    }

    @Test
    fun testToString() {
        assertEquals(
            "package org.codroid.interfaces.exceptions;\n" +
                    "\n" +
                    "\n" +
                    "import org.codroid.interfaces.log.Logger;\n" +
                    "\n" +
                    "/**\n" +
                    " * This is a superclass that should only be inherited by the exceptions about addon.\n" +
                    " */\n" +
                    "public class AddonException extends Exception {\n" +
                    "\n" +
                    "    /**\n" +
                    "     * Print the stack trace by addon logger.\n" +
                    "     *\n" +
                    "     * @param logger which logger you want to use.\n" +
                    "     */\n" +
                    "    public void printStackTrace(Logger logger) {\n" +
                    "        StringBuilder builder = new StringBuilder();\n" +
                    "        builder.append(this.toString());\n" +
                    "        for (var i : getStackTrace()) {\n" +
                    "            builder.append(\"\\n\\tat \");\n" +
                    "            builder.append(i.toString());\n" +
                    "        }\n" +
                    "        logger.e(builder.toString());\n" +
                    "    }\n" +
                    "}\n", mLineArray.toString()
        )
    }

    @Test
    fun constructor2() {
        val seq = LineArray(
            "package org.codroid.interfaces.exceptions;\n" +
                    "\n" +
                    "\n" +
                    "import org.codroid.interfaces.log.Logger;\n" +
                    "\n" +
                    "/**\n" +
                    " * This is a superclass that should only be inherited by the exceptions about addon.\n" +
                    " */"
        )

        seq.delete(1 until 0)
        assertEquals(8, seq.rows())
        assertEquals(43 + 2 + 43 + 4 + 85 + 3, seq.length())
        assertEquals("package org.codroid.interfaces.exceptions;", seq.rowAt(0))
    }

    @Test
    fun testIterator() {
        val seq = LineArray(
            "package org.codroid.interfaces.exceptions;\n" +
                    "\n" +
                    "\n" +
                    "import org.codroid.interfaces.log.Logger;\n" +
                    "\n" +
                    "/**\n" +
                    " * This is a superclass that should only be inherited by the exceptions about addon.\n" +
                    " */"
        )

        for ((index, line) in seq.withIndex()) {
            assertEquals(seq.rowAt(index), line)
        }
    }

    @Test
    fun `test charIndex`() {
        val seq = LineArray(
            "package org.codroid.interfaces.exceptions;\n" +
                    "\n" +
                    "\n" +
                    "import org.codroid.interfaces.log.Logger;\n" +
                    "\n" +
                    "/**\n" +
                    " * This is a superclass that should only be inherited by the exceptions about addon.\n" +
                    " */"
        )
        assertEquals(-1, seq.charIndex(0, 0))
        assertEquals(0, seq.charIndex(0, 1))
        assertEquals(5, seq.charIndex(0, 6))
        assertEquals(41, seq.charIndex(0, 42))
        assertEquals(42, seq.charIndex(1, 0)) // New line
        assertEquals(47, seq.charIndex(3, 3))
        assertEquals(179, seq.charIndex(7, 3))
    }

    @Test
    fun `test get row and col`() {
        val seq = LineArray(
            "package org.codroid.interfaces.exceptions;\n" +
                    "\n" +
                    "\n" +
                    "import org.codroid.interfaces.log.Logger;\n" +
                    "\n" +
                    "/**\n" +
                    " * This is a superclass that should only be inherited by the exceptions about addon.\n" +
                    " */"
        )
        assertEquals(makePair(0, 0), seq.getRowAndCol(-1))
        assertEquals(makePair(0, 1), seq.getRowAndCol(0))
        assertEquals(makePair(0, 6), seq.getRowAndCol(5))
        assertEquals(makePair(1, 0), seq.getRowAndCol(42))
        assertEquals(makePair(3, 3), seq.getRowAndCol(47))
        assertEquals(makePair(7, 3), seq.getRowAndCol(179))
    }

    @Test
    fun `can map after editing`() {
        val seq = LineArray(
            "package org.codroid.interfaces.exceptions;\n" +
                    "\n" +
                    "\n" +
                    "import org.codroid.interfaces.log.Logger;\n" +
                    "\n" +
                    "/**\n" +
                    " * This is a superclass that should only be inherited by the exceptions about addon.\n" +
                    " */"
        )
        assertEquals(42, seq.charIndex(1, 0))
        assertEquals(makePair(1, 0), seq.getRowAndCol(42))
        seq.insert("Hello", 0)
        assertEquals(47, seq.charIndex(1, 0))
        assertEquals(makePair(1, 0), seq.getRowAndCol(47))
        seq.insert("World", 48)
        assertEquals(52, seq.charIndex(1, 5))
        assertEquals(makePair(1, 5), seq.getRowAndCol(52))
        seq.delete(0 until 50)
        assertEquals(seq.length() - 1, seq.charIndex(6, 3))
        assertEquals(makePair(6, 3), seq.getRowAndCol(seq.length() - 1))
    }
}