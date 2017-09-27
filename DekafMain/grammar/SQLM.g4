grammar SQLM;


/// LEXER RULES \\\

fragment L:     [A-Za-z] ;
fragment D:     [0-9]    ;

LINE:           [-–—][-–—][-–—][-–—]+ ;
WORD:           (L|'_'|'$') (L|D|'_'|'$')* ;

DELIM_SEMI:     ('\r'? '\n' [ \t]*)* ';'      [ \t]* '\r'? '\n' ;
DELIM_SLASH:    ('\r'? '\n' [ \t]*)* '/'      [ \t]* '\r'? '\n' ;
DELIM_GO:       ('\r'? '\n' [ \t]*)* [Gg][Oo] [ \t]* '\r'? '\n' ;

SP:             [ \t]+        -> skip ;
NL:             '\r'? '\n' ;

ANY:            ~[ \t\r\n] .*? ;



/// PARSER RULES \\\


file
:       sectionN+
|       section1 sectionN*
;


section1
:       script
;


sectionN
:       head script
;


head
:       LINE WORD LINE NL
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
:       ( LINE | WORD | ANY | NL ) +
;