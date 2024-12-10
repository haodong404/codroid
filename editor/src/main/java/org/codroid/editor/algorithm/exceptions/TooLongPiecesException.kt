package org.codroid.editor.algorithm.exceptions

class TooLongPiecesException : Exception() {
    override val message: String
        get() = "Too long pieces."
}