package uk.ac.ucl.jsh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import uk.ac.ucl.jsh.app.ApplicationFactory;
import org.apache.commons.lang3.StringUtils;

public class Jsh {

    public static String currentDirectory = System.getProperty("user.dir");
    public static boolean quitCommand = false;

    public static void setQuitCommand (boolean val){
        quitCommand = val;
    }

    /**
     * This method takes in a raw command and performs globbing if * is
     * found within the the command as well as splitting the command up
     * into individual tokens.
     *
     * @param rawCommand The command after it has been parsed
     *
     * @return An ArrayList of the tokens
     *
     * @exception IOException If an error occurs
     */
    
    public static ArrayList<String> tokenSplit(String rawCommand)
        throws IOException {
        String spaceRegex = "\"([^\"]*)\"|'([^']*)'|[^\\s]+";
        ArrayList<String> tokens = new ArrayList<String>();
        Pattern regex = Pattern.compile(spaceRegex);
        Matcher regexMatcher = regex.matcher(rawCommand);
        String nonQuote;
        boolean previousQuoted = false;

        while (regexMatcher.find()) {
            if (
                regexMatcher.group(1) != null || regexMatcher.group(2) != null
            ) {
                String quoted = regexMatcher.group(0).trim();
                previousQuoted = true;
                tokens.add(quoted.substring(1, quoted.length() - 1));
            } else {
                nonQuote = regexMatcher.group().trim();
                if (previousQuoted && nonQuote.contains("\"")) {
                    String last = "";
                    last = tokens.remove(tokens.size() - 1);
                    nonQuote = last + nonQuote.replaceAll("\"", "");
                }
                previousQuoted = false;
                ArrayList<String> globbingResult = new ArrayList<String>();
                Path dir = Paths.get(currentDirectory);

                String tempDir = currentDirectory;
                String relativeDir = "";
                if (
                    nonQuote.indexOf("*") != -1 && !(nonQuote.charAt(0) == '*')
                ) {
                    relativeDir = nonQuote.substring(0, nonQuote.indexOf("*"));
                    tempDir = tempDir.concat("/");
                    tempDir = tempDir.concat(relativeDir);
                    dir = Paths.get(tempDir);
                    nonQuote =nonQuote.substring(nonQuote.indexOf("*"), nonQuote.length());
                }

                DirectoryStream<Path> stream = Files.newDirectoryStream(dir, nonQuote);

                for (Path entry : stream) {
                    if (tempDir.equals(currentDirectory)) {
                        globbingResult.add(entry.getFileName().toString());
                    } else {
                        globbingResult.add(
                            relativeDir + entry.getFileName().toString()
                        );
                    }
                }
                if (globbingResult.isEmpty()) {
                    globbingResult.add(nonQuote.replaceAll("\"", ""));
                }
                tokens.addAll(globbingResult);
            }
        }
        return tokens;
    }

    /**
     * This method takes in the command line input from a user and returns
     * an ExeceptionPlan (result of walking through the command with visitor)
     * which splits the command up to be processed by JSH eval
     *
     * @param cmdline The command line input from the user
     *
     * @return An ExecutionPlan which is the result of visiting the tree from input
     *
     */
    public static ExecutionPlan parse(String cmdline) {
        CharStream parserInput = CharStreams.fromString(cmdline);
        JshGrammarLexer lexer = new JshGrammarLexer(parserInput);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        JshGrammarParser parser = new JshGrammarParser(tokenStream);
        ParseTree tree = parser.command();
        CommandVisitor visitor = new CommandVisitor();
        return visitor.visit(tree);
    }

    /**
     * This method creates a that doesn't exist for when we redirect to
     * a file so any output can be written to the file.
     * 
     *
     * @param filePath The filePath of where we want to create a file
     *
     * @return The desired File after creating it.
     *
     */

    private static File createRedirectFile(String filePath) {
        File returnFile;
        char slash = '/';
        if (filePath.charAt(0) == slash) {
            returnFile = new File(filePath);
        } else {
            returnFile = new File(currentDirectory + "/" + filePath);
        }
        return returnFile;
    }

    private static void emptyFile(File file) throws IOException {
        if (file.exists()) {
            PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
            writer.print("");
            writer.close();
        } else {
            file.getParentFile().mkdir();
            file.createNewFile();
        }
    }

    private static String readInputStream(
        InputStream input,
        OutputStream output
    )
        throws IOException {
        if (input != null && input.available() != 0) {
            final int bufferSize = 1024 * 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder pipeStr = new StringBuilder();
            Reader rdr = new InputStreamReader(input, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
                pipeStr.insert(0, buffer, 0, charsRead);
            }
            String inputText = pipeStr.toString();
            output.write(inputText.getBytes(StandardCharsets.UTF_8));
            inputText = inputText.replace("\n", "");
            return inputText;
        }
        return "";
    }

    private static File redirectFrom(Queue<String> commands)
        throws IOException {
        String filePath;
        File readFile;
        do {
            commands.poll();
            filePath = commands.poll().trim();
            readFile = createRedirectFile(filePath);
            if (!readFile.exists()) {
                throw new IOException("File " + readFile.getName() + " Does not exist.");
            }
        } while (StringUtils.equals(commands.peek(), ConnectionType.REDIRECT_FROM.toString()));
        return readFile;
    }

