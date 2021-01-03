grammar JshGrammar;

/*
 * Parser Rules
 */

command : WHITESPACE* | atomicCommand (SEPARATOR atomicCommand)* | (atomicCommand SEPARATOR*)+;

atomicCommand : (NONSPECIAL | DOUBLEQUOTED | SINGLEQUOTED)+;

/*
 * Lexer Rules
 */

NONSPECIAL : ~[;|<>`]+;
SEPARATOR : ';' | '|' | '>' | '<' | '`';
DOUBLEQUOTED : '"' (~'"')* '"';
SINGLEQUOTED : '\'' (~'\'')* '\'';