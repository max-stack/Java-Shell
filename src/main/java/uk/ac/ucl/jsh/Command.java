package uk.ac.ucl.jsh;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Comparator;

import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Command {
    String appName; 
    ArrayList<String> appArgs; 
    ArrayList<ArrayList> allTokens;
    static String currentDirectory = System.getProperty("user.dir");
    public void parse(String cmdline) throws IOException{
        CharStream parserInput = CharStreams.fromString(cmdline); 
        JshGrammarLexer lexer = new JshGrammarLexer(parserInput);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);        
        JshGrammarParser parser = new JshGrammarParser(tokenStream);
        ParseTree tree = parser.command();
        ArrayList<String> rawCommands = new ArrayList<String>();
        String lastSubcommand = "";
        for (int i=0; i<tree.getChildCount(); i++) {
            if (!tree.getChild(i).getText().equals(";")) {
                lastSubcommand += tree.getChild(i).getText();
            } else {
                rawCommands.add(lastSubcommand);
                lastSubcommand = "";
            }
        }
        rawCommands.add(lastSubcommand);
        for (String rawCommand : rawCommands) {
            String spaceRegex = "[^\\s\"']+|\"([^\"]*)\"|'([^']*)'";
            ArrayList<String> tokens = new ArrayList<String>();
            Pattern regex = Pattern.compile(spaceRegex);
            Matcher regexMatcher = regex.matcher(rawCommand);
            String nonQuote;
            while (regexMatcher.find()) {
                if (regexMatcher.group(1) != null || regexMatcher.group(2) != null) {
                    String quoted = regexMatcher.group(0).trim();
                    tokens.add(quoted.substring(1,quoted.length()-1));
                } else {
                    nonQuote = regexMatcher.group().trim();
                    ArrayList<String> globbingResult = new ArrayList<String>();
                    Path dir = Paths.get(currentDirectory);
                    DirectoryStream<Path> stream = Files.newDirectoryStream(dir, nonQuote);
                    for (Path entry : stream) {
                        globbingResult.add(entry.getFileName().toString());
                    }
                    if (globbingResult.isEmpty()) {
                        globbingResult.add(nonQuote);
                    }
                    tokens.addAll(globbingResult);
                }
            }
            appName = tokens.get(0);
            appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));
        }
    }

    public void eval(String input, OutputStream output) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(output);
        switch (appName) {
            case "cd":
                ChangeDirectory.evalArgs(appArgs, input, writer);
                break;
            case "pwd":
                PrintWorkingDirectory.evalArgs(appArgs, writer);
                break;
            case "ls":
                List.evalArgs(appArgs, writer);
                break;
            case "cat":
                Concatenate.evalArgs(appArgs, writer);
                break;
            case "echo":
                Echo.evalArgs(appArgs, writer);
                break;
            case "head":
                Head.evalArgs(appArgs, writer);
                break;
            case "tail":
                Tail.evalArgs(appArgs, writer);
                break;
            case "grep":
                GlobalRegExPrint.evalArgs(appArgs, writer);
                break;
            case "sort":
                Sort.evalArgs(appArgs, writer);
                break;             
            default:
                throw new RuntimeException(appName + ": unknown application");
            }
        }
}

class ChangeDirectory extends Command {
    public static void evalArgs(ArrayList<String> appArgs, String input, OutputStreamWriter writer) throws IOException {
         if (appArgs.isEmpty()) {
            throw new RuntimeException("cd: missing argument");
        } else if (appArgs.size() > 1) {
            throw new RuntimeException("cd: too many arguments");
        }
        String dirString = appArgs.get(0);
        File dir = new File(currentDirectory, dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("cd: " + dirString + " is not an existing directory");
        }
        currentDirectory = dir.getCanonicalPath();
    }
}

class PrintWorkingDirectory extends Command {
    public static void evalArgs(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
        writer.write(currentDirectory);
        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }
}

class List extends Command {
    public static void evalArgs(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
        File currDir;
        if (appArgs.isEmpty()) {
            currDir = new File(currentDirectory);
        } else if (appArgs.size() == 1) {
            currDir = new File(appArgs.get(0));
        } else {
            throw new RuntimeException("ls: too many arguments");
        }
        try {
            File[] listOfFiles = currDir.listFiles();
            boolean atLeastOnePrinted = false;
            for (File file : listOfFiles) {
                if (!file.getName().startsWith(".")) {
                    writer.write(file.getName());
                    writer.write("\t");
                    writer.flush();
                    atLeastOnePrinted = true;
                }
            }
            if (atLeastOnePrinted) {
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("ls: no such directory");
        }
    }
}

class Concatenate extends Command {
    public static void evalArgs(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
         if (appArgs.isEmpty()) {
            throw new RuntimeException("cat: missing arguments");
        } else {
            for (String arg : appArgs) {
                Charset encoding = StandardCharsets.UTF_8;
                File currFile = new File(currentDirectory + File.separator + arg);
                if (currFile.exists()) {
                    Path filePath = Paths.get(currentDirectory + File.separator + arg);
                    try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            writer.write(String.valueOf(line));
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("cat: cannot open " + arg);
                    }
                } else {
                    throw new RuntimeException("cat: file does not exist");
                }
            }
        }
    }
}

