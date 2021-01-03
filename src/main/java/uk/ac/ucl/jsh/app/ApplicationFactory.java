package uk.ac.ucl.jsh.app;

public class ApplicationFactory {

    public static Application make(String name) {
        switch (name) {
            case "cd": case "__cd":
                return new ChangeDirectory();
            case "pwd": case "__pwd":
                return new PrintWorkingDirectory();
            case "ls": case "__ls":
                return new List();
            case "cat": case "__cat":
                return new Concatenate();
            case "echo": case "__echo":
                return new Echo();
            case "head": case "__head":
                return new Head();
            case "tail": case "__tail":
                return new Tail();
            case "grep": case "__grep":
                return new GlobalRegExPrint();
            case "cut": case "__cut":
                return new Cut();
            case "sort": case "__sort":
                return new Sort();
            case "uniq": case "__uniq":
                return new Unique();
            case "find": case "__find":
                return new Find();       
            default:
                throw new RuntimeException(name + ": unknown application");
        }
    }
}