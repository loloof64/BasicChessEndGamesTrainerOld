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

package com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.antlr4;

// Generated from ScriptLanguage.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ScriptLanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ScriptLanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ScriptLanguageParser#scriptLanguage}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScriptLanguage(ScriptLanguageParser.ScriptLanguageContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericAssign}
	 * labeled alternative in {@link ScriptLanguageParser#variableAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericAssign(ScriptLanguageParser.NumericAssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanAssign}
	 * labeled alternative in {@link ScriptLanguageParser#variableAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanAssign(ScriptLanguageParser.BooleanAssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptLanguageParser#terminalExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerminalExpr(ScriptLanguageParser.TerminalExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericEquality}
	 * labeled alternative in {@link ScriptLanguageParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericEquality(ScriptLanguageParser.NumericEqualityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orComparison}
	 * labeled alternative in {@link ScriptLanguageParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrComparison(ScriptLanguageParser.OrComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditionalBooleanExpr}
	 * labeled alternative in {@link ScriptLanguageParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalBooleanExpr(ScriptLanguageParser.ConditionalBooleanExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanVariable}
	 * labeled alternative in {@link ScriptLanguageParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanVariable(ScriptLanguageParser.BooleanVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesisBooleanExpr}
	 * labeled alternative in {@link ScriptLanguageParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesisBooleanExpr(ScriptLanguageParser.ParenthesisBooleanExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andComparison}
	 * labeled alternative in {@link ScriptLanguageParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndComparison(ScriptLanguageParser.AndComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericRelational}
	 * labeled alternative in {@link ScriptLanguageParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericRelational(ScriptLanguageParser.NumericRelationalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptLanguageParser#fileConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileConstant(ScriptLanguageParser.FileConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptLanguageParser#rankConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankConstant(ScriptLanguageParser.RankConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code absoluteNumericExpr}
	 * labeled alternative in {@link ScriptLanguageParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAbsoluteNumericExpr(ScriptLanguageParser.AbsoluteNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesisNumericExpr}
	 * labeled alternative in {@link ScriptLanguageParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesisNumericExpr(ScriptLanguageParser.ParenthesisNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditionalNumericExpr}
	 * labeled alternative in {@link ScriptLanguageParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalNumericExpr(ScriptLanguageParser.ConditionalNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericVariable}
	 * labeled alternative in {@link ScriptLanguageParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericVariable(ScriptLanguageParser.NumericVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code plusMinusNumericExpr}
	 * labeled alternative in {@link ScriptLanguageParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlusMinusNumericExpr(ScriptLanguageParser.PlusMinusNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code litteralNumericExpr}
	 * labeled alternative in {@link ScriptLanguageParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLitteralNumericExpr(ScriptLanguageParser.LitteralNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankConstantNumericExpr}
	 * labeled alternative in {@link ScriptLanguageParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankConstantNumericExpr(ScriptLanguageParser.RankConstantNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fileConstantNumericExpr}
	 * labeled alternative in {@link ScriptLanguageParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileConstantNumericExpr(ScriptLanguageParser.FileConstantNumericExprContext ctx);
}