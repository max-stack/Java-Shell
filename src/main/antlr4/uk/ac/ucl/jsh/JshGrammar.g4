grammar JshGrammar;

/*
 * Parser Rules
 */

command : atomicCommand (SEPARATOR atomicCommand)*;

atomicCommand : (NONSPECIAL | DOUBLEQUOTED | SINGLEQUOTED)+;

/*
 * Lexer Rules
 */

NONSPECIAL : ~[;|<>]+;
SEPARATOR : ';' | '|' | '>' | '<';
DOUBLEQUOTED : '"' (~'"')* '"';
SINGLEQUOTED : '\'' (~'\'')* '\'';