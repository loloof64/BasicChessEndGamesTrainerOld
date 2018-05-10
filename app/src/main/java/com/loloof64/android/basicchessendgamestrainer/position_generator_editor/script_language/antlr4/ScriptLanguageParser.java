package com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.antlr4;
// Generated from ScriptLanguage.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ScriptLanguageParser extends Parser {
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
		RULE_scriptLanguage = 0, RULE_variableAssign = 1, RULE_terminalExpr = 2, 
		RULE_booleanExpr = 3, RULE_fileConstant = 4, RULE_rankConstant = 5, RULE_numericExpr = 6;
	public static final String[] ruleNames = {
		"scriptLanguage", "variableAssign", "terminalExpr", "booleanExpr", "fileConstant", 
		"rankConstant", "numericExpr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "':='", "';'", "'return'", "'('", "')'", "'if'", "'then'", "'else'", 
		"'<'", "'>'", "'<='", "'>='", "'='", "'<>'", "'and'", "'or'", "'FileA'", 
		"'FileB'", "'FileC'", "'FileD'", "'FileE'", "'FileF'", "'FileG'", "'FileH'", 
		"'Rank1'", "'Rank2'", "'Rank3'", "'Rank4'", "'Rank5'", "'Rank6'", "'Rank7'", 
		"'Rank8'", "'abs('", "'%'", "'+'", "'-'"
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
	public String getGrammarFileName() { return "ScriptLanguage.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ScriptLanguageParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ScriptLanguageContext extends ParserRuleContext {
		public TerminalExprContext terminalExpr() {
			return getRuleContext(TerminalExprContext.class,0);
		}
		public List<VariableAssignContext> variableAssign() {
			return getRuleContexts(VariableAssignContext.class);
		}
		public VariableAssignContext variableAssign(int i) {
			return getRuleContext(VariableAssignContext.class,i);
		}
		public ScriptLanguageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scriptLanguage; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitScriptLanguage(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptLanguageContext scriptLanguage() throws RecognitionException {
		ScriptLanguageContext _localctx = new ScriptLanguageContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_scriptLanguage);
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
		public TerminalNode ID() { return getToken(ScriptLanguageParser.ID, 0); }
		public NumericExprContext numericExpr() {
			return getRuleContext(NumericExprContext.class,0);
		}
		public NumericAssignContext(VariableAssignContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitNumericAssign(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanAssignContext extends VariableAssignContext {
		public TerminalNode ID() { return getToken(ScriptLanguageParser.ID, 0); }
		public BooleanExprContext booleanExpr() {
			return getRuleContext(BooleanExprContext.class,0);
		}
		public BooleanAssignContext(VariableAssignContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitBooleanAssign(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitTerminalExpr(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitNumericEquality(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitOrComparison(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitConditionalBooleanExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanVariableContext extends BooleanExprContext {
		public TerminalNode ID() { return getToken(ScriptLanguageParser.ID, 0); }
		public BooleanVariableContext(BooleanExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitBooleanVariable(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitParenthesisBooleanExpr(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitAndComparison(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitNumericRelational(this);
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
			setState(59);
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
				booleanExpr(0);
				setState(45);
				match(T__6);
				setState(46);
				booleanExpr(0);
				setState(47);
				match(T__7);
				setState(48);
				booleanExpr(6);
				}
				break;
			case 3:
				{
				_localctx = new BooleanVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(50);
				match(ID);
				}
				break;
			case 4:
				{
				_localctx = new NumericRelationalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(51);
				numericExpr(0);
				setState(52);
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
				setState(53);
				numericExpr(0);
				}
				break;
			case 5:
				{
				_localctx = new NumericEqualityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(55);
				numericExpr(0);
				setState(56);
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
				setState(57);
				numericExpr(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(69);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(67);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new AndComparisonContext(new BooleanExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_booleanExpr);
						setState(61);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(62);
						match(T__14);
						setState(63);
						booleanExpr(3);
						}
						break;
					case 2:
						{
						_localctx = new OrComparisonContext(new BooleanExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_booleanExpr);
						setState(64);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(65);
						match(T__15);
						setState(66);
						booleanExpr(2);
						}
						break;
					}
					} 
				}
				setState(71);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitFileConstant(this);
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
			setState(72);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitRankConstant(this);
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
			setState(74);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitAbsoluteNumericExpr(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitParenthesisNumericExpr(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitConditionalNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericVariableContext extends NumericExprContext {
		public TerminalNode ID() { return getToken(ScriptLanguageParser.ID, 0); }
		public NumericVariableContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitNumericVariable(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitPlusMinusNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LitteralNumericExprContext extends NumericExprContext {
		public TerminalNode NumericLitteral() { return getToken(ScriptLanguageParser.NumericLitteral, 0); }
		public LitteralNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitLitteralNumericExpr(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitRankConstantNumericExpr(this);
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
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitFileConstantNumericExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ModuloNumericExprContext extends NumericExprContext {
		public List<NumericExprContext> numericExpr() {
			return getRuleContexts(NumericExprContext.class);
		}
		public NumericExprContext numericExpr(int i) {
			return getRuleContext(NumericExprContext.class,i);
		}
		public ModuloNumericExprContext(NumericExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptLanguageVisitor ) return ((ScriptLanguageVisitor<? extends T>)visitor).visitModuloNumericExpr(this);
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
			setState(96);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				{
				_localctx = new ParenthesisNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(77);
				match(T__3);
				setState(78);
				numericExpr(0);
				setState(79);
				match(T__4);
				}
				break;
			case T__5:
				{
				_localctx = new ConditionalNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(81);
				match(T__5);
				setState(82);
				booleanExpr(0);
				setState(83);
				match(T__6);
				setState(84);
				numericExpr(0);
				setState(85);
				match(T__7);
				setState(86);
				numericExpr(8);
				}
				break;
			case T__32:
				{
				_localctx = new AbsoluteNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(88);
				match(T__32);
				setState(89);
				numericExpr(0);
				setState(90);
				match(T__4);
				}
				break;
			case NumericLitteral:
				{
				_localctx = new LitteralNumericExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(92);
				match(NumericLitteral);
				}
				break;
			case ID:
				{
				_localctx = new NumericVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(93);
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
				setState(94);
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
				setState(95);
				rankConstant();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(106);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(104);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
					case 1:
						{
						_localctx = new ModuloNumericExprContext(new NumericExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_numericExpr);
						setState(98);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(99);
						match(T__33);
						setState(100);
						numericExpr(7);
						}
						break;
					case 2:
						{
						_localctx = new PlusMinusNumericExprContext(new NumericExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_numericExpr);
						setState(101);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(102);
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
						setState(103);
						numericExpr(6);
						}
						break;
					}
					} 
				}
				setState(108);
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
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3)p\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\7\2\22\n\2\f\2\16\2\25\13"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3#\n\3\3\4\3\4\3"+
		"\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\5\5>\n\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5F\n\5\f\5\16"+
		"\5I\13\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bc\n\b\3\b\3\b\3\b\3\b\3\b\3\b\7"+
		"\bk\n\b\f\b\16\bn\13\b\3\b\2\4\b\16\t\2\4\6\b\n\f\16\2\7\3\2\13\16\3\2"+
		"\17\20\3\2\23\32\3\2\33\"\3\2%&\2x\2\23\3\2\2\2\4\"\3\2\2\2\6$\3\2\2\2"+
		"\b=\3\2\2\2\nJ\3\2\2\2\fL\3\2\2\2\16b\3\2\2\2\20\22\5\4\3\2\21\20\3\2"+
		"\2\2\22\25\3\2\2\2\23\21\3\2\2\2\23\24\3\2\2\2\24\26\3\2\2\2\25\23\3\2"+
		"\2\2\26\27\5\6\4\2\27\3\3\2\2\2\30\31\7(\2\2\31\32\7\3\2\2\32\33\5\16"+
		"\b\2\33\34\7\4\2\2\34#\3\2\2\2\35\36\7(\2\2\36\37\7\3\2\2\37 \5\b\5\2"+
		" !\7\4\2\2!#\3\2\2\2\"\30\3\2\2\2\"\35\3\2\2\2#\5\3\2\2\2$%\7\5\2\2%&"+
		"\5\b\5\2&\'\7\4\2\2\'\7\3\2\2\2()\b\5\1\2)*\7\6\2\2*+\5\b\5\2+,\7\7\2"+
		"\2,>\3\2\2\2-.\7\b\2\2./\5\b\5\2/\60\7\t\2\2\60\61\5\b\5\2\61\62\7\n\2"+
		"\2\62\63\5\b\5\b\63>\3\2\2\2\64>\7(\2\2\65\66\5\16\b\2\66\67\t\2\2\2\67"+
		"8\5\16\b\28>\3\2\2\29:\5\16\b\2:;\t\3\2\2;<\5\16\b\2<>\3\2\2\2=(\3\2\2"+
		"\2=-\3\2\2\2=\64\3\2\2\2=\65\3\2\2\2=9\3\2\2\2>G\3\2\2\2?@\f\4\2\2@A\7"+
		"\21\2\2AF\5\b\5\5BC\f\3\2\2CD\7\22\2\2DF\5\b\5\4E?\3\2\2\2EB\3\2\2\2F"+
		"I\3\2\2\2GE\3\2\2\2GH\3\2\2\2H\t\3\2\2\2IG\3\2\2\2JK\t\4\2\2K\13\3\2\2"+
		"\2LM\t\5\2\2M\r\3\2\2\2NO\b\b\1\2OP\7\6\2\2PQ\5\16\b\2QR\7\7\2\2Rc\3\2"+
		"\2\2ST\7\b\2\2TU\5\b\5\2UV\7\t\2\2VW\5\16\b\2WX\7\n\2\2XY\5\16\b\nYc\3"+
		"\2\2\2Z[\7#\2\2[\\\5\16\b\2\\]\7\7\2\2]c\3\2\2\2^c\7\'\2\2_c\7(\2\2`c"+
		"\5\n\6\2ac\5\f\7\2bN\3\2\2\2bS\3\2\2\2bZ\3\2\2\2b^\3\2\2\2b_\3\2\2\2b"+
		"`\3\2\2\2ba\3\2\2\2cl\3\2\2\2de\f\b\2\2ef\7$\2\2fk\5\16\b\tgh\f\7\2\2"+
		"hi\t\6\2\2ik\5\16\b\bjd\3\2\2\2jg\3\2\2\2kn\3\2\2\2lj\3\2\2\2lm\3\2\2"+
		"\2m\17\3\2\2\2nl\3\2\2\2\n\23\"=EGbjl";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}