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

// Generated from SingleKingConstraint.g4 by ANTLR 4.7.1
package com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.antlr4;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SingleKingConstraintLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, NumericLitteral=33, ID=34, WS=35;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
		"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "NumericLitteral", 
		"ID", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'return'", "';'", "':='", "'('", "')'", "'<'", "'>'", "'<='", "'>='", 
		"'='", "'<>'", "'and'", "'or'", "'FileA'", "'FileB'", "'FileC'", "'FileD'", 
		"'FileE'", "'FileF'", "'FileG'", "'FileH'", "'Rank1'", "'Rank2'", "'Rank3'", 
		"'Rank4'", "'Rank5'", "'Rank6'", "'Rank7'", "'Rank8'", "'abs('", "'+'", 
		"'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, "NumericLitteral", 
		"ID", "WS"
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


	public SingleKingConstraintLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SingleKingConstraint.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2%\u00ed\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3"+
		"\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3"+
		"\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3 \3 \3!\3!\3"+
		"\"\3\"\7\"\u00db\n\"\f\"\16\"\u00de\13\"\3#\3#\7#\u00e2\n#\f#\16#\u00e5"+
		"\13#\3$\6$\u00e8\n$\r$\16$\u00e9\3$\3$\2\2%\3\3\5\4\7\5\t\6\13\7\r\b\17"+
		"\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+"+
		"\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%\3\2\7\3\2\63"+
		";\3\2\62;\4\2C\\c|\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\2\u00ef\2\3\3\2\2"+
		"\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3"+
		"\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2"+
		"\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2"+
		"\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2"+
		"\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3"+
		"\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\3I\3\2\2"+
		"\2\5P\3\2\2\2\7R\3\2\2\2\tU\3\2\2\2\13W\3\2\2\2\rY\3\2\2\2\17[\3\2\2\2"+
		"\21]\3\2\2\2\23`\3\2\2\2\25c\3\2\2\2\27e\3\2\2\2\31h\3\2\2\2\33l\3\2\2"+
		"\2\35o\3\2\2\2\37u\3\2\2\2!{\3\2\2\2#\u0081\3\2\2\2%\u0087\3\2\2\2\'\u008d"+
		"\3\2\2\2)\u0093\3\2\2\2+\u0099\3\2\2\2-\u009f\3\2\2\2/\u00a5\3\2\2\2\61"+
		"\u00ab\3\2\2\2\63\u00b1\3\2\2\2\65\u00b7\3\2\2\2\67\u00bd\3\2\2\29\u00c3"+
		"\3\2\2\2;\u00c9\3\2\2\2=\u00cf\3\2\2\2?\u00d4\3\2\2\2A\u00d6\3\2\2\2C"+
		"\u00d8\3\2\2\2E\u00df\3\2\2\2G\u00e7\3\2\2\2IJ\7t\2\2JK\7g\2\2KL\7v\2"+
		"\2LM\7w\2\2MN\7t\2\2NO\7p\2\2O\4\3\2\2\2PQ\7=\2\2Q\6\3\2\2\2RS\7<\2\2"+
		"ST\7?\2\2T\b\3\2\2\2UV\7*\2\2V\n\3\2\2\2WX\7+\2\2X\f\3\2\2\2YZ\7>\2\2"+
		"Z\16\3\2\2\2[\\\7@\2\2\\\20\3\2\2\2]^\7>\2\2^_\7?\2\2_\22\3\2\2\2`a\7"+
		"@\2\2ab\7?\2\2b\24\3\2\2\2cd\7?\2\2d\26\3\2\2\2ef\7>\2\2fg\7@\2\2g\30"+
		"\3\2\2\2hi\7c\2\2ij\7p\2\2jk\7f\2\2k\32\3\2\2\2lm\7q\2\2mn\7t\2\2n\34"+
		"\3\2\2\2op\7H\2\2pq\7k\2\2qr\7n\2\2rs\7g\2\2st\7C\2\2t\36\3\2\2\2uv\7"+
		"H\2\2vw\7k\2\2wx\7n\2\2xy\7g\2\2yz\7D\2\2z \3\2\2\2{|\7H\2\2|}\7k\2\2"+
		"}~\7n\2\2~\177\7g\2\2\177\u0080\7E\2\2\u0080\"\3\2\2\2\u0081\u0082\7H"+
		"\2\2\u0082\u0083\7k\2\2\u0083\u0084\7n\2\2\u0084\u0085\7g\2\2\u0085\u0086"+
		"\7F\2\2\u0086$\3\2\2\2\u0087\u0088\7H\2\2\u0088\u0089\7k\2\2\u0089\u008a"+
		"\7n\2\2\u008a\u008b\7g\2\2\u008b\u008c\7G\2\2\u008c&\3\2\2\2\u008d\u008e"+
		"\7H\2\2\u008e\u008f\7k\2\2\u008f\u0090\7n\2\2\u0090\u0091\7g\2\2\u0091"+
		"\u0092\7H\2\2\u0092(\3\2\2\2\u0093\u0094\7H\2\2\u0094\u0095\7k\2\2\u0095"+
		"\u0096\7n\2\2\u0096\u0097\7g\2\2\u0097\u0098\7I\2\2\u0098*\3\2\2\2\u0099"+
		"\u009a\7H\2\2\u009a\u009b\7k\2\2\u009b\u009c\7n\2\2\u009c\u009d\7g\2\2"+
		"\u009d\u009e\7J\2\2\u009e,\3\2\2\2\u009f\u00a0\7T\2\2\u00a0\u00a1\7c\2"+
		"\2\u00a1\u00a2\7p\2\2\u00a2\u00a3\7m\2\2\u00a3\u00a4\7\63\2\2\u00a4.\3"+
		"\2\2\2\u00a5\u00a6\7T\2\2\u00a6\u00a7\7c\2\2\u00a7\u00a8\7p\2\2\u00a8"+
		"\u00a9\7m\2\2\u00a9\u00aa\7\64\2\2\u00aa\60\3\2\2\2\u00ab\u00ac\7T\2\2"+
		"\u00ac\u00ad\7c\2\2\u00ad\u00ae\7p\2\2\u00ae\u00af\7m\2\2\u00af\u00b0"+
		"\7\65\2\2\u00b0\62\3\2\2\2\u00b1\u00b2\7T\2\2\u00b2\u00b3\7c\2\2\u00b3"+
		"\u00b4\7p\2\2\u00b4\u00b5\7m\2\2\u00b5\u00b6\7\66\2\2\u00b6\64\3\2\2\2"+
		"\u00b7\u00b8\7T\2\2\u00b8\u00b9\7c\2\2\u00b9\u00ba\7p\2\2\u00ba\u00bb"+
		"\7m\2\2\u00bb\u00bc\7\67\2\2\u00bc\66\3\2\2\2\u00bd\u00be\7T\2\2\u00be"+
		"\u00bf\7c\2\2\u00bf\u00c0\7p\2\2\u00c0\u00c1\7m\2\2\u00c1\u00c2\78\2\2"+
		"\u00c28\3\2\2\2\u00c3\u00c4\7T\2\2\u00c4\u00c5\7c\2\2\u00c5\u00c6\7p\2"+
		"\2\u00c6\u00c7\7m\2\2\u00c7\u00c8\79\2\2\u00c8:\3\2\2\2\u00c9\u00ca\7"+
		"T\2\2\u00ca\u00cb\7c\2\2\u00cb\u00cc\7p\2\2\u00cc\u00cd\7m\2\2\u00cd\u00ce"+
		"\7:\2\2\u00ce<\3\2\2\2\u00cf\u00d0\7c\2\2\u00d0\u00d1\7d\2\2\u00d1\u00d2"+
		"\7u\2\2\u00d2\u00d3\7*\2\2\u00d3>\3\2\2\2\u00d4\u00d5\7-\2\2\u00d5@\3"+
		"\2\2\2\u00d6\u00d7\7/\2\2\u00d7B\3\2\2\2\u00d8\u00dc\t\2\2\2\u00d9\u00db"+
		"\t\3\2\2\u00da\u00d9\3\2\2\2\u00db\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc"+
		"\u00dd\3\2\2\2\u00ddD\3\2\2\2\u00de\u00dc\3\2\2\2\u00df\u00e3\t\4\2\2"+
		"\u00e0\u00e2\t\5\2\2\u00e1\u00e0\3\2\2\2\u00e2\u00e5\3\2\2\2\u00e3\u00e1"+
		"\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4F\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e6"+
		"\u00e8\t\6\2\2\u00e7\u00e6\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9\u00e7\3\2"+
		"\2\2\u00e9\u00ea\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ec\b$\2\2\u00ec"+
		"H\3\2\2\2\6\2\u00dc\u00e3\u00e9\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}