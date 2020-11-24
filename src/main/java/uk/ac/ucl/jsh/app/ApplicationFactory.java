package uk.ac.ucl.jsh.app;

public class ApplicationFactory{

    public static Application make(String name){
        switch (name) {
            case "cd":
                return new ChangeDirectory();
            case "pwd":
                return new PrintWorkingDirectory();
            case "ls":
                return new List();
            case "cat":
                return new Concatenate();
            case "echo":
                return new Echo();
            case "head":
                return new Head();
            case "tail":
                return new Tail();
            case "grep":
                return new GlobalRegExPrint();
            case "sort":
                return new Sort();
            case "uniq":
                return new Unique();            
            default:
                throw new RuntimeException(name + ": unknown application");
        }
    }
}