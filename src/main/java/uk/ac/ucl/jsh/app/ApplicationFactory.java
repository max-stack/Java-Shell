package uk.ac.ucl.jsh.app;

public class ApplicationFactory {

    public static Application make(String name) {
        switch (name) {
            case "cd":
                return new ChangeDirectory(new Safe());
            case "_cd":
                return new ChangeDirectory(new Unsafe());

            case "pwd":
            case "_pwd":
                return new PrintWorkingDirectory();

            case "ls":
                return new List(new Safe());
            case "_ls":
                return new List(new Unsafe());

            case "cat":
                return new Concatenate(new Safe());
            case "_cat":
                return new Concatenate(new Unsafe());

            case "echo":
                return new Echo(new Safe());
            case "_echo":
                return new Echo(new Unsafe());

            case "head":
                return new Head(new Safe());
            case "_head":
                return new Head(new Unsafe());

            case "tail":
                return new Tail(new Safe());
            case "_tail":
                return new Tail(new Unsafe());

            case "grep":
                return new GlobalRegExPrint(new Safe());
            case "_grep":
                return new GlobalRegExPrint(new Unsafe());

            case "cut":
                return new Cut(new Safe());
            case "_cut":
                return new Cut(new Unsafe());

            case "sort":
                return new Sort(new Safe());
            case "_sort":
                return new Sort(new Unsafe());

            case "uniq":
                return new Unique(new Safe());
            case "_uniq":
                return new Unique(new Unsafe());

            case "find":
                return new Find(new Safe());
            case "_find":
                return new Find(new Unsafe());

            default:
                throw new RuntimeException(name + ": unknown application");
        }
    }
}
