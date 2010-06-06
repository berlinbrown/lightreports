// $ANTLR 3.1.2 /usr/local/projects/light_edit/src/antlr/Sexpr.g 2009-03-13 22:17:06

package com.light.parser;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

public class SexprParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LPAREN", "RPAREN", "DOT", "STRING", "SYMBOL", "NUMBER", "WHITESPACE", "DIGIT", "SYMBOL_START"
    };
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


        public SexprParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public SexprParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return SexprParser.tokenNames; }
    public String getGrammarFileName() { return "/usr/local/projects/light_edit/src/antlr/Sexpr.g"; }


    public static class sexpr_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sexpr"
    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:44:1: sexpr : ( item )* EOF ;
    public final SexprParser.sexpr_return sexpr() throws RecognitionException {
        SexprParser.sexpr_return retval = new SexprParser.sexpr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token EOF2=null;
        SexprParser.item_return item1 = null;


        CommonTree EOF2_tree=null;

        try {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:45:5: ( ( item )* EOF )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:45:7: ( item )* EOF
            {
            root_0 = (CommonTree)adaptor.nil();

            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:45:7: ( item )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==LPAREN||(LA1_0>=STRING && LA1_0<=NUMBER)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:45:7: item
            	    {
            	    pushFollow(FOLLOW_item_in_sexpr79);
            	    item1=item();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, item1.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_sexpr82); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF2_tree = (CommonTree)adaptor.create(EOF2);
            adaptor.addChild(root_0, EOF2_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sexpr"

    public static class item_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "item"
    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:47:1: item : ( atom | ( list )=> list | LPAREN item RPAREN );
    public final SexprParser.item_return item() throws RecognitionException {
        SexprParser.item_return retval = new SexprParser.item_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LPAREN5=null;
        Token RPAREN7=null;
        SexprParser.atom_return atom3 = null;

        SexprParser.list_return list4 = null;

        SexprParser.item_return item6 = null;


        CommonTree LPAREN5_tree=null;
        CommonTree RPAREN7_tree=null;

        try {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:48:5: ( atom | ( list )=> list | LPAREN item RPAREN )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>=STRING && LA2_0<=NUMBER)) ) {
                alt2=1;
            }
            else if ( (LA2_0==LPAREN) ) {
                int LA2_2 = input.LA(2);

                if ( (synpred1_Sexpr()) ) {
                    alt2=2;
                }
                else if ( (true) ) {
                    alt2=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:48:7: atom
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_atom_in_item98);
                    atom3=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom3.getTree());

                    }
                    break;
                case 2 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:49:7: ( list )=> list
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_list_in_item111);
                    list4=list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, list4.getTree());

                    }
                    break;
                case 3 :
                    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:50:7: LPAREN item RPAREN
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    LPAREN5=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_item119); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LPAREN5_tree = (CommonTree)adaptor.create(LPAREN5);
                    adaptor.addChild(root_0, LPAREN5_tree);
                    }
                    pushFollow(FOLLOW_item_in_item121);
                    item6=item();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, item6.getTree());
                    RPAREN7=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_item123); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RPAREN7_tree = (CommonTree)adaptor.create(RPAREN7);
                    adaptor.addChild(root_0, RPAREN7_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "item"

    public static class list_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "list"
    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:52:1: list : LPAREN ( item )* RPAREN ;
    public final SexprParser.list_return list() throws RecognitionException {
        SexprParser.list_return retval = new SexprParser.list_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LPAREN8=null;
        Token RPAREN10=null;
        SexprParser.item_return item9 = null;


        CommonTree LPAREN8_tree=null;
        CommonTree RPAREN10_tree=null;

        try {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:53:5: ( LPAREN ( item )* RPAREN )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:53:7: LPAREN ( item )* RPAREN
            {
            root_0 = (CommonTree)adaptor.nil();

            LPAREN8=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_list139); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LPAREN8_tree = (CommonTree)adaptor.create(LPAREN8);
            adaptor.addChild(root_0, LPAREN8_tree);
            }
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:53:14: ( item )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==LPAREN||(LA3_0>=STRING && LA3_0<=NUMBER)) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:53:14: item
            	    {
            	    pushFollow(FOLLOW_item_in_list141);
            	    item9=item();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, item9.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            RPAREN10=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_list144); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RPAREN10_tree = (CommonTree)adaptor.create(RPAREN10);
            adaptor.addChild(root_0, RPAREN10_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "list"

    public static class atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // /usr/local/projects/light_edit/src/antlr/Sexpr.g:55:1: atom : ( STRING | SYMBOL | NUMBER );
    public final SexprParser.atom_return atom() throws RecognitionException {
        SexprParser.atom_return retval = new SexprParser.atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set11=null;

        CommonTree set11_tree=null;

        try {
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:56:5: ( STRING | SYMBOL | NUMBER )
            // /usr/local/projects/light_edit/src/antlr/Sexpr.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set11=(Token)input.LT(1);
            if ( (input.LA(1)>=STRING && input.LA(1)<=NUMBER) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set11));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "atom"

    // $ANTLR start synpred1_Sexpr
    public final void synpred1_Sexpr_fragment() throws RecognitionException {   
        // /usr/local/projects/light_edit/src/antlr/Sexpr.g:49:7: ( list )
        // /usr/local/projects/light_edit/src/antlr/Sexpr.g:49:8: list
        {
        pushFollow(FOLLOW_list_in_synpred1_Sexpr107);
        list();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_Sexpr

    // Delegated rules

    public final boolean synpred1_Sexpr() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_Sexpr_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_item_in_sexpr79 = new BitSet(new long[]{0x0000000000000390L});
    public static final BitSet FOLLOW_EOF_in_sexpr82 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_item98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_list_in_item111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_item119 = new BitSet(new long[]{0x0000000000000390L});
    public static final BitSet FOLLOW_item_in_item121 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RPAREN_in_item123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_list139 = new BitSet(new long[]{0x00000000000003B0L});
    public static final BitSet FOLLOW_item_in_list141 = new BitSet(new long[]{0x00000000000003B0L});
    public static final BitSet FOLLOW_RPAREN_in_list144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_atom0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_list_in_synpred1_Sexpr107 = new BitSet(new long[]{0x0000000000000002L});

}