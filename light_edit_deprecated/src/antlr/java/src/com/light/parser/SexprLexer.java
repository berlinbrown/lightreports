// $ANTLR 3.1.2 /usr/local/projects/light_edit/src/antlr/Sexpr.g 2009-03-13 22:17:07

package com.light.parser;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

public class SexprLexer extends Lexer {
    public static final int RPAREN=5;
    public static final int SYMBOL_START=12;
    public static final int SYMBOL=8;
    public static final int NUMBER=9;
    public static final int WHITESPACE=10;
    public static final int DIGIT=11;
    public static final int DOT=6;
    public static final int EOF=-1;
    public static final int LPAREN=4;
    public static final int STRING=7;

    // delegates
    // delegators

    public SexprLexer() {;} 
    public SexprLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public SexprLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/usr/local/projects/light_edit/src/antlr/Sexpr.g"; }

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:9:8: ( '(' )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:9:10: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:10:8: ( ')' )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:10:10: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:60:5: ( '\"' ( '\\\\' . | ~ ( '\\\\' | '\"' ) )* '\"' )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:60:6: '\"' ( '\\\\' . | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:60:10: ( '\\\\' . | ~ ( '\\\\' | '\"' ) )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\\') ) {
                    alt1=1;
                }
                else if ( ((LA1_0>='\u0000' && LA1_0<='!')||(LA1_0>='#' && LA1_0<='[')||(LA1_0>=']' && LA1_0<='\uFFFF')) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:60:12: '\\\\' .
            	    {
            	    match('\\'); 
            	    matchAny(); 

            	    }
            	    break;
            	case 2 :
            	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:60:21: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:63:5: ( ( ' ' | '\\n' | '\\t' | '\\r' )+ )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:63:7: ( ' ' | '\\n' | '\\t' | '\\r' )+
            {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:63:7: ( ' ' | '\\n' | '\\t' | '\\r' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\t' && LA2_0<='\n')||LA2_0=='\r'||LA2_0==' ') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:5: ( ( '+' | '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )? )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:7: ( '+' | '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )?
            {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:7: ( '+' | '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='+'||LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:20: ( DIGIT )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:21: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:29: ( '.' ( DIGIT )+ )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='.') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:30: '.' ( DIGIT )+
                    {
                    match('.'); 
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:34: ( DIGIT )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:67:35: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NUMBER"

    // $ANTLR start "SYMBOL"
    public final void mSYMBOL() throws RecognitionException {
        try {
            int _type = SYMBOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:70:5: ( SYMBOL_START ( SYMBOL_START | DIGIT )* )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:70:7: SYMBOL_START ( SYMBOL_START | DIGIT )*
            {
            mSYMBOL_START(); 
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:70:20: ( SYMBOL_START | DIGIT )*
            loop7:
            do {
                int alt7=3;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>='*' && LA7_0<='+')||(LA7_0>='-' && LA7_0<='/')||(LA7_0>='A' && LA7_0<='Z')||(LA7_0>='a' && LA7_0<='z')) ) {
                    alt7=1;
                }
                else if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                    alt7=2;
                }


                switch (alt7) {
            	case 1 :
            	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:70:21: SYMBOL_START
            	    {
            	    mSYMBOL_START(); 

            	    }
            	    break;
            	case 2 :
            	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:70:36: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            	if (".".equals(getText())) {
            		_type = DOT;
            	}
            }           

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SYMBOL"

    // $ANTLR start "SYMBOL_START"
    public final void mSYMBOL_START() throws RecognitionException {
        try {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:75:5: ( ( 'a' .. 'z' ) | ( 'A' .. 'Z' ) | '+' | '-' | '*' | '/' | '.' )
            int alt8=7;
            switch ( input.LA(1) ) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt8=1;
                }
                break;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                {
                alt8=2;
                }
                break;
            case '+':
                {
                alt8=3;
                }
                break;
            case '-':
                {
                alt8=4;
                }
                break;
            case '*':
                {
                alt8=5;
                }
                break;
            case '/':
                {
                alt8=6;
                }
                break;
            case '.':
                {
                alt8=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:75:7: ( 'a' .. 'z' )
                    {
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:75:7: ( 'a' .. 'z' )
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:75:8: 'a' .. 'z'
                    {
                    matchRange('a','z'); 

                    }


                    }
                    break;
                case 2 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:75:20: ( 'A' .. 'Z' )
                    {
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:75:20: ( 'A' .. 'Z' )
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:75:21: 'A' .. 'Z'
                    {
                    matchRange('A','Z'); 

                    }


                    }
                    break;
                case 3 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:76:7: '+'
                    {
                    match('+'); 

                    }
                    break;
                case 4 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:76:13: '-'
                    {
                    match('-'); 

                    }
                    break;
                case 5 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:76:19: '*'
                    {
                    match('*'); 

                    }
                    break;
                case 6 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:76:25: '/'
                    {
                    match('/'); 

                    }
                    break;
                case 7 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:77:7: '.'
                    {
                    match('.'); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "SYMBOL_START"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:81:5: ( ( '0' .. '9' ) )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:81:7: ( '0' .. '9' )
            {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:81:7: ( '0' .. '9' )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:81:8: '0' .. '9'
            {
            matchRange('0','9'); 

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    public void mTokens() throws RecognitionException {
        // /usr/local/projects/light_edit/src/antlr/Sexpr.g:1:8: ( LPAREN | RPAREN | STRING | WHITESPACE | NUMBER | SYMBOL )
        int alt9=6;
        alt9 = dfa9.predict(input);
        switch (alt9) {
            case 1 :
                // /usr/local/projects/light_edit/src/antlr/Sexpr.g:1:10: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 2 :
                // /usr/local/projects/light_edit/src/antlr/Sexpr.g:1:17: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 3 :
                // /usr/local/projects/light_edit/src/antlr/Sexpr.g:1:24: STRING
                {
                mSTRING(); 

                }
                break;
            case 4 :
                // /usr/local/projects/light_edit/src/antlr/Sexpr.g:1:31: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;
            case 5 :
                // /usr/local/projects/light_edit/src/antlr/Sexpr.g:1:42: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 6 :
                // /usr/local/projects/light_edit/src/antlr/Sexpr.g:1:49: SYMBOL
                {
                mSYMBOL(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\5\uffff\1\7\2\uffff\1\7\1\6\1\7\1\6";
    static final String DFA9_eofS =
        "\14\uffff";
    static final String DFA9_minS =
        "\1\11\4\uffff\1\60\2\uffff\1\60\1\52\1\60\1\52";
    static final String DFA9_maxS =
        "\1\172\4\uffff\1\71\2\uffff\1\71\1\172\1\71\1\172";
    static final String DFA9_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\5\1\6\4\uffff";
    static final String DFA9_specialS =
        "\14\uffff}>";
    static final String[] DFA9_transitionS = {
            "\2\4\2\uffff\1\4\22\uffff\1\4\1\uffff\1\3\5\uffff\1\1\1\2\1"+
            "\7\1\5\1\uffff\1\10\2\7\12\6\7\uffff\32\7\6\uffff\32\7",
            "",
            "",
            "",
            "",
            "\12\11",
            "",
            "",
            "\12\11",
            "\2\7\1\uffff\1\7\1\12\1\7\12\11\7\uffff\32\7\6\uffff\32\7",
            "\12\13",
            "\2\7\1\uffff\3\7\12\13\7\uffff\32\7\6\uffff\32\7"
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( LPAREN | RPAREN | STRING | WHITESPACE | NUMBER | SYMBOL );";
        }
    }
 

}