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

package com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.antlr4;

// Generated from SingleKingConstraint.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SingleKingConstraintParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, NumericLitteral=36, ID=37, WS=38;
	public static final int
		RULE_singleKingConstraint = 0, RULE_variableAssign = 1, RULE_terminalExpr = 2, 
		RULE_booleanExpr = 3, RULE_fileConstant = 4, RULE_rankConstant = 5, RULE_numericExpr = 6;
	public static final String[] ruleNames = {
		"singleKingConstraint", "variableAssign", "terminalExpr", "booleanExpr", 
		"fileConstant", "rankConstant", "numericExpr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "':='", "';'", "'return'", "'('", "')'", "'if'", "'then'", "'else'", 
		"'<'", "'>'", "'<='", "'>='", "'='", "'<>'", "'and'", "'or'", "'FileA'", 
		"'FileB'", "'FileC'", "'FileD'", "'FileE'", "'FileF'", "'FileG'", "'FileH'", 
		"'Rank1'", "'Rank2'", "'Rank3'", "'Rank4'", "'Rank5'", "'Rank6'", "'Rank7'", 
		"'Rank8'", "'abs('", "'+'", "'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"NumericLitteral", "ID", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SingleKingConstraint.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SingleKingConstraintParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SingleKingConstraintContext extends ParserRuleContext {
		public TerminalExprContext terminalExpr() {
			return getRuleContext(TerminalExprContext.class,0);
		}
		public List<VariableAssignContext> variableAssign() {
			return getRuleContexts(VariableAssignContext.class);
		}
		public VariableAssignContext variableAssign(int i) {
			return getRuleContext(VariableAssignContext.class,i);
		}
		public SingleKingConstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleKingConstraint; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitSingleKingConstraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleKingConstraintContext singleKingConstraint() throws RecognitionException {
		SingleKingConstraintContext _localctx = new SingleKingConstraintContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_singleKingConstraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(17);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(14);
				variableAssign();
				}
				}
				setState(19);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(20);
			terminalExpr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableAssignContext extends ParserRuleContext {
		public VariableAssignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableAssign; }
	 
		public VariableAssignContext() { }
		public void copyFrom(VariableAssignContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NumericAssignContext extends VariableAssignContext {
		public TerminalNode ID() { return getToken(SingleKingConstraintParser.ID, 0); }
		public NumericExprContext numericExpr() {
			return getRuleContext(NumericExprContext.class,0);
		}
		public NumericAssignContext(VariableAssignContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitNumericAssign(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanAssignContext extends VariableAssignContext {
		public TerminalNode ID() { return getToken(SingleKingConstraintParser.ID, 0); }
		public BooleanExprContext booleanExpr() {
			return getRuleContext(BooleanExprContext.class,0);
		}
		public BooleanAssignContext(VariableAssignContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitBooleanAssign(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableAssignContext variableAssign() throws RecognitionException {
		VariableAssignContext _localctx = new VariableAssignContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_variableAssign);
		try {
			setState(32);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new NumericAssignContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(22);
				match(ID);
				setState(23);
				match(T__0);
				setState(24);
				numericExpr(0);
				setState(25);
				match(T__1);
				}
				break;
			case 2:
				_localctx = new BooleanAssignContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(27);
				match(ID);
				setState(28);
				match(T__0);
				setState(29);
				booleanExpr(0);
				setState(30);
				match(T__1);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TerminalExprContext extends ParserRuleContext {
		public BooleanExprContext booleanExpr() {
			return getRuleContext(BooleanExprContext.class,0);
		}
		public TerminalExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_terminalExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitTerminalExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TerminalExprContext terminalExpr() throws RecognitionException {
		TerminalExprContext _localctx = new TerminalExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_terminalExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			match(T__2);
			setState(35);
			booleanExpr(0);
			setState(36);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleanExprContext extends ParserRuleContext {
		public BooleanExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanExpr; }
	 
		public BooleanExprContext() { }
		public void copyFrom(BooleanExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NumericEqualityContext extends BooleanExprContext {
		public Token op;
		public List<NumericExprContext> numericExpr() {
			return getRuleContexts(NumericExprContext.class);
		}
		public NumericExprContext numericExpr(int i) {
			return getRuleContext(NumericExprContext.class,i);
		}
		public NumericEqualityContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitNumericEquality(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OrComparisonContext extends BooleanExprContext {
		public List<BooleanExprContext> booleanExpr() {
			return getRuleContexts(BooleanExprContext.class);
		}
		public BooleanExprContext booleanExpr(int i) {
			return getRuleContext(BooleanExprContext.class,i);
		}
		public OrComparisonContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitOrComparison(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConditionalBooleanExprContext extends BooleanExprContext {
		public List<BooleanExprContext> booleanExpr() {
			return getRuleContexts(BooleanExprContext.class);
		}
		public BooleanExprContext booleanExpr(int i) {
			return getRuleContext(BooleanExprContext.class,i);
		}
		public ConditionalBooleanExprContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitConditionalBooleanExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanVariableContext extends BooleanExprContext {
		public TerminalNode ID() { return getToken(SingleKingConstraintParser.ID, 0); }
		public BooleanVariableContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitBooleanVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesisBooleanExprContext extends BooleanExprContext {
		public BooleanExprContext booleanExpr() {
			return getRuleContext(BooleanExprContext.class,0);
		}
		public ParenthesisBooleanExprContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitParenthesisBooleanExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AndComparisonContext extends BooleanExprContext {
		public List<BooleanExprContext> booleanExpr() {
			return getRuleContexts(BooleanExprContext.class);
		}
		public BooleanExprContext booleanExpr(int i) {
			return getRuleContext(BooleanExprContext.class,i);
		}
		public AndComparisonContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitAndComparison(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericRelationalContext extends BooleanExprContext {
		public Token op;
		public List<NumericExprContext> numericExpr() {
			return getRuleContexts(NumericExprContext.class);
		}
		public NumericExprContext numericExpr(int i) {
			return getRuleContext(NumericExprContext.class,i);
		}
		public NumericRelationalContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitNumericRelational(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanExprContext booleanExpr() throws RecognitionException {
		return booleanExpr(0);
	}

	private BooleanExprContext booleanExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		BooleanExprContext _localctx = new BooleanExprContext(_ctx, _parentState);
		BooleanExprContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_booleanExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				_localctx = new ParenthesisBooleanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(39);
				match(T__3);
				setState(40);
				booleanExpr(0);
				setState(41);
				match(T__4);
				}
				break;
			case 2:
				{
				_localctx = new ConditionalBooleanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(43);
				match(T__5);
				setState(44);
				match(T__3);
				setState(45);
				booleanExpr(0);
				setState(46);
				match(T__4);
				setState(47);
				match(T__6);
				setState(48);
				match(T__3);
				setState(49);
				booleanExpr(0);
				setState(50);
				match(T__4);
				setState(51);
				match(T__7);
				setState(52);
				match(T__3);
				setState(53);
				booleanExpr(0);
				setState(54);
				match(T__4);
				}
				break;
			case 3:
				{
				_localctx = new BooleanVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(56);
				match(ID);
				}
				break;
			case 4:
				{
				_localctx = new NumericRelationalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(57);
				numericExpr(0);
				setState(58);
				((NumericRelationalContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11))) != 0)) ) {
					((NumericRelationalContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(59);
				numericExpr(0);
				}
				break;
			case 5:
				{
				_localctx = new NumericEqualityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(61);
				numericExpr(0);
				setState(62);
				((NumericEqualityContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__12 || _la==T__13) ) {
					((NumericEqualityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(63);
				numericExpr(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(75);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(73);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new AndComparisonContext(new BooleanExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_booleanExpr);
						setState(67);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(68);
						match(T__14);
						setState(69);
						booleanExpr(3);
						}
						break;
					case 2:
						{
						_localctx = new OrComparisonContext(new BooleanExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_booleanExpr);
						setState(70);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(71);
						match(T__15);
						setState(72);
						booleanExpr(2);
						}
						break;
					}
					} 
				}
				setState(77);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FileConstantContext extends ParserRuleContext {
		public FileConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fileConstant; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitFileConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileConstantContext fileConstant() throws RecognitionException {
		FileConstantContext _localctx = new FileConstantContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_fileConstant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RankConstantContext extends ParserRuleContext {
		public RankConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rankConstant; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitRankConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RankConstantContext rankConstant() throws RecognitionException {
		RankConstantContext _localctx = new RankConstantContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_rankConstant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumericExprContext extends ParserRuleContext {
		public NumericExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericExpr; }
	 
		public NumericExprContext() { }
		public void copyFrom(NumericExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AbsoluteNumericExprContext extends NumericExprContext {
		public NumericExprContext numericExpr() {
			return getRuleContext(NumericExprContext.class,0);
		}
		public AbsoluteNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitAbsoluteNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesisNumericExprContext extends NumericExprContext {
		public NumericExprContext numericExpr() {
			return getRuleContext(NumericExprContext.class,0);
		}
		public ParenthesisNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitParenthesisNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConditionalNumericExprContext extends NumericExprContext {
		public BooleanExprContext booleanExpr() {
			return getRuleContext(BooleanExprContext.class,0);
		}
		public List<NumericExprContext> numericExpr() {
			return getRuleContexts(NumericExprContext.class);
		}
		public NumericExprContext numericExpr(int i) {
			return getRuleContext(NumericExprContext.class,i);
		}
		public ConditionalNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitConditionalNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericVariableContext extends NumericExprContext {
		public TerminalNode ID() { return getToken(SingleKingConstraintParser.ID, 0); }
		public NumericVariableContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitNumericVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PlusMinusNumericExprContext extends NumericExprContext {
		public Token op;
		public List<NumericExprContext> numericExpr() {
			return getRuleContexts(NumericExprContext.class);
		}
		public NumericExprContext numericExpr(int i) {
			return getRuleContext(NumericExprContext.class,i);
		}
		public PlusMinusNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitPlusMinusNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LitteralNumericExprContext extends NumericExprContext {
		public TerminalNode NumericLitteral() { return getToken(SingleKingConstraintParser.NumericLitteral, 0); }
		public LitteralNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitLitteralNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RankConstantNumericExprContext extends NumericExprContext {
		public RankConstantContext rankConstant() {
			return getRuleContext(RankConstantContext.class,0);
		}
		public RankConstantNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitRankConstantNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FileConstantNumericExprContext extends NumericExprContext {
		public FileConstantContext fileConstant() {
			return getRuleContext(FileConstantContext.class,0);
		}
		public FileConstantNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitFileConstantNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumericExprContext numericExpr() throws RecognitionException {
		return numericExpr(0);
	}

	private NumericExprContext numericExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		NumericExprContext _localctx = new NumericExprContext(_ctx, _parentState);
		NumericExprContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_numericExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				{
				_localctx = new ParenthesisNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(83);
				match(T__3);
				setState(84);
				numericExpr(0);
				setState(85);
				match(T__4);
				}
				break;
			case T__5:
				{
				_localctx = new ConditionalNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(87);
				match(T__5);
				setState(88);
				match(T__3);
				setState(89);
				booleanExpr(0);
				setState(90);
				match(T__4);
				setState(91);
				match(T__6);
				setState(92);
				match(T__3);
				setState(93);
				numericExpr(0);
				setState(94);
				match(T__4);
				setState(95);
				match(T__7);
				setState(96);
				match(T__3);
				setState(97);
				numericExpr(0);
				setState(98);
				match(T__4);
				}
				break;
			case T__32:
				{
				_localctx = new AbsoluteNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(100);
				match(T__32);
				setState(101);
				numericExpr(0);
				setState(102);
				match(T__4);
				}
				break;
			case NumericLitteral:
				{
				_localctx = new LitteralNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(104);
				match(NumericLitteral);
				}
				break;
			case ID:
				{
				_localctx = new NumericVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(105);
				match(ID);
				}
				break;
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
				{
				_localctx = new FileConstantNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(106);
				fileConstant();
				}
				break;
			case T__24:
			case T__25:
			case T__26:
			case T__27:
			case T__28:
			case T__29:
			case T__30:
			case T__31:
				{
				_localctx = new RankConstantNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(107);
				rankConstant();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(115);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new PlusMinusNumericExprContext(new NumericExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_numericExpr);
					setState(110);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(111);
					((PlusMinusNumericExprContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==T__33 || _la==T__34) ) {
						((PlusMinusNumericExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(112);
					numericExpr(2);
					}
					} 
				}
				setState(117);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3:
			return booleanExpr_sempred((BooleanExprContext)_localctx, predIndex);
		case 6:
			return numericExpr_sempred((NumericExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean booleanExpr_sempred(BooleanExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean numericExpr_sempred(NumericExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3(y\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\7\2\22\n\2\f\2\16\2\25\13"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3#\n\3\3\4\3\4\3"+
		"\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5D\n\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\7\5L\n\5\f\5\16\5O\13\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\5\bo\n\b\3\b\3\b\3\b\7\bt\n\b\f\b\16\bw\13\b\3\b\2\4\b\16"+
		"\t\2\4\6\b\n\f\16\2\7\3\2\13\16\3\2\17\20\3\2\23\32\3\2\33\"\3\2$%\2\u0080"+
		"\2\23\3\2\2\2\4\"\3\2\2\2\6$\3\2\2\2\bC\3\2\2\2\nP\3\2\2\2\fR\3\2\2\2"+
		"\16n\3\2\2\2\20\22\5\4\3\2\21\20\3\2\2\2\22\25\3\2\2\2\23\21\3\2\2\2\23"+
		"\24\3\2\2\2\24\26\3\2\2\2\25\23\3\2\2\2\26\27\5\6\4\2\27\3\3\2\2\2\30"+
		"\31\7\'\2\2\31\32\7\3\2\2\32\33\5\16\b\2\33\34\7\4\2\2\34#\3\2\2\2\35"+
		"\36\7\'\2\2\36\37\7\3\2\2\37 \5\b\5\2 !\7\4\2\2!#\3\2\2\2\"\30\3\2\2\2"+
		"\"\35\3\2\2\2#\5\3\2\2\2$%\7\5\2\2%&\5\b\5\2&\'\7\4\2\2\'\7\3\2\2\2()"+
		"\b\5\1\2)*\7\6\2\2*+\5\b\5\2+,\7\7\2\2,D\3\2\2\2-.\7\b\2\2./\7\6\2\2/"+
		"\60\5\b\5\2\60\61\7\7\2\2\61\62\7\t\2\2\62\63\7\6\2\2\63\64\5\b\5\2\64"+
		"\65\7\7\2\2\65\66\7\n\2\2\66\67\7\6\2\2\678\5\b\5\289\7\7\2\29D\3\2\2"+
		"\2:D\7\'\2\2;<\5\16\b\2<=\t\2\2\2=>\5\16\b\2>D\3\2\2\2?@\5\16\b\2@A\t"+
		"\3\2\2AB\5\16\b\2BD\3\2\2\2C(\3\2\2\2C-\3\2\2\2C:\3\2\2\2C;\3\2\2\2C?"+
		"\3\2\2\2DM\3\2\2\2EF\f\4\2\2FG\7\21\2\2GL\5\b\5\5HI\f\3\2\2IJ\7\22\2\2"+
		"JL\5\b\5\4KE\3\2\2\2KH\3\2\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2N\t\3\2\2"+
		"\2OM\3\2\2\2PQ\t\4\2\2Q\13\3\2\2\2RS\t\5\2\2S\r\3\2\2\2TU\b\b\1\2UV\7"+
		"\6\2\2VW\5\16\b\2WX\7\7\2\2Xo\3\2\2\2YZ\7\b\2\2Z[\7\6\2\2[\\\5\b\5\2\\"+
		"]\7\7\2\2]^\7\t\2\2^_\7\6\2\2_`\5\16\b\2`a\7\7\2\2ab\7\n\2\2bc\7\6\2\2"+
		"cd\5\16\b\2de\7\7\2\2eo\3\2\2\2fg\7#\2\2gh\5\16\b\2hi\7\7\2\2io\3\2\2"+
		"\2jo\7&\2\2ko\7\'\2\2lo\5\n\6\2mo\5\f\7\2nT\3\2\2\2nY\3\2\2\2nf\3\2\2"+
		"\2nj\3\2\2\2nk\3\2\2\2nl\3\2\2\2nm\3\2\2\2ou\3\2\2\2pq\f\3\2\2qr\t\6\2"+
		"\2rt\5\16\b\4sp\3\2\2\2tw\3\2\2\2us\3\2\2\2uv\3\2\2\2v\17\3\2\2\2wu\3"+
		"\2\2\2\t\23\"CKMnu";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}