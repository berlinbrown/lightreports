/**
 * Date: 3/21/2009
 * Example Antlr Very basic parser
 * Modified by: Berlin Brown
 *
 * Tested on Version: Antlr runtime 3.1.2.
 * From the antlr reference book
 *
 * Usage:
 *
 *      org.antlr.Tool T.g
 */
grammar T;

r : 'call' ID ';' { System.out.println("invoke " + $ID.text); } ;

ID: 'a'..'z'+ ;

WHITESPACE: (' ' | '\n' | '\r')+  { $channel = HIDDEN; } ;