    private static File redirectTo(Queue<String> commands) throws IOException {
        String filePath;
        File writeFile;
        do {
            commands.poll();
            filePath = commands.poll().trim();
            writeFile = createRedirectFile(filePath);
            emptyFile(writeFile);
        } while (StringUtils.equals(commands.peek(), ConnectionType.REDIRECT_TO.toString()));

        while (StringUtils.equals(commands.peek(), ConnectionType.REDIRECT_FROM.toString()) ||
               StringUtils.equals(commands.peek(), ConnectionType.REDIRECT_TO.toString())) {
            if (StringUtils.equals(commands.peek(), ConnectionType.REDIRECT_FROM.toString())) {
                commands.poll();
                commands.poll();
            } else if (
                StringUtils.equals(commands.peek(), ConnectionType.REDIRECT_TO.toString())
            ) {
                commands.poll();
                filePath = commands.poll().trim();
                emptyFile(createRedirectFile(filePath));
            }
        }
        return writeFile;
    }

    private static String createSubstitutionCommand(Queue<String> commands, String subCommand, boolean appSub) {
        String command = commands.poll();
        if (subCommand.charAt(0) == '\'') {
            subCommand = "\"" + subCommand + "\"";
        }
        if (appSub) {
            command = subCommand + command;
        } else if (command.stripLeading().substring(0, 4).equals("echo")) {
            command = command.stripLeading();
            if (
                commands.peek() != null &&
                !ConnectionType.connectionExists(commands.peek())
            ) {
                command = command + subCommand + commands.poll();
            } else {
                command = command.stripLeading() + subCommand;
            }
        }
        return command;
    }
    /**
     * This method takes in the current executor service and shuts it down.
     * It waits for all the previous threads to finish terminating as unsafe
     * applications shouldn't effect any other command in the execution chain.
     *
     * @param executor The executor service that is running multiple threads.
     *
     * @return A boolean to either break from the while loop or create a new 
     * newCachedThreadPool
     * 
     */

    private static boolean executorShutdown(ExecutorService executor) {
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!quitCommand) {
            return false;
        } else {
            quitCommand = false;
            return true;
        }
    }

    /**
     * This method takes in the command line input from the user, parses
     * it and runs any of the necessary applications.
     *
     *
     * @param cmdline The command line input from the user of which command
     * they wish to run.
     *
     * @exception IOException If an error occurs
     */
    
    public static void eval(String cmdline) throws IOException {
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
        boolean appSub = false;

        while (!commands.isEmpty()) {
            InputStream input = lastInput;
            OutputStream output = System.out;
            if (substitution) {
                if (subCommand.trim().length() > 0) {
                    subCommand += " ";
                }
                subCommand += readInputStream(subInput, wholeSubOutput);
                pipedInput = new PipedInputStream();
                subOutput = new PipedOutputStream(pipedInput);
                subInput = pipedInput;
                output = subOutput;
            }

            String command = commands.poll();
            if (command.equals("appsub")) {
                appSub = true;
                continue;
            }

            if (ConnectionType.connectionExists(command)) {
                if (command.equals(ConnectionType.SUBSTITUTION.toString())) {
                    if (!substitution) {
                        substitution = true;
                        pipedInput = new PipedInputStream();
                        wholeSubOutput = new PipedOutputStream(pipedInput);
                        wholeSubInput = pipedInput;
                        continue;
                    } else {
                        substitution = false;
                        command = commands.peek();
                        String updatedCommand = createSubstitutionCommand(commands, subCommand, appSub);
                        if (updatedCommand.equals(command)) {
                            input = wholeSubInput;
                        } else {
                            command = updatedCommand;
                        }
                        output.close();
                        wholeSubOutput.close();
                        output = System.out;
                    }
                }

                if (command.equals(ConnectionType.SEQUENCE.toString())) {
                    lastInput = null;
                    continue;
                }

                if (command.equals(ConnectionType.REDIRECT_TO.toString())) {
                    String[] redirectArray = commands.poll().trim().split(" ", 2);
                    command = redirectArray[1];
                    String filePath = redirectArray[0];
                    File writeFile = createRedirectFile(filePath);
                    emptyFile(writeFile);
                    output = new FileOutputStream(writeFile, true);
                }

                if (command.equals(ConnectionType.REDIRECT_FROM.toString())) {
                    String[] redirectArray = commands.poll().trim().split(" ", 2);
                    command = redirectArray[1];
                    String filePath = redirectArray[0];
                    File readFile = createRedirectFile(filePath);
                    if (!readFile.exists()) {
                        throw new IOException("File " + readFile.getName() + " Does not exist.");
                    }
                    input = new FileInputStream(readFile);
                }
            }

            if (ConnectionType.connectionExists(commands.peek())) {
                if (StringUtils.equals(commands.peek(), ConnectionType.REDIRECT_FROM.toString())) {
                    File readFile = redirectFrom(commands);
                    input = new FileInputStream(readFile);
                }

                if (StringUtils.equals(commands.peek(), ConnectionType.REDIRECT_TO.toString())) {
                    File writeFile = redirectTo(commands);
                    output = new FileOutputStream(writeFile, true);
                }

                if (StringUtils.equals(commands.peek(), ConnectionType.PIPE.toString())) {
                    pipedInput = new PipedInputStream();
                    output = new PipedOutputStream(pipedInput);
                    lastInput = pipedInput;
                    commands.poll();
                }
            }
            ArrayList<String> tokens = tokenSplit(command);
            String appName = tokens.get(0);
            ApplicationFactory.make(appName);
            executor.execute(new RunCommand(tokens, output, input));

            if (command.equals(ConnectionType.SEQUENCE.toString()) ||
                !ConnectionType.connectionExists(command)) {
                if (executorShutdown(executor)) {
                    break;
                } else {
                    executor = Executors.newCachedThreadPool();
                }
            }
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
            Scanner input = new Scanner(System.in, StandardCharsets.UTF_8);
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