class Echo extends Command {
    public static void evalArgs(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
        boolean atLeastOnePrinted = false;
        for (String arg : appArgs) {
            writer.write(arg);
            writer.write(" ");
            writer.flush();
            atLeastOnePrinted = true;
        }
        if (atLeastOnePrinted) {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }
}

class Head extends Command {
    public static void evalArgs(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
        if (appArgs.isEmpty()) {
            throw new RuntimeException("head: missing arguments");
        }
        if (appArgs.size() != 1 && appArgs.size() != 3) {
            throw new RuntimeException("head: wrong arguments");
        }
        if (appArgs.size() == 3 && !appArgs.get(0).equals("-n")) {
            throw new RuntimeException("head: wrong argument " + appArgs.get(0));
        }
        int headLines = 10;
        String headArg;
        if (appArgs.size() == 3) {
            try {
                headLines = Integer.parseInt(appArgs.get(1));
            } catch (Exception e) {
                throw new RuntimeException("head: wrong argument " + appArgs.get(1));
            }
            headArg = appArgs.get(2);
        } else {
            headArg = appArgs.get(0);
        }
        File headFile = new File(currentDirectory + File.separator + headArg);
        if (headFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + headArg);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                for (int i = 0; i < headLines; i++) {
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("head: cannot open " + headArg);
            }
        } else {
            throw new RuntimeException("head: " + headArg + " does not exist");
        }
    }
}

class Tail extends Command {
    public static void evalArgs(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
        if (appArgs.isEmpty()) {
            throw new RuntimeException("tail: missing arguments");
        }
        if (appArgs.size() != 1 && appArgs.size() != 3) {
            throw new RuntimeException("tail: wrong arguments");
        }
        if (appArgs.size() == 3 && !appArgs.get(0).equals("-n")) {
            throw new RuntimeException("tail: wrong argument " + appArgs.get(0));
        }
        int tailLines = 10;
        String tailArg;
        if (appArgs.size() == 3) {
            try {
                tailLines = Integer.parseInt(appArgs.get(1));
            } catch (Exception e) {
                throw new RuntimeException("tail: wrong argument " + appArgs.get(1));
            }
            tailArg = appArgs.get(2);
        } else {
            tailArg = appArgs.get(0);
        }
        File tailFile = new File(currentDirectory + File.separator + tailArg);
        if (tailFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + tailArg);
            ArrayList<String> storage = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    storage.add(line);
                }
                int index = 0;
                if (tailLines > storage.size()) {
                    index = 0;
                } else {
                    index = storage.size() - tailLines;
                }
                for (int i = index; i < storage.size(); i++) {
                    writer.write(storage.get(i) + System.getProperty("line.separator"));
                    writer.flush();
                }            
            } catch (IOException e) {
                throw new RuntimeException("tail: cannot open " + tailArg);
            }
        } else {
            throw new RuntimeException("tail: " + tailArg + " does not exist");
        }
    }
}

class GlobalRegExPrint extends Command {
    public static void evalArgs(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
        if (appArgs.size() < 2) {
            throw new RuntimeException("grep: wrong number of arguments");
        }
        Pattern grepPattern = Pattern.compile(appArgs.get(0));
        int numOfFiles = appArgs.size() - 1;
        Path filePath;
        Path[] filePathArray = new Path[numOfFiles];
        Path currentDir = Paths.get(currentDirectory);
        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(appArgs.get(i + 1));
            if (Files.notExists(filePath) || Files.isDirectory(filePath) || 
                !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("grep: wrong file argument");
            }
            filePathArray[i] = filePath;
        }
        for (int j = 0; j < filePathArray.length; j++) {
            Charset encoding = StandardCharsets.UTF_8;
            try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = grepPattern.matcher(line);
                    if (matcher.find()) {
                        if (numOfFiles > 1) {
                            writer.write(appArgs.get(j+1));
                            writer.write(":");
                        }
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("grep: cannot open " + appArgs.get(j + 1));
            }
        }
    }
}


class Sort extends Command {
    public static void evalArgs(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
        if (appArgs.isEmpty()) {
            throw new RuntimeException("sort: missing arguments");
        }
        if (appArgs.size() != 1 && appArgs.size() != 2) {
            throw new RuntimeException("sort: wrong arguments");
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-r")) {
            throw new RuntimeException("sort: wrong argument " + appArgs.get(0));
        }

        boolean reversed = false; 
        String sortArg = appArgs.get(0);

        if (appArgs.size() == 2){
            reversed = true;
            sortArg = appArgs.get(1);
        }

        String sortFile = currentDirectory + File.separator + sortArg;

        try (Stream<String> stream = Files.lines(Paths.get(sortFile))) {
            if (!reversed){
            stream.sorted().forEach(line -> { 
                    try {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    } 
                    catch (IOException e)  {
                        throw new RuntimeException("sort: cannot open " + appArgs.get(0));
                    }
                }
                );
            } else {
             stream.sorted(Comparator.reverseOrder()).forEach(line -> { 
                    try {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    } 
                    catch (IOException e)  {
                        throw new RuntimeException("sort: cannot open " + appArgs.get(0));
                    }
                } 
             );              
            }
        } catch (IOException e) {
            throw new RuntimeException("sort: cannot open " + appArgs.get(0));
        }
    }
}