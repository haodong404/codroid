package org.codroid.editor.buffer.linearr

import org.codroid.editor.buffer.TextSequence
import org.codroid.editor.config.TextBufferConfig
import java.io.InputStream
import kotlin.math.max

class LineArray : TextSequence {

    private val mBuffer: ArrayList<String> = ArrayList(20)
    private var length = 0

    constructor(inputStream: InputStream) : super(
        inputStream
    ) {
        val bytes = String(inputStream.readBytes(), TextBufferConfig.charset())
        for (i in bytes.lineSequence()) {
            mBuffer.add(i)
            // Each line has a line breaker(except the last line).
            length += i.length + 1
        }
        // There isn't a line breaker in the last line, so I subtracted 1.
        length--
    }

    constructor(charSequence: String) : super(charSequence) {
        for (i in charSequence.lineSequence()) {
            mBuffer.add(i)
            length += i.length + 1
        }
        length--
    }

    override fun rowAt(index: Int): String {
        return mBuffer[index]
    }

    override fun insert(content: String, position: Int) {
        var ptr = 0
        var row = 0
        var col = 0
        for ((idx, now) in mBuffer.withIndex()) {
            ptr += now.length
            if (ptr >= position) {
                row = idx
                col = position - ptr + now.length
                break
            }
            ptr++
        }
        insert(content, row, col)
    }

    override fun insert(content: String, row: Int, col: Int) {
        val old = mBuffer[row]
        var offset = row
        for ((index, now) in content.lineSequence().withIndex()) {
            if (index == 0) {
                mBuffer[row] = old.substring(0, col) + now
                length += now.length
            } else {
                mBuffer.add(row + index, now)
                length += now.length + 1
                offset++
            }
        }
        mBuffer[offset] =
            StringBuilder(mBuffer[offset])
                .append(old.substring(col, max(0, old.length)))
                .toString()
    }

    override fun delete(start: Int, end: Int) {
        var ptr = 0
        val builder = StringBuilder()
        var flag = false
        var startRow = 0
        var offset = 0
        for ((idx, i) in mBuffer.withIndex()) {
            ptr += i.length
            if (ptr in start..end) {
                if (!flag) {
                    builder.append(i)
                    startRow = idx
                    flag = true
                } else {
                    mBuffer.removeAt(idx)
                }
                builder.append(i)
                builder.append(TextBufferConfig.lineSeparator())
            } else if (flag) {
                builder.removeSuffix(TextBufferConfig.lineSeparator())
                break
            } else {
                offset += i.length
            }
        }
        builder.removeRange(start - offset, end - offset)

        length -= (end - start)

        if (builder.isEmpty()) {
            mBuffer.removeAt(startRow)
        } else {
            mBuffer[startRow] = ""
            insert(builder.toString(), startRow, 0)
        }
    }

    override fun replace(content: String, start: Int, end: Int) {
        TODO("Not yet implemented")
    }

    override fun length(): Int {
        return this.length
    }

    override fun rows(): Int {
        return mBuffer.size
    }

    override fun toString(): String {
        val result = StringBuffer()
        for ((idx, i) in mBuffer.withIndex()) {
            result.append(i)
            if (idx != mBuffer.size - 1) {
                result.append(TextBufferConfig.lineSeparator())
            }
        }
        return result.toString()
    }

}