package org.codroid.editor.analysis

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.newSingleThreadContext
import org.codroid.editor.buffer.TextSequence
import org.codroid.editor.utils.IntPair
import org.codroid.editor.utils.makePair
import org.codroid.textmate.EmbeddedLanguagesMap
import org.codroid.textmate.Registry
import org.codroid.textmate.TokenizeLineResult2
import org.codroid.textmate.Tokenizer
import org.codroid.textmate.grammar.StateStack
import org.codroid.textmate.theme.RawTheme
import java.nio.file.Path
import java.util.*
import kotlin.io.path.extension

@OptIn(DelicateCoroutinesApi::class)
class SyntaxAnalyser(rawTheme: RawTheme, private val mSequence: TextSequence, path: Path) {

    // It presents the end position of all the tokenized text.
    private var mLastEnd = 0
    private val mStateStacks = TreeMap<Int, StateStack>()
    private val mTokenizer: Tokenizer?
    private val mThreadContext = newSingleThreadContext("Syntax analyzer: #${hashCode()}")

    companion object {
        var registry: Registry? = null
        private var resolver: Resolver? = null
    }

    init {
        if (registry == null || resolver == null) {
            resolver = Resolver(rawTheme)
            registry = Registry(resolver!!)
        }
        mTokenizer = prepareTokenizer(path)
    }

    suspend fun analyze(startRow: Int = 0): Flow<Pair<IntPair, TokenizeLineResult2>> {
        // IntPair: first -> index of current row, second -> length of current row.
        mLastEnd = 0
        return flow {
            mTokenizer?.run {
                var ruleStack = findStateStack(startRow)
                for (rowIndex in startRow until mSequence.rows()) {
                    val current = mSequence.rowAt(rowIndex)
                    val result = tokenizeLine2(current, ruleStack, 0)
                    emit(makePair(mLastEnd, current.length) to result)
                    ruleStack = result.ruleStack
                    mStateStacks[startRow + rowIndex] = result.ruleStack
                    mLastEnd += current.length
                }
            }
        }.flowOn(mThreadContext)
    }

    private fun findStateStack(rowIndex: Int): StateStack {
        return mStateStacks[rowIndex - 1] ?: StateStack.Null
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