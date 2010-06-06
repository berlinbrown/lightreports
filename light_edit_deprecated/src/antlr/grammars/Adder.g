/**
 * Date: 3/21/2009
 * Example Antlr 'Adder', simple interpreter for adding two numbers.
 * Modified by: Berlin Brown
 *
 * Tested on Version: Antlr runtime 3.1.2.
 * From the antlr reference book
 *
 * Usage:
 *    org.antlr.Tool Adder.g
 */
grammar Adder;

@header {
    import java.util.HashMap;
}

@members {
    final HashMap memory = new HashMap();
}

prog:   stat+ ;

// START: stat
stat:   // Evaluate expr and emit result
        expr NEWLINE { System.out.println($expr.value); }
    
        // Match assignment and stored value
    |   ID '=' expr NEWLINE { memory.put($ID.text, new Integer($expr.value)); }
    |   NEWLINE                           
    ;
// END: stat

expr returns [int value]
    :   e = multExpr  { $value = $e.value; }
        (   '+' e = multExpr  { $value += $e.value; }
        |   '-' e = multExpr  { $value -= $e.value; }
        )*   
    ;
// END: expr

multExpr returns [int value]
    :   e=atom { $value = $e.value; }  
        ('*' e=atom { $value *= $e.value; } )*
    ;

// START:atom
atom returns [int value]
    :   // Value of an INT
        INT { $value = Integer.parseInt($INT.text); }
        
    |   ID
        {
            // Look up value of variable
            Integer v = (Integer) memory.get($ID.text);
            
            // If found , set return value
            if    (v != null) $value = v.intValue();
            else  System.err.println("Undefined variable " + $ID.text);
        }
        
        // Value of expression
    |   '(' expr ')' { $value = $expr.value; }
    ;
// END : atom

// START : tokens
CLOJURE_KEYWORDS :   'call'  { System.out.println("--->"); } ;
ID          :   ('a'..'z' | 'A'..'Z')+      ;
INT         :   '0'..'9'+                   ;
NEWLINE     :   '\r'? '\n'                  ;
WS          :   ( ' ' | '\t' | '\n' | '\r' )+ { skip(); } ;

// END TOKENS

