package org.codroid.editor.utils

fun IntRange.isIn(other: IntRange): Boolean = this.first >= other.first && this.last <= other.last

fun IntRange.hasIntersection(b: IntRange): Boolean {
    return !(b.first - last > 0 || first - b.last > 0)
}