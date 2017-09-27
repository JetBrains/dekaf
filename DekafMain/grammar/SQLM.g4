grammar SQLM;


/// LEXER RULES \\\

fragment L:     [A-Za-z] ;
fragment D:     [0-9]    ;

HEAD_LINE:      [-–—][-–—][-–—][-–—]+ ;
HEAD_ID:        (L|D|'_'|'$')+ ;

DELIM_SEMI:     ('\r'? '\n' [ \t]*)* ';'      [ \t]* '\r'? '\n' ;
DELIM_SLASH:    ('\r'? '\n' [ \t]*)* '/'      [ \t]* '\r'? '\n' ;
DELIM_GO:       ('\r'? '\n' [ \t]*)* [Gg][Oo] [ \t]* '\r'? '\n' ;

SP:             [ \t]+        -> skip ;
NL:             '\r'? '\n' ;

ANY:            ~[ \t\r\n] .*? ;



/// PARSER RULES \\\


file
:       section1 sectionN*
|       sectionN+
;


section1
:       script
;


sectionN
:       head script
;


head
:       HEAD_LINE HEAD_ID HEAD_LINE NL
;


script
:       statement ( delimiter statement )* delimiter?
;


delimiter
:       DELIM_SEMI
|       DELIM_SLASH
|       DELIM_GO
;


statement
:       ANY
;