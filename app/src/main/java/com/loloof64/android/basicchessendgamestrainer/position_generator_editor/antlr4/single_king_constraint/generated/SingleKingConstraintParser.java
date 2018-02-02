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
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

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
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, NumericLitteral=37, 
		ID=38, WS=39;
	public static final int
		RULE_singleKingConstraint = 0, RULE_variableAssign = 1, RULE_booleanExpr = 2, 
		RULE_rangeValue = 3, RULE_fileRange = 4, RULE_rankRange = 5, RULE_fileConstant = 6, 
		RULE_rankConstant = 7, RULE_numericExpr = 8;
	public static final String[] ruleNames = {
		"singleKingConstraint", "variableAssign", "booleanExpr", "rangeValue", 
		"fileRange", "rankRange", "fileConstant", "rankConstant", "numericExpr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'return'", "';'", "':='", "'('", "')'", "'in'", "'<'", "'>'", "'<='", 
		"'>='", "'='", "'<>'", "'and'", "'or'", "'['", "','", "']'", "'FileA'", 
		"'FileB'", "'FileC'", "'FileD'", "'FileE'", "'FileF'", "'FileG'", "'FileH'", 
		"'Rank1'", "'Rank2'", "'Rank3'", "'Rank4'", "'Rank5'", "'Rank6'", "'Rank7'", 
		"'Rank8'", "'abs('", "'+'", "'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, "NumericLitteral", "ID", "WS"
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
		public BooleanExprContext booleanExpr() {
			return getRuleContext(BooleanExprContext.class,0);
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
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(18);
				variableAssign();
				}
				}
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(24);
			match(T__0);
			setState(25);
			booleanExpr(0);
			setState(26);
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
			setState(38);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new NumericAssignContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(28);
				match(ID);
				setState(29);
				match(T__2);
				setState(30);
				numericExpr(0);
				setState(31);
				match(T__1);
				}
				break;
			case 2:
				_localctx = new BooleanAssignContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(33);
				match(ID);
				setState(34);
				match(T__2);
				setState(35);
				booleanExpr(0);
				setState(36);
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
	public static class RangeCheckContext extends BooleanExprContext {
		public NumericExprContext numericExpr() {
			return getRuleContext(NumericExprContext.class,0);
		}
		public RangeValueContext rangeValue() {
			return getRuleContext(RangeValueContext.class,0);
		}
		public RangeCheckContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitRangeCheck(this);
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
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_booleanExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				_localctx = new ParenthesisBooleanExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(41);
				match(T__3);
				setState(42);
				booleanExpr(0);
				setState(43);
				match(T__4);
				}
				break;
			case 2:
				{
				_localctx = new BooleanVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(45);
				match(ID);
				}
				break;
			case 3:
				{
				_localctx = new RangeCheckContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(46);
				numericExpr(0);
				setState(47);
				match(T__5);
				setState(48);
				rangeValue();
				}
				break;
			case 4:
				{
				_localctx = new NumericRelationalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(50);
				numericExpr(0);
				setState(51);
				((NumericRelationalContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0)) ) {
					((NumericRelationalContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(52);
				numericExpr(0);
				}
				break;
			case 5:
				{
				_localctx = new NumericEqualityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(54);
				numericExpr(0);
				setState(55);
				((NumericEqualityContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__10 || _la==T__11) ) {
					((NumericEqualityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(56);
				numericExpr(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(68);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(66);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new AndComparisonContext(new BooleanExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_booleanExpr);
						setState(60);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(61);
						match(T__12);
						setState(62);
						booleanExpr(3);
						}
						break;
					case 2:
						{
						_localctx = new OrComparisonContext(new BooleanExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_booleanExpr);
						setState(63);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(64);
						match(T__13);
						setState(65);
						booleanExpr(2);
						}
						break;
					}
					} 
				}
				setState(70);
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

	public static class RangeValueContext extends ParserRuleContext {
		public FileRangeContext fileRange() {
			return getRuleContext(FileRangeContext.class,0);
		}
		public RankRangeContext rankRange() {
			return getRuleContext(RankRangeContext.class,0);
		}
		public RangeValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rangeValue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitRangeValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RangeValueContext rangeValue() throws RecognitionException {
		RangeValueContext _localctx = new RangeValueContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_rangeValue);
		try {
			setState(73);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(71);
				fileRange();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(72);
				rankRange();
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

	public static class FileRangeContext extends ParserRuleContext {
		public List<FileConstantContext> fileConstant() {
			return getRuleContexts(FileConstantContext.class);
		}
		public FileConstantContext fileConstant(int i) {
			return getRuleContext(FileConstantContext.class,i);
		}
		public FileRangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fileRange; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitFileRange(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileRangeContext fileRange() throws RecognitionException {
		FileRangeContext _localctx = new FileRangeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_fileRange);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(T__14);
			setState(76);
			fileConstant();
			setState(77);
			match(T__15);
			setState(78);
			fileConstant();
			setState(79);
			match(T__16);
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

	public static class RankRangeContext extends ParserRuleContext {
		public List<RankConstantContext> rankConstant() {
			return getRuleContexts(RankConstantContext.class);
		}
		public RankConstantContext rankConstant(int i) {
			return getRuleContext(RankConstantContext.class,i);
		}
		public RankRangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rankRange; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SingleKingConstraintVisitor ) return ((SingleKingConstraintVisitor<? extends T>)visitor).visitRankRange(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RankRangeContext rankRange() throws RecognitionException {
		RankRangeContext _localctx = new RankRangeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_rankRange);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			match(T__14);
			setState(82);
			rankConstant();
			setState(83);
			match(T__15);
			setState(84);
			rankConstant();
			setState(85);
			match(T__16);
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
		enterRule(_localctx, 12, RULE_fileConstant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24))) != 0)) ) {
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
		enterRule(_localctx, 14, RULE_rankConstant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__25) | (1L << T__26) | (1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32))) != 0)) ) {
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
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_numericExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				{
				_localctx = new ParenthesisNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(92);
				match(T__3);
				setState(93);
				numericExpr(0);
				setState(94);
				match(T__4);
				}
				break;
			case T__33:
				{
				_localctx = new AbsoluteNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(96);
				match(T__33);
				setState(97);
				numericExpr(0);
				setState(98);
				match(T__4);
				}
				break;
			case NumericLitteral:
				{
				_localctx = new LitteralNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(100);
				match(NumericLitteral);
				}
				break;
			case ID:
				{
				_localctx = new NumericVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(101);
				match(ID);
				}
				break;
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
				{
				_localctx = new FileConstantNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(102);
				fileConstant();
				}
				break;
			case T__25:
			case T__26:
			case T__27:
			case T__28:
			case T__29:
			case T__30:
			case T__31:
			case T__32:
				{
				_localctx = new RankConstantNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(103);
				rankConstant();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(111);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new PlusMinusNumericExprContext(new NumericExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_numericExpr);
					setState(106);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(107);
					((PlusMinusNumericExprContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==T__34 || _la==T__35) ) {
						((PlusMinusNumericExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(108);
					numericExpr(2);
					}
					} 
				}
				setState(113);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
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
		case 2:
			return booleanExpr_sempred((BooleanExprContext)_localctx, predIndex);
		case 8:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3)u\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\7\2\26\n"+
		"\2\f\2\16\2\31\13\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\5\3)\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\5\4=\n\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4E\n\4\f\4\16\4"+
		"H\13\4\3\5\3\5\5\5L\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\5\nk\n\n\3\n\3\n\3\n\7\np\n\n\f\n\16\ns\13\n\3\n\2\4\6\22\13\2\4\6\b"+
		"\n\f\16\20\22\2\7\3\2\t\f\3\2\r\16\3\2\24\33\3\2\34#\3\2%&\2z\2\27\3\2"+
		"\2\2\4(\3\2\2\2\6<\3\2\2\2\bK\3\2\2\2\nM\3\2\2\2\fS\3\2\2\2\16Y\3\2\2"+
		"\2\20[\3\2\2\2\22j\3\2\2\2\24\26\5\4\3\2\25\24\3\2\2\2\26\31\3\2\2\2\27"+
		"\25\3\2\2\2\27\30\3\2\2\2\30\32\3\2\2\2\31\27\3\2\2\2\32\33\7\3\2\2\33"+
		"\34\5\6\4\2\34\35\7\4\2\2\35\3\3\2\2\2\36\37\7(\2\2\37 \7\5\2\2 !\5\22"+
		"\n\2!\"\7\4\2\2\")\3\2\2\2#$\7(\2\2$%\7\5\2\2%&\5\6\4\2&\'\7\4\2\2\')"+
		"\3\2\2\2(\36\3\2\2\2(#\3\2\2\2)\5\3\2\2\2*+\b\4\1\2+,\7\6\2\2,-\5\6\4"+
		"\2-.\7\7\2\2.=\3\2\2\2/=\7(\2\2\60\61\5\22\n\2\61\62\7\b\2\2\62\63\5\b"+
		"\5\2\63=\3\2\2\2\64\65\5\22\n\2\65\66\t\2\2\2\66\67\5\22\n\2\67=\3\2\2"+
		"\289\5\22\n\29:\t\3\2\2:;\5\22\n\2;=\3\2\2\2<*\3\2\2\2</\3\2\2\2<\60\3"+
		"\2\2\2<\64\3\2\2\2<8\3\2\2\2=F\3\2\2\2>?\f\4\2\2?@\7\17\2\2@E\5\6\4\5"+
		"AB\f\3\2\2BC\7\20\2\2CE\5\6\4\4D>\3\2\2\2DA\3\2\2\2EH\3\2\2\2FD\3\2\2"+
		"\2FG\3\2\2\2G\7\3\2\2\2HF\3\2\2\2IL\5\n\6\2JL\5\f\7\2KI\3\2\2\2KJ\3\2"+
		"\2\2L\t\3\2\2\2MN\7\21\2\2NO\5\16\b\2OP\7\22\2\2PQ\5\16\b\2QR\7\23\2\2"+
		"R\13\3\2\2\2ST\7\21\2\2TU\5\20\t\2UV\7\22\2\2VW\5\20\t\2WX\7\23\2\2X\r"+
		"\3\2\2\2YZ\t\4\2\2Z\17\3\2\2\2[\\\t\5\2\2\\\21\3\2\2\2]^\b\n\1\2^_\7\6"+
		"\2\2_`\5\22\n\2`a\7\7\2\2ak\3\2\2\2bc\7$\2\2cd\5\22\n\2de\7\7\2\2ek\3"+
		"\2\2\2fk\7\'\2\2gk\7(\2\2hk\5\16\b\2ik\5\20\t\2j]\3\2\2\2jb\3\2\2\2jf"+
		"\3\2\2\2jg\3\2\2\2jh\3\2\2\2ji\3\2\2\2kq\3\2\2\2lm\f\3\2\2mn\t\6\2\2n"+
		"p\5\22\n\4ol\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2\2\2r\23\3\2\2\2sq\3\2\2"+
		"\2\n\27(<DFKjq";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}