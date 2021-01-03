package uk.ac.ucl.jsh.app;

public class ApplicationFactory {

    public static Application make(String name) {
        switch (name) {
            case "cd": case "_cd":
                return new ChangeDirectory();
            case "pwd": case "_pwd":
                return new PrintWorkingDirectory();
            case "ls": case "_ls":
                return new List();
            case "cat": case "_cat":
                return new Concatenate();
            case "echo": case "_echo":
                return new Echo();
            case "head": case "_head":
                return new Head();
            case "tail": case "_tail":
                return new Tail();
            case "grep": case "_grep":
                return new GlobalRegExPrint();
            case "cut": case "_cut":
                return new Cut();
            case "sort": case "_sort":
                return new Sort();
            case "uniq": case "_uniq":
                return new Unique();
            case "find": case "_find":
                return new Find();       
            default:
                throw new RuntimeException(name + ": unknown application");
        }
    }
}