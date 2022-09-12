package org.codroid.editor.utils

typealias IntPair = ULong

fun IntPair.first(): Int = ((this and 0xFFFFFFFF00000000U) shr 32).toInt()

fun IntPair.second(): Int = (this and 0xFFFFFFFFU).toInt()

fun makePair(first: Int, second: Int): IntPair = (first.toULong() shl 32) or second.toULong()
