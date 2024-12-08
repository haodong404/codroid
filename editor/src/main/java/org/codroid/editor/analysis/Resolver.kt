package org.codroid.editor.analysis

import oniguruma.OnigLib
import org.codroid.textmate.RegistryOptions
import org.codroid.textmate.exceptions.TextMateException
import org.codroid.textmate.grammar.RawGrammar
import org.codroid.textmate.parseRawGrammar
import org.codroid.textmate.theme.RawTheme
import org.codroid.textmate.theme.ScopeName
import java.io.File

internal class Resolver(rawTheme: RawTheme) :
    RegistryOptions(theme = rawTheme, regexLib = OnigLib()) {


    override val loadGrammar: ((ScopeName) -> RawGrammar?)
        get() = grammar@{ scopeName ->
            registeredGrammar[scopeName]?.let {
                if (it.grammar == null) {
                    File(it.path).inputStream().buffered().use { input ->
                        it.grammar = parseRawGrammar(input, it.path)
                    }
                }
                return@grammar it.grammar
            }
            return@grammar null
        }

    fun findLanguageByExtension(fileExtension: String): String? {
        for (lang in registeredLanguage) {
            if (lang.extensions.isEmpty()) continue
            for (extension in lang.extensions) {
                if (extension == fileExtension) {
                    return lang.id
                }
            }
        }
        return null
    }

    fun findLanguageByFilename(filename: String): String? {
        for (lan in registeredLanguage) {
            if (lan.filenames.isEmpty()) continue
            for (filename_ in lan.filenames) {
                if (filename == filename_) {
                    return lan.id
                }
            }
        }
        return null
    }

    fun findGrammarByLanguage(language: String): GrammarRegistration {
        for (grammar in registeredGrammar.values) {
            if (grammar.language == language) {
                return grammar
            }
        }
        throw TextMateException("Could not findGrammarByLanguage for $language")
    }
}