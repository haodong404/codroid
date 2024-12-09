package org.codroid.editor.utils

import org.codroid.editor.decoration.*
import org.codroid.editor.graphics.TextPaint
import java.util.*
import kotlin.math.abs

class Block(substring: String = "") {

    private var mSpans: Decorator.Spans? = null
    private var mCharacterSpan: CharacterSpan? = null

    private val mSubstring = StringBuilder(substring)

    fun getSpans() = mSpans

    fun getCharacterSpan() = mCharacterSpan

    fun getAssembledSpans() = overrideSpans(mCharacterSpan)

    private fun overrideSpans(span: SpanDecoration?): Decorator.Spans? {

        if (span == null) return mSpans

        mSpans = Decorator.Spans()

        if (span is RepaintSpan) {
            mSpans!!.repaint = span
        }

        if (span is BackgroundSpan) {
            mSpans!!.background.add(span)
        }

        if (span is ForegroundSpan) {
            mSpans!!.foreground.add(span)
        }

        if (span is ReplacementSpan) {
            mSpans!!.replacement.add(span)
        }
        return mSpans
    }

    fun getSubstring() = mSubstring.toString()

    fun isEmpty() = mSubstring.isEmpty()

    fun appendChar(char: Char) {
        mSubstring.append(char)
    }

    fun setSpans(spans: Decorator.Spans) {
        this.mSpans = spans
    }

    fun setCharacterSpan(span: CharacterSpan) {
        this.mCharacterSpan = span
    }

    override fun toString(): String {
        return "Row($mSubstring, ${mSpans.toString()})"
    }
}

fun disassembleSpan(span: SpanDecoration, out: Decorator.Spans) {
    if (span is RepaintSpan) {
        val temp = out.repaint
        if (temp != null) {
            out.repaint = object : RepaintSpan {
                override fun onRepaint(origin: TextPaint): TextPaint {
                    return temp.onRepaint(origin)
                }
            }
        } else {
            out.repaint = span
        }
    }
    if (span is ForegroundSpan) {
        out.foreground.addLast(span)
    }
    if (span is BackgroundSpan) {
        out.background.addLast(span)
    }
    if (span is ReplacementSpan) {
        out.replacement.addLast(span)
    }
}

data class Row(val blocks: LinkedList<Block> = LinkedList(), var selection: IntPair = 0u) {

    fun appendBlock(block: Block) {
        if (!block.isEmpty()) {
            blocks.add(block)
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (item in blocks) {
            builder.append(item.getSubstring())
        }
        return builder.toString()
    }

}

/**
 * A description for a content.
 * @param name the full name, e.g. name.cpp, or Makefile
 * @param extension e.g. cpp (without the .(dot)), or an empty string if it's not exists.
 */
data class ContentDescription(val name: String, val extension: String)