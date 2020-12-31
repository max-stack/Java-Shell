grammar JshGrammar;

/*
 * Parser Rules
 */

command : WHITESPACE* | atomicCommand (SEPARATOR atomicCommand)*;

atomicCommand : (NONSPECIAL | DOUBLEQUOTED | SINGLEQUOTED)+;

/*
 * Lexer Rules
 */


NONSPECIAL : ~[;|<>]+;
SEPARATOR : ';' | '|' | '>' | '<';
DOUBLEQUOTED : '"' (~'"')* '"';
SINGLEQUOTED : '\'' (~'\'')* '\''; 