package Absyn;

import java_cup.runtime.*;
import java.util.ArrayList;

import Absyn.*;
%%

%public
%class Scanner
%implements sym
%cup
%line
%unicode
%column

%{
    StringBuilder string = new StringBuilder();
    public static ArrayList<String> errors = new ArrayList<String>();
    private Symbol symbol(int type) {
        return new Symbol(type, yyline+1, yycolumn+1);
    }
    /* for error checking */
    public int yyline(){
        return yyline+1;
    }
    public int yycolumn(){
        return yycolumn+1;
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
    public static ArrayList<String> getErrors(){
        return Scanner.errors;
    }
%}
Identifier = [A-Za-z][A-Za-z0-9_]*
Intnum = [0-9]+
Floatnum = [0-9]+\.[0-9]+
Whitespace = [\ \t\f]
Newline = \r|\n|\r\n

%%
/* yytext() : matched regular expression context 
   yyline   : matched line number
*/
<YYINITIAL> {
    "+"             {return symbol(sym.ADD, "+");}
    "-"             {return symbol(sym.SUB, "-");}
    "*"             {return symbol(sym.MUL, "*");}
    "/"             {return symbol(sym.DIV, "/");}
    "="             {return symbol(sym.ASSIGN, "=");}
    "=="            {return symbol(sym.EQ, "==");}
    "<"             {return symbol(sym.LT, "<");}
    ">"             {return symbol(sym.GT, ">");}
    "<="            {return symbol(sym.LTE, "<=");}
    ">="            {return symbol(sym.GTE, ">=");}
    "!="            {return symbol(sym.NEQ, "!=");}
    "("             {return symbol(sym.LPR);}
    ")"             {return symbol(sym.RPR);}
    "{"             {return symbol(sym.LBR);}
    "}"             {return symbol(sym.RBR);}
    "["             {return symbol(sym.LSQ);}
    "]"             {return symbol(sym.RSQ);}
    ":"             {return symbol(sym.COLON);}
    ";"             {return symbol(sym.SEMI, ";");}
    ","             {return symbol(sym.COMMA);}
    "int"           {return symbol(sym.INT);}
    "float"         {return symbol(sym.FLOAT);}
    "return"        {return symbol(sym.RETURN);}
    "do"            {return symbol(sym.DO);}
    "while"         {return symbol(sym.WHILE);}
    "if"            {return symbol(sym.IF);}
    "else"          {return symbol(sym.ELSE);}
    "switch"        {return symbol(sym.SWITCH);}
    "for"           {return symbol(sym.FOR);}
    "case"          {return symbol(sym.CASE);}
    "break"         {return symbol(sym.BREAK);}
    "default"       {return symbol(sym.DEFAULT);}
    {Intnum}        {return symbol(sym.INT_N,yytext());}
    {Floatnum}      {return symbol(sym.FLOAT_N,yytext());}
    {Identifier}    {return symbol(ID,yytext());}
    {Newline}       {/* ignore */}
    {Whitespace}    {/* ignore */}
    .               {Scanner.errors.add(new String(":" + (yyline+1)+ ":"+ (yycolumn+1)
                                        + ": error: illegal character: "+ yytext()));}
}
