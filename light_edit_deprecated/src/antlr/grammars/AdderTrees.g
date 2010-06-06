grammar AdderTrees;

options {
    output=AST;
    ASTLabelType=CommonTree;
}

@header {
    import java.util.HashMap;
}

@members {
    final HashMap memory = new HashMap();
}

//////////////////////////////////////////////////////////////////////

prog:  ( stat  { 
        System.out.println ($stat.tree == null ? "null" : $stat.tree.toStringTree()); } )+
    ;

// START: stat
stat:   
        expr NEWLINE        -> expr    
    |   ID '=' expr NEWLINE -> ^('=' ID expr)
    |   NEWLINE             ->                           
    ;
// END: stat

expr :  multExpr (('+'^ | '-'^) multExpr)*
    ;
// END: expr

multExpr
    :   atom ('*'^ atom)*
    ;

atom    
    :   INT
    |   ID
    |   '('! expr ')'!
    ;        


// START : tokens
ID          :   ('a'..'z' | 'A'..'Z')+      ;
INT         :   '0'..'9'+                   ;
NEWLINE     :   '\r'? '\n'                  ;
WS          :   ( ' ' | '\t' | '\n' | '\r' )+ { skip(); } ;

// END TOKENS

