package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.OutputStream;
<<<<<<< HEAD
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
=======
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
>>>>>>> ff8a819 (Updating Jsh for testing threads)
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Deque;
import java.util.Deque;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.FileWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import uk.ac.ucl.jsh.app.Application;
import uk.ac.ucl.jsh.app.ApplicationFactory;

import org.antlr.v4.runtime.tree.TerminalNode;

<<<<<<< HEAD
=======

//Shell.Java interface
//Takes run(shell, args, input,output)
//output stout then output = stdout
//if pipe give output = outputstream but needs to be on seperate threads
//
<<<<<<< HEAD
>>>>>>> 1165f9e8d4dec7c9bfd8565e196699bf780a9f0a
=======

>>>>>>> 33f1a45 (Starting point for new execution using CommandVisitor)
public class Jsh {

    public static String currentDirectory = System.getProperty("user.dir");

    public static ArrayList<String> tokenSplit(String rawCommand) throws IOException{
        String spaceRegex = "\"([^\"]*)\"|'([^']*)'|[^\\s]+";
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
        return tokens;
    }

<<<<<<< HEAD
    public static void eval(String cmdline, OutputStream output) throws IOException {
=======
    public static ExecutionPlan parse(String cmdline){
<<<<<<< HEAD
>>>>>>> 1165f9e8d4dec7c9bfd8565e196699bf780a9f0a

=======
>>>>>>> 33f1a45 (Starting point for new execution using CommandVisitor)
        CharStream parserInput = CharStreams.fromString(cmdline); 
        JshGrammarLexer lexer = new JshGrammarLexer(parserInput);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);        
        JshGrammarParser parser = new JshGrammarParser(tokenStream);
        ParseTree tree = parser.command();
        CommandVisitor visitor = new CommandVisitor();
<<<<<<< HEAD
        System.out.println(visitor.visit(tree).getCommandQueue());
        Queue<String> commands = visitor.visit(tree).getCommandQueue();
        LinkedList<String> pipeCommands = new LinkedList<>();
        while(!commands.isEmpty()){
            String command = commands.poll();
            if(ConnectionType.connectionExists(command)){
                if(command == ConnectionType.SEQUENCE.toString()){
                    for(int i = 0; i < 2; i++){
                        String tempCommand = commands.poll();
                        ArrayList<String> tokens = tokenSplit(tempCommand);
                        String appName = tokens.get(0);
                        ArrayList<String> appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));
                        Application app = ApplicationFactory.make(appName);
                        app.exec(appArgs, null, output);
                    }
=======
        return visitor.visit(tree);
    }

    public static void eval(String cmdline) throws IOException{
        Queue<String> commands = parse(cmdline).getCommandQueue();
        System.out.println(commands);
        ExecutorService executor = Executors.newCachedThreadPool();
        InputStream lastInput = null;

        while(!commands.isEmpty()){
            InputStream input = lastInput;
            OutputStream output = System.out;

            String command = commands.poll();

            if(ConnectionType.connectionExists(command)){
                if(command == ConnectionType.SEQUENCE.toString()){
                    lastInput = null;
                    output = System.out;
                    continue;
                }

                if(command == ConnectionType.REDIRECT_TO.toString()){
                    command = commands.poll();
                    while(commands.peek() == ConnectionType.REDIRECT_TO.toString()){
                        commands.poll();
                        File writeFile = new File (commands.peek().trim());
                        if(writeFile.exists()){
                            PrintWriter writer = new PrintWriter(commands.peek().trim());
                            writer.print("");
                            writer.close();
                        }
                        else{
                            writeFile.createNewFile();
                        }
                        commands.poll();
                    }
                    output = new FileOutputStream(commands.peek().trim(), true);
                    commands.poll();
                }

                if(command == ConnectionType.REDIRECT_FROM.toString()){
                    command = commands.poll();
                    File readFile = new File(commands.peek().trim());
                    while(commands.peek() == ConnectionType.REDIRECT_FROM.toString()){
                        commands.poll();
                        readFile = new File(commands.peek().trim());
                        if(!readFile.exists()){
                            throw new IOException("File " + readFile.getName() + " Does not exist.");
                        }
                        commands.poll();
                    }
                    input = new FileInputStream(readFile);
                }

                if(command == ConnectionType.PIPE.toString()){
                    PipedInputStream pipedIn = new PipedInputStream();
                    output = new PipedOutputStream(pipedIn);
                    lastInput = pipedIn;
<<<<<<< HEAD
>>>>>>> 1165f9e8d4dec7c9bfd8565e196699bf780a9f0a
=======
                    command = commands.poll();
>>>>>>> a01b8a3 (Adding redirect to and from commands)
                }
                
            }
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
            else{
                ArrayList<String> tokens = tokenSplit(command);
                String appName = tokens.get(0);
                ArrayList<String> appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));
                final PipedInputStream in = new PipedInputStream();
                final OutputStream out = new PipedOutputStream(in);
                ExecutorService executor = Executors.newCachedThreadPool();
                executor.execute(new RunCommand(appArgs, out, in));
                System.out.println("Hello");
                byte[] test1 = new byte[100];
                in.read(test1);
                String testString = new String(test1);
                System.out.println(testString);
                // Application app = ApplicationFactory.make(appName);
                // app.exec(appArgs, null, output);
            }
=======
=======
>>>>>>> 2d99439 (Adding input streams as parameters to applicaitions - work with the)
=======

>>>>>>> a01b8a3 (Adding redirect to and from commands)
            ArrayList<String> tokens = tokenSplit(command);
            ApplicationFactory.make(tokens.get(0));
            executor.execute(new RunCommand(tokens, output, input));
            
            if((command == ConnectionType.SEQUENCE.toString() || !ConnectionType.connectionExists(command))){
                executor.shutdown();
                try{
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                executor = Executors.newCachedThreadPool();
            }
            // byte[] test1 = new byte[100];
            // pipedIn.read(test1);
            // String testString = new String(test1);
            // System.out.println(testString);
<<<<<<< HEAD


>>>>>>> 1165f9e8d4dec7c9bfd8565e196699bf780a9f0a
=======
>>>>>>> 33f1a45 (Starting point for new execution using CommandVisitor)
        }

       
        // ArrayList<String> rawCommands = new ArrayList<String>();
        // String lastSubcommand = "";
        // for (int i=0; i<tree.getChildCount(); i++) {
        //     if (!tree.getChild(i).getText().equals(";")) {
        //         lastSubcommand += tree.getChild(i).getText();
        //     } else {
        //         rawCommands.add(lastSubcommand);
        //         lastSubcommand = "";
        //     }
        // }
        // rawCommands.add(lastSubcommand);
        // for (String rawCommand : rawCommands) {
        //     String spaceRegex = "[^\\s\"']+|\"([^\"]*)\"|'([^']*)'";
        //     ArrayList<String> tokens = new ArrayList<String>();
        //     Pattern regex = Pattern.compile(spaceRegex);
        //     Matcher regexMatcher = regex.matcher(rawCommand);
        //     String nonQuote;
        //     while (regexMatcher.find()) {
        //         if (regexMatcher.group(1) != null || regexMatcher.group(2) != null) {
        //             String quoted = regexMatcher.group(0).trim();
        //             tokens.add(quoted.substring(1,quoted.length()-1));
        //         } else {
        //             nonQuote = regexMatcher.group().trim();
        //             ArrayList<String> globbingResult = new ArrayList<String>();
        //             Path dir = Paths.get(currentDirectory);
        //             DirectoryStream<Path> stream = Files.newDirectoryStream(dir, nonQuote);
        //             for (Path entry : stream) {
        //                 globbingResult.add(entry.getFileName().toString());
        //             }
        //             if (globbingResult.isEmpty()) {
        //                 globbingResult.add(nonQuote);
        //             }
        //             tokens.addAll(globbingResult);
        //         }
        //     }
            
        //     String appName = tokens.get(0);
        //     ArrayList<String> appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));
        //     Application app = ApplicationFactory.make(appName);
        //     app.exec(appArgs, output);
        // }
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("jsh: wrong number of arguments");
                return;
            }
            if (!args[0].equals("-c")) {
                System.out.println("jsh: " + args[0] + ": unexpected argument");
            }
            try {
                eval(args[1]);
            } catch (Exception e) {
                System.out.println("jsh: " + e.getMessage());
            }
        } else {
            Scanner input = new Scanner(System.in);
            try {
                while (true) {
                    String prompt = currentDirectory + "> ";
                    System.out.print(prompt);
                    try {
                        String cmdline = input.nextLine();
                        eval(cmdline);
                    } catch (Exception e) {
                        System.out.println("jsh: " + e.getMessage());
                    }
                }
            } finally {
                input.close();
            }
        }
    }

}