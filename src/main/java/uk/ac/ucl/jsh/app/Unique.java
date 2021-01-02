package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import uk.ac.ucl.jsh.Jsh;
import uk.ac.ucl.jsh.app.HelperMethods;

public class Unique implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if (appArgs.size() > 2) {
            HelperMethods.outputError(unsafe, out, "uniq: too many arguments"); return;
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-i")) {
            HelperMethods.outputError(unsafe, out, "uniq: wrong argument " + appArgs.get(0)); return;
        }

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

        String previousLine = "";

        if (uniqArg.isEmpty()) { // Take InputStream
            String adjustedLine = null;

            String[] pipeInput = HelperMethods.readInputStream(in);
            for (String line : pipeInput) {
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

        } else { // Use file path

            String uniqFile = Jsh.currentDirectory + File.separator + uniqArg;
            String input = null;
            String adjustedInput = null;

            Scanner sc = new Scanner(new File(uniqFile));

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

        }
    }
   
}