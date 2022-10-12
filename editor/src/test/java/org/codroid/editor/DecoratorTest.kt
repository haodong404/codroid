package org.codroid.editor

import android.graphics.Canvas
import org.codroid.editor.decoration.*
import org.codroid.editor.graphics.TextPaint
import org.codroid.editor.utils.disassembleSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test


class PaintSpanAbs : RepaintSpan {
    override fun onRepaint(origin: TextPaint): TextPaint {
        return origin
    }
}

class BackgroundAbs : BackgroundSpan {
    override fun onDraw(canvas: Canvas, rect: SpanRect) {

    }
}

class DecoratorTest {

    private val characterSpans = arrayOf(
        PaintSpanAbs(), PaintSpanAbs(), PaintSpanAbs(), PaintSpanAbs(), PaintSpanAbs()
    )

    private val backgroundSpans = arrayOf(
        BackgroundAbs(), BackgroundAbs(), BackgroundAbs(), BackgroundAbs(), BackgroundAbs()
    )

    private fun flatSpan(
        spans: Map<IntRange, SpanDecoration>,
        size: Int
    ): ArrayList<Decorator.Spans> {
        return spans.flatMap {
            val result = ArrayList<Decorator.Spans>(size)
            val temp = Decorator.Spans()
            disassembleSpan(it.value, temp)
            for (i in it.key) {
                result.add(temp)
            }
            result
        } as ArrayList
    }

    @Test
    fun `Set Spans using a RowNode`() {
        val spans = mapOf(
            0..2 to characterSpans[0],
            3..3 to characterSpans[1],
        )
        val spans2 = mapOf(
            0..6 to characterSpans[2],
            7..8 to characterSpans[3],
        )
        val decorator = Decorator()
        decorator.setSpan(null, spans)
        decorator.setSpan(null, spans2)

        assertFalse(decorator.spanDecorations().empty())
        assertEquals(2, decorator.spanDecorations().size())
        assertEquals(flatSpan(spans, spans.size), decorator.spanDecorations().nodeAt(0)?.value)
        assertEquals(flatSpan(spans2, spans.size), decorator.spanDecorations().nodeAt(1)?.value)
    }

    @Test
    fun `Set Spans using index`() {
        val spans = mapOf(
            0..2 to characterSpans[0],
            3..3 to characterSpans[1],
        )
        val spans2 = mapOf(
            0..6 to characterSpans[2],
            7..8 to characterSpans[3],
        )
        val decorator = Decorator()
        decorator.setSpan(0, spans)
        decorator.setSpan(1, spans2)

        assertFalse(decorator.spanDecorations().empty())
        assertEquals(2, decorator.spanDecorations().size())
        assertEquals(flatSpan(spans, spans.size), decorator.spanDecorations().nodeAt(0)?.value)
        assertEquals(flatSpan(spans2, spans.size), decorator.spanDecorations().nodeAt(1)?.value)
    }

    @Test
    fun `Add Spans at the first time`() {
        val spans = mapOf(
            0..2 to characterSpans[0],
            3..3 to characterSpans[1],
        )
        val spans2 = mapOf(
            0..6 to characterSpans[2],
            7..8 to characterSpans[3],
        )
        val decorator = Decorator()
        decorator.setSpan(null, spans)
        decorator.setSpan(null, spans2)

        assertEquals(flatSpan(spans, spans.size), decorator.spanDecorations().nodeAt(0)?.value)

        decorator.addSpan(0, 0, 4, characterSpans[4])
        assertEquals(
            flatSpan(mapOf(0..3 to characterSpans[4]), spans.size),
            decorator.spanDecorations().nodeAt(0)?.value
        )

        decorator.addSpan(0, 0, 23, characterSpans[1])
        assertEquals(
            flatSpan(mapOf(0..3 to characterSpans[1]), spans.size),
            decorator.spanDecorations().nodeAt(0)?.value
        )
        assertEquals(
            flatSpan(mapOf(0..8 to characterSpans[1]), spans2.size),
            decorator.spanDecorations().nodeAt(1)?.value
        )
    }

    @Test
    fun `Add Spans at existing position`() {
        val spans = mapOf(
            0..2 to backgroundSpans[0],
            3..3 to backgroundSpans[1],
        )
        val spans2 = mapOf(
            0..6 to backgroundSpans[2],
            7..8 to backgroundSpans[3],
        )
        val decorator = Decorator()
        decorator.setSpan(null, spans)
        decorator.setSpan(null, spans2)

        assertEquals(flatSpan(spans, spans.size), decorator.spanDecorations().nodeAt(0)?.value)

        decorator.addSpan(0, 2, 1, backgroundSpans[4])
        assertEquals(
            1,
            decorator.spanDecorations().nodeAt(0)?.value?.getOrNull(0)?.background?.size
        )

        assertEquals(
            1,
            decorator.spanDecorations().nodeAt(0)?.value?.getOrNull(1)?.background?.size
        )

        assertEquals(
            2,
            decorator.spanDecorations().nodeAt(0)?.value?.getOrNull(2)?.background?.size
        )
        assertEquals(
            1,
            decorator.spanDecorations().nodeAt(0)?.value?.getOrNull(3)?.background?.size
        )
    }
}