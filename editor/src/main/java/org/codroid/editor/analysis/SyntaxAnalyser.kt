package org.codroid.editor.analysis

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.newSingleThreadContext
import org.codroid.editor.buffer.TextSequence
import org.codroid.textmate.EmbeddedLanguagesMap
import org.codroid.textmate.Registry
import org.codroid.textmate.TokenizeLineResult2
import org.codroid.textmate.Tokenizer
import org.codroid.textmate.grammar.StateStack
import org.codroid.textmate.theme.RawTheme
import java.nio.file.Path
import kotlin.io.path.extension

class SyntaxAnalyser(rawTheme: RawTheme) {
    companion object {
        var registry: Registry? = null
        private var resolver: Resolver? = null
    }

    init {
        if (registry == null || resolver == null) {
            resolver = Resolver(rawTheme)
            registry = Registry(resolver!!)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun analyze(
        sequence: TextSequence,
        path: Path
    ): Flow<Pair<String, TokenizeLineResult2>> {
        return flow {
            prepareTokenizer(path)?.run {
                var ruleStack = StateStack.Null
                for (line in sequence) {
                    val result = tokenizeLine2(line, ruleStack, 0)
                    emit(line to result)
                    ruleStack = result.ruleStack
                }
            }
        }.flowOn(newSingleThreadContext("Syntax analyzer: #${hashCode()}"))
    }

    private fun prepareTokenizer(path: Path): Tokenizer? {
        resolver?.run {
            var language = findLanguageByExtension(".${path.extension}")
            if (language == null) {
                language = findLanguageByFilename(path.fileName.toString())
            }
            if (language == null) {
                return null
            }
            val grammar = findGrammarByLanguage(language)
            val embeddedLanguages = EmbeddedLanguagesMap()
            if (grammar.embeddedLanguages.isNotEmpty()) {
                for (scopeName in grammar.embeddedLanguages.keys) {
                    val temp = grammar.embeddedLanguages[scopeName]
                    embeddedLanguages[scopeName] = language2id[temp ?: ""] ?: -1
                }
            }
            return registry?.loadGrammarWithEmbeddedLanguages(
                grammar.scopeName,
                language2id[language]!!,
                embeddedLanguages
            )
        }
        return null
    }
}