package org.codroid.editor.config

import java.nio.charset.Charset

class TextBufferConfig {
    companion object {
        fun charset(): Charset {
            return Charsets.UTF_8
        }

        fun lineSeparator(): String {
            return "\n"
        }
    }
}