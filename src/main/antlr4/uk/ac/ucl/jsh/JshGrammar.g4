grammar JshGrammar;

/*
 * Parser Rules
 */

command : WHITESPACE* | SEPARATOR2* atomicCommand ((SEPARATOR1|SEPARATOR2) atomicCommand)* | (SEPARATOR2* atomicCommand (SEPARATOR1|SEPARATOR2)*)+;

atomicCommand : (NONSPECIAL | DOUBLEQUOTED | SINGLEQUOTED)+;

/*
 * Lexer Rules
 */

NONSPECIAL : ~[;|<>`]+;
SEPARATOR1 : ';' | '|';
SEPARATOR2 : '>' | '<' | '`';
DOUBLEQUOTED : '"' (~('"' | '`'))* '"';
SINGLEQUOTED : '\'' (~'\'')* '\'';
