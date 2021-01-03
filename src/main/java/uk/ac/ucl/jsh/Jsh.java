package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.OutputStream;
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.io.FileWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import uk.ac.ucl.jsh.app.Application;
import uk.ac.ucl.jsh.app.ApplicationFactory;

import org.antlr.v4.runtime.tree.TerminalNode;


//Shell.Java interface
//Takes run(shell, args, input,output)
//output stout then output = stdout
//if pipe give output = outputstream but needs to be on seperate threads
//

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
               
                String tempDir = currentDirectory;
                String relativeDir = "";
                if((nonQuote.indexOf("*") != -1) && !(nonQuote.charAt(0) == '*')){
                    relativeDir = nonQuote.substring(0, nonQuote.indexOf("*"));
                    tempDir = tempDir.concat("/");
                    tempDir = tempDir.concat(relativeDir);
                    dir = Paths.get(tempDir);
                    nonQuote = nonQuote.substring(nonQuote.indexOf("*"), nonQuote.length());
                }
                
                DirectoryStream<Path> stream = Files.newDirectoryStream(dir, nonQuote);
                
                for (Path entry : stream) {
                    if(tempDir.equals(currentDirectory)){
                        globbingResult.add(entry.getFileName().toString());
                    }
                    else{
                        globbingResult.add(relativeDir + entry.getFileName().toString());
                    }
                }
                if (globbingResult.isEmpty()) {
                    globbingResult.add(nonQuote);
                }
                tokens.addAll(globbingResult);
            }
        }
        return tokens;
    }

    public static ExecutionPlan parse(String cmdline){
        CharStream parserInput = CharStreams.fromString(cmdline); 
        JshGrammarLexer lexer = new JshGrammarLexer(parserInput);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);        
        JshGrammarParser parser = new JshGrammarParser(tokenStream);
        ParseTree tree = parser.command();
        CommandVisitor visitor = new CommandVisitor();
        return visitor.visit(tree);
    }

    private static File createRedirectFile(String filePath){
        File returnFile;
        char slash = '/';
        if(filePath.charAt(0) == slash){
            returnFile = new File(filePath);
        } 
        else{
            returnFile = new File(currentDirectory + "/" + filePath);
        }
        return returnFile;
    }

    private static void emptyFile(File file) throws IOException{
         if(file.exists()){
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        }
        else{
            file.getParentFile().mkdir();
            file.createNewFile();
        }
    }

    private static String readInputStream(InputStream input, OutputStream output) throws IOException{
        if(input != null && input.available() != 0){
            final int bufferSize = 1024 * 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder pipeStr = new StringBuilder();
            Reader rdr = new InputStreamReader(input, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
                pipeStr.insert(0,buffer, 0, charsRead);
            }
            String inputText = pipeStr.toString();
            output.write(inputText.getBytes());
            inputText = inputText.replace("\n", "");
            return inputText;
        }
        return "";
    }

    // private static void inputToOutput (OutputStream out, InputStream in){
    //     int charRead;
    //     while((charRead = in.read()) > 0){
    //         out.write(charRead);
    //     }
    // }

    public static void eval(String cmdline) throws IOException{
        Queue<String> commands = parse(cmdline).getCommandQueue();
        ExecutorService executor = Executors.newCachedThreadPool();
        InputStream lastInput = null;
        OutputStream subOutput = null;
        InputStream subInput = null;
        boolean substitution = false;
        OutputStream wholeSubOutput = null;
        InputStream wholeSubInput = null;
        String subCommand = "";
        PipedInputStream pipedInput;

        while(!commands.isEmpty()){
            InputStream input = lastInput;
            OutputStream output = System.out;
            if(substitution){
                subCommand += readInputStream(subInput, wholeSubOutput);
                pipedInput = new PipedInputStream();
                subOutput = new PipedOutputStream(pipedInput);
                subInput = pipedInput;
                output = subOutput;
            }
            
            String command = commands.poll();

            if(command == ConnectionType.SUBSTITUTION.toString()){
                if(!substitution){
                    substitution = true;
                    pipedInput = new PipedInputStream();
                    wholeSubOutput = new PipedOutputStream(pipedInput);
                    wholeSubInput = pipedInput;
                    continue;
                }
                else{
                    substitution = false;
                    command = commands.poll();
                    if(command.substring(0, 4).equals("echo")){
                        if(commands.peek() != null && !ConnectionType.connectionExists(commands.peek())){
                            command = command + subCommand + commands.poll();
                        }
                        else{
                            command = command + subCommand;
                        }
                    }
                    else{
                        input = wholeSubInput;
                    }
                    output.close();
                    wholeSubOutput.close();
                    output = System.out;
                }
            }
            if(ConnectionType.connectionExists(commands.peek())){
                if(commands.peek() == ConnectionType.SEQUENCE.toString()){
                    commands.poll();
                    lastInput = null;
                }

                if(commands.peek() == ConnectionType.REDIRECT_FROM.toString()){
                    String filePath;
                    File readFile;
                    do{
                        commands.poll();
                        filePath = commands.poll().trim();
                        readFile = createRedirectFile(filePath);
                        if(!readFile.exists()){
                            throw new IOException("File " + readFile.getName() + " Does not exist.");
                        }
                    } while(commands.peek() == ConnectionType.REDIRECT_FROM.toString());                   
                    input = new FileInputStream(readFile);
                }

                if(commands.peek() == ConnectionType.REDIRECT_TO.toString()){
                    String filePath;
                    File writeFile;
                    do{
                        commands.poll();
                        filePath = commands.poll().trim();
                        writeFile = createRedirectFile(filePath);
                        emptyFile(writeFile);
                    } while(commands.peek() == ConnectionType.REDIRECT_TO.toString());
                    while(commands.peek() == ConnectionType.REDIRECT_FROM.toString() ||
                          commands.peek() == ConnectionType.REDIRECT_TO.toString()){
                        if(commands.peek() == ConnectionType.REDIRECT_FROM.toString()){
                            commands.poll();
                            commands.poll();
                        }
                        else if(commands.peek() == ConnectionType.REDIRECT_TO.toString()){
                            commands.poll();
                            filePath = commands.poll().trim();
                            emptyFile(createRedirectFile(filePath));
                        }
                    }
                    output = new FileOutputStream(writeFile, true);
                }

                if(commands.peek() == ConnectionType.PIPE.toString()){
                    pipedInput = new PipedInputStream();
                    output = new PipedOutputStream(pipedInput);
                    lastInput = pipedInput;
                    commands.poll();
                }
            }
            ArrayList<String> tokens = tokenSplit(command);
            String appName = tokens.get(0);
            ApplicationFactory.make(appName);
            Boolean unsafe = false;
            if (appName.length() > 1 && appName.substring(0,1).equals("_")) { unsafe = true; }
            executor.execute(new RunCommand(tokens, output, input, unsafe));
            
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
        }
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