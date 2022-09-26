package org.codroid.editor.utils

fun IntRange.hasIntersection(b: IntRange): Boolean {
    return !(b.first - last > 0 || first - b.last > 0) || b.first == last || first == b.last
}