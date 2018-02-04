/*
 * Basic Chess Endgames : generates a position of the endgame you want, then play it against computer.
    Copyright (C) 2017-2018  Laurent Bernabe <laurent.bernabe@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Code adapted from this gist :
 * https://gist.github.com/KetothXupack/bec735ad12016ea2b552a3503f80cd53
 */
package com.loloof64.android.basicchessendgamestrainer.position_generator_editor

import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.R
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.ParseCancellationException

class PositionConstraintBailErrorStrategy : DefaultErrorStrategy() {
    override fun recover(recognizer: Parser?, e: RecognitionException?) {
        throw ParseCancellationException(e)
    }

    override fun recoverInline(recognizer: Parser?): Token {
        val exception = InputMismatchException(recognizer)
        reportError(recognizer, exception)
        throw exception
    }

    override fun reportError(recognizer: Parser?, error: RecognitionException?) {
        when (error){
            is InputMismatchException -> throw ParseCancellationException(
                    buildInputMismatchExceptionMessage(recognizer, error), error
            )
            // This one should not happen
            is FailedPredicateException -> throw ParseCancellationException(
                    "Unexpected semantic predicate error", error
            )
            is NoViableAltException -> throw ParseCancellationException(
                    buildRecognitionExceptionMessage(error), error
            )
            else -> throw ParseCancellationException(
                    MyApplication.appContext.resources.getString(R.string.misc_parse_error), error
            )
        }
    }

    override fun sync(recognizer: Parser?) {

    }

    override fun getTokenErrorDisplay(token: Token?): String {
        return if (token == null) MyApplication.appContext.resources.getString(R.string.no_antlr4_token)
        else escapeWSAndQuote(String.format("<%s>", getErrorSymbol(token)))
    }

    private fun getErrorSymbol(token: Token): String {
        var symbol = getSymbolText(token)

        if (symbol == null) {
            symbol = if (tokenIsEOF(token)) {
                MyApplication.appContext.getString(R.string.antlr4_eof)
            } else {
                getSymbolType(token).toString()
            }
        }
        return symbol
    }

    private fun buildRecognitionExceptionMessage(error: NoViableAltException?) : String {
        val inputToken = escapeWSAndQuote(error?.offendingToken?.text)
        val line = error?.offendingToken?.line
        val positionInLine = error?.offendingToken?.charPositionInLine

        val messageString = MyApplication.appContext.resources.getString(R.string.no_viable_alt_exception)

        return String.format(messageString,
                inputToken, line, positionInLine)
    }

    private fun buildInputMismatchExceptionMessage(recognizer: Parser?, error: RecognitionException?): String {
        val line = error?.offendingToken?.line
        val positionInLine = error?.offendingToken?.charPositionInLine
        val tokenErrorDisplay = getTokenErrorDisplay(error?.offendingToken)
        val expectedToken = error?.expectedTokens?.toString(recognizer?.vocabulary)

        val messageString = MyApplication.appContext.resources.getString(R.string.input_mismatch_exception)

        return String.format(messageString,
                line, positionInLine, expectedToken, tokenErrorDisplay)
    }

    private fun tokenIsEOF(token: Token) : Boolean {
        return getSymbolType(token) == Token.EOF
    }
}