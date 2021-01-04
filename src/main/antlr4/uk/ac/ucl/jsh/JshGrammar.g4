grammar JshGrammar;

/*
 * Parser Rules
 */

<<<<<<< HEAD
command : WHITESPACE* | SEPARATOR2* atomicCommand ((SEPARATOR1|SEPARATOR2) atomicCommand)* | (SEPARATOR2* atomicCommand (SEPARATOR1|SEPARATOR2)*)+;
=======
command : WHITESPACE* | atomicCommand (SEPARATOR atomicCommand)* | (atomicCommand SEPARATOR*)+;
>>>>>>> cdc64fd61f6e83d3d0938dee2b2417b53a3c9750

atomicCommand : (NONSPECIAL | DOUBLEQUOTED | SINGLEQUOTED)+;

/*
 * Lexer Rules
 */

NONSPECIAL : ~[;|<>`]+;
<<<<<<< HEAD
SEPARATOR1 : ';' | '|';
SEPARATOR2 : '>' | '<' | '`';
DOUBLEQUOTED : '"' (~('"' | '`'))* '"';
SINGLEQUOTED : '\'' (~'\'')* '\'';
=======
SEPARATOR : ';' | '|' | '>' | '<' | '`';
DOUBLEQUOTED : '"' (~'"')* '"';
SINGLEQUOTED : '\'' (~'\'')* '\'';
>>>>>>> cdc64fd61f6e83d3d0938dee2b2417b53a3c9750
