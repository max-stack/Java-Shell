package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import uk.ac.ucl.jsh.Jsh;

public class Sort implements Application {

    private ErrorOutput error;

    public Sort(ErrorOutput error) {
        this.error = error;
    }

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");

        if (!handleArguments(appArgs, out)) { return; }

        boolean reversed = false;
        String sortArg = "";
        if (appArgs.size() == 2) { // -r tag and file path provided
            reversed = true;
            sortArg = appArgs.get(1);
        } else if (appArgs.size() == 1) {
            if (appArgs.get(0).equals("-r")) { // Use InputSteam reversed
                reversed = true;
            } else { // Use file path
                sortArg = appArgs.get(0);
            }
        }

        if (sortArg.isEmpty()) { // Take InputStream
            if (!handleInput(writer, in, out, reversed)) { return; }

        } else { // Use file path
            if (!handleOutput(appArgs, writer, out, sortArg, reversed)) { return; }
        }
    }

    private boolean handleArguments(ArrayList<String> appArgs, OutputStream out) throws IOException{
        if (appArgs.size() > 2) {
            error.output(out, "sort: too many arguments"); return false;
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-r")) {
            error.output(out, "sort: wrong argument " + appArgs.get(0)); return false;
        }
        return true;
    }

    private boolean handleInput(OutputStreamWriter writer, InputStream in, OutputStream out, boolean reversed) throws IOException{
        String[] pipeInput = HelperMethods.readInputStream(in);
        List<String> sortList = Arrays.asList(pipeInput);
        Collections.sort(sortList);
        if (reversed) { Collections.reverse(sortList); }

        sortList.forEach(line -> { 
            try {
                writer.write(line);
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            } catch (IOException e) {

                /* Lambda function means the try-catch block is required for this error output */
                try {
                    error.output(out, "sort: unable to write"); return;
                } catch (Exception f) {
                    throw new RuntimeException("sort: unexpected error 1 - " + f);
                }

            }
        });
        return true;
    }

    private boolean handleOutput(ArrayList<String> appArgs, OutputStreamWriter writer, OutputStream out, String sortArg, boolean reversed) throws IOException{
        String sortFile = Jsh.currentDirectory + File.separator + sortArg;

        try (Stream<String> stream = Files.lines(Paths.get(sortFile))) {
            if (!reversed) {
                stream.sorted().forEach(line -> { 
                    try {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    } catch (IOException e) {

                        /* Lambda function means the try-catch block is required for this error output */
                        try {
                            error.output(out, "sort: cannot open " + appArgs.get(0)); return;
                        } catch (Exception f) {
                            throw new RuntimeException("sort: unexpected error 2 - " + f);
                        }

                    }
                });
            } else {
                stream.sorted(Comparator.reverseOrder()).forEach(line -> { 
                    try {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    } catch (IOException e)  {

                        /* Lambda function means the try-catch block is required for this error output */
                        try {
                            error.output(out, "sort: unchecked exception " + appArgs.get(0)); return;
                        } catch (Exception f) {
                            throw new RuntimeException("sort: unexpected error 3 - " + f);
                        }

                    }
                });              
            }
        } catch (IOException e) {
            error.output(out, "sort: cannot open " + appArgs.get(0)); return false;
        }
        return true;
    }
    
}
