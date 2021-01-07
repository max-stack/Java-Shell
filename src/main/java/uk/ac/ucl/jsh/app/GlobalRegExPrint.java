package uk.ac.ucl.jsh.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import uk.ac.ucl.jsh.Jsh;

public class GlobalRegExPrint implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        boolean successfullyPassed = handleArguments(appArgs, out, unsafe);
        if (!successfullyPassed) { return; }

        Pattern grepPattern = Pattern.compile(appArgs.get(0));
        
        int numOfFiles = appArgs.size() - 1;
        if (numOfFiles == 0) { // Take InputStream
            handleInput(writer, in, grepPattern);
        } else { // Use file path(s)
            handleOutput(out, appArgs, unsafe, grepPattern);
        }
    }


    private boolean handleArguments(ArrayList<String> appArgs, OutputStream out, Boolean unsafe) throws IOException {
        if (appArgs.isEmpty()) {
            HelperMethods.outputError(unsafe, out, "grep: missing arguments"); return false;
        }
        try { Pattern.compile(appArgs.get(0)); }
        catch (PatternSyntaxException e) {
            HelperMethods.outputError(unsafe, out, "grep: wrong pattern syntax " + appArgs.get(0)); return false;
        }
        return true;
    }


    private void handleInput(OutputStreamWriter writer, InputStream in, Pattern grepPattern) throws IOException {
        String[] pipeInput = HelperMethods.readInputStream(in);
        for (String line : pipeInput) { grepFromLine(writer, 0, null, line, grepPattern); }
    }


    private void handleOutput(OutputStream out, ArrayList<String> appArgs, boolean unsafe, Pattern grepPattern) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        int numOfFiles = appArgs.size() - 1;
        Path filePath;
        Path[] filePathArray = new Path[numOfFiles];
        Path currentDir = Paths.get(Jsh.currentDirectory);
        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(appArgs.get(i + 1));
            if (Files.isDirectory(filePath) || !Files.isReadable(filePath)) {
                HelperMethods.outputError(unsafe, out, "grep: wrong file argument"); writer.close(); return;
            }
            filePathArray[i] = filePath;
        }

        for (int j = 0; j < filePathArray.length; j++) {
            String currentFile = appArgs.get(j+1);
            Charset encoding = StandardCharsets.UTF_8;
            try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) { grepFromLine(writer, numOfFiles, currentFile, line, grepPattern); }
            } catch (IOException e) {
                HelperMethods.outputError(unsafe, out, "grep: cannot open " + appArgs.get(j + 1)); return;
            }
        }
    }


    private void grepFromLine(OutputStreamWriter writer, int numOfFiles, String file, String line, Pattern grepPattern) throws IOException {
        Matcher matcher = grepPattern.matcher(line);
        if (matcher.find()) {
            if (numOfFiles > 1) { writer.write(file + ":"); }
            writer.write(line);
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

}