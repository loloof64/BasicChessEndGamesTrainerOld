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

package com.loloof64.android.basicchessendgamestrainer.position_generator_editor.antlr4.single_king_constraint.generated;// Generated from SingleKingConstraint.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SingleKingConstraintParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SingleKingConstraintVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SingleKingConstraintParser#singleKingConstraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleKingConstraint(SingleKingConstraintParser.SingleKingConstraintContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericAssign}
	 * labeled alternative in {@link SingleKingConstraintParser#variableAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericAssign(SingleKingConstraintParser.NumericAssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanAssign}
	 * labeled alternative in {@link SingleKingConstraintParser#variableAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanAssign(SingleKingConstraintParser.BooleanAssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericEquality}
	 * labeled alternative in {@link SingleKingConstraintParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericEquality(SingleKingConstraintParser.NumericEqualityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rangeCheck}
	 * labeled alternative in {@link SingleKingConstraintParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRangeCheck(SingleKingConstraintParser.RangeCheckContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orComparison}
	 * labeled alternative in {@link SingleKingConstraintParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrComparison(SingleKingConstraintParser.OrComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanVariable}
	 * labeled alternative in {@link SingleKingConstraintParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanVariable(SingleKingConstraintParser.BooleanVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesisBooleanExpr}
	 * labeled alternative in {@link SingleKingConstraintParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesisBooleanExpr(SingleKingConstraintParser.ParenthesisBooleanExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andComparison}
	 * labeled alternative in {@link SingleKingConstraintParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndComparison(SingleKingConstraintParser.AndComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericRelational}
	 * labeled alternative in {@link SingleKingConstraintParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericRelational(SingleKingConstraintParser.NumericRelationalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SingleKingConstraintParser#rangeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRangeValue(SingleKingConstraintParser.RangeValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SingleKingConstraintParser#fileRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileRange(SingleKingConstraintParser.FileRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SingleKingConstraintParser#rankRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankRange(SingleKingConstraintParser.RankRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SingleKingConstraintParser#fileConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileConstant(SingleKingConstraintParser.FileConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link SingleKingConstraintParser#rankConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankConstant(SingleKingConstraintParser.RankConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code absoluteNumericExpr}
	 * labeled alternative in {@link SingleKingConstraintParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAbsoluteNumericExpr(SingleKingConstraintParser.AbsoluteNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesisNumericExpr}
	 * labeled alternative in {@link SingleKingConstraintParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesisNumericExpr(SingleKingConstraintParser.ParenthesisNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numericVariable}
	 * labeled alternative in {@link SingleKingConstraintParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericVariable(SingleKingConstraintParser.NumericVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code plusMinusNumericExpr}
	 * labeled alternative in {@link SingleKingConstraintParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlusMinusNumericExpr(SingleKingConstraintParser.PlusMinusNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code litteralNumericExpr}
	 * labeled alternative in {@link SingleKingConstraintParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLitteralNumericExpr(SingleKingConstraintParser.LitteralNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rankConstantNumericExpr}
	 * labeled alternative in {@link SingleKingConstraintParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRankConstantNumericExpr(SingleKingConstraintParser.RankConstantNumericExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fileConstantNumericExpr}
	 * labeled alternative in {@link SingleKingConstraintParser#numericExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileConstantNumericExpr(SingleKingConstraintParser.FileConstantNumericExprContext ctx);
}