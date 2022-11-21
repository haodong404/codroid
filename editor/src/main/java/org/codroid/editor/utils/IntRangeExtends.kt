package org.codroid.editor.utils

import kotlin.math.abs

fun IntRange.hasIntersection(b: IntRange): Boolean {
    return !(b.first - last > 0 || first - b.last > 0) || b.first == last || first == b.last
}

fun IntRange.length() = abs(last - first) + 1

fun IntRange.endExclusive() = last + 1

fun IntRange.offset(offset: Int) = (first + offset)..(last + offset)