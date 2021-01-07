package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import uk.ac.ucl.jsh.Jsh;
public class Unique implements Application {


    private boolean handleArguments(ArrayList<String> appArgs, OutputStream out, Boolean unsafe) throws IOException{
        if (appArgs.size() > 2) {
            HelperMethods.outputError(unsafe, out, "uniq: too many arguments"); return false;
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-i")) {
            HelperMethods.outputError(unsafe, out, "uniq: wrong argument " + appArgs.get(0)); return false;
        }
        return true;
    }

    private void handleInput(OutputStreamWriter writer, InputStream in,  boolean caseSensitive) throws IOException{
        String previousLine = "";
        String adjustedLine = "";
        String[] pipeInput = HelperMethods.readInputStream(in);
        
        for (String line : pipeInput) {
            adjustedLine = line;

            if (!caseSensitive) {
                adjustedLine = line.toLowerCase();
            }
            if (!adjustedLine.equals(previousLine)) {
                writer.append(line);
                writer.write(System.getProperty("line.separator"));
                writer.flush();
                previousLine = adjustedLine;
            }
        }
    }

    private boolean handleOutput(OutputStreamWriter writer, OutputStream out, Boolean unsafe,  boolean caseSensitive, String uniqArg) throws IOException{
        String previousLine = "";
        String uniqFile = Jsh.currentDirectory + File.separator + uniqArg;
        String input = null;
        String adjustedInput = null;
        Scanner sc;

        try {
            sc = new Scanner(new File(uniqFile));
        } catch (Exception e) {
            HelperMethods.outputError(unsafe, out, "uniq: wrong file argument"); return false;
        }

        while (sc.hasNextLine()) {
            input = sc.nextLine();
            adjustedInput = input;
            if (!caseSensitive) {
                adjustedInput = input.toLowerCase();
            }
            if (!adjustedInput.equals(previousLine)) {
                writer.append(input);
                writer.write(System.getProperty("line.separator"));
                writer.flush();
                previousLine = adjustedInput;
            }
        }
        return true;
    }

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        boolean successfullyPassed = handleArguments(appArgs, out, unsafe);
        if(!successfullyPassed) {return; }

        boolean caseSensitive = true; 
        String uniqArg = "";
        if (appArgs.size() == 2) { // -i tag AND file path provided
            caseSensitive = false;
            uniqArg = appArgs.get(1);
        } else if (appArgs.size() == 1) {
            if (appArgs.get(0).equals("-i")) { // Use InputStream
                caseSensitive = false;
            } else { // Use file path
                uniqArg = appArgs.get(0);
            }
        }

        

        if (uniqArg.isEmpty()) { // Take InputStream
            handleInput(writer, in, caseSensitive);

        } else {
            successfullyPassed = handleOutput(writer, out, unsafe, caseSensitive, uniqArg);
            if(!successfullyPassed) {return; }

        }
        
    }
   
}