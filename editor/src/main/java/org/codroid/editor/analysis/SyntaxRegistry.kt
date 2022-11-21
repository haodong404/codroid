package org.codroid.editor.analysis

import org.codroid.textmate.grammar.RawGrammar
import org.codroid.textmate.theme.ScopeName

internal val registeredGrammar = mutableMapOf<ScopeName, GrammarRegistration>()
internal val registeredLanguage = mutableSetOf<LanguageRegistration>()
internal val language2id = mutableMapOf<String, Int>()
internal var lastLanguage = 0


fun registerGrammar(registration: GrammarRegistration) {
    registeredGrammar[registration.scopeName] = registration
}

fun registerLanguage(registration: LanguageRegistration) {
    registeredLanguage.add(registration)
    lastLanguage++
    language2id[registration.id] = lastLanguage
}

data class GrammarRegistration(
    val language: String = "",
    val scopeName: ScopeName = "",
    var path: String = "",
    val embeddedLanguages: Map<String, String> = emptyMap(),
    var grammar: RawGrammar? = null
)

data class LanguageRegistration(
    val id: String = "",
    val extensions: List<String> = emptyList(),
    val filenames: List<String> = emptyList()
)

