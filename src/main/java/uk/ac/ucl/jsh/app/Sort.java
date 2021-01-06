package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.nio.charset.StandardCharsets;
import java.util.List;

import uk.ac.ucl.jsh.Jsh;
import uk.ac.ucl.jsh.app.HelperMethods;



public class Sort implements Application{

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        if(Thread.currentThread().isInterrupted()){
            return;
        }

        if (appArgs.size() > 2) {
            HelperMethods.outputError(unsafe, out, "sort: too many arguments"); return;
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-r")) {
            HelperMethods.outputError(unsafe, out, "sort: wrong argument " + appArgs.get(0)); return;
        }

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

                    /* Not sure why outputError doesn't work without the try-catch block */
                    try {
                        //throw new RuntimeException("sort: unable to write");
                        HelperMethods.outputError(unsafe, out, "sort: unable to write"); return;
                    } catch (Exception f) {
                        throw new RuntimeException("sort: unexpected error 1 - " + f);
                    }
                    
                }
            });

        } else { // Use file path

            String sortFile = Jsh.currentDirectory + File.separator + sortArg;

            try (Stream<String> stream = Files.lines(Paths.get(sortFile))) {
                if (!reversed) {
                    stream.sorted().forEach(line -> { 
                        try {
                            writer.write(line);
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        } catch (IOException e) {

                            /* Not sure why outputError doesn't work without the try-catch block */
                            try {
                                //throw new RuntimeException("sort: cannot open " + appArgs.get(0));
                                HelperMethods.outputError(unsafe, out, "sort: cannot open " + appArgs.get(0)); return;
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

                            /* Not sure why outputError doesn't work without the try-catch block */
                            try {
                                //throw new RuntimeException("sort: unchecked exception " + appArgs.get(0));
                                HelperMethods.outputError(unsafe, out, "sort: unchecked exception " + appArgs.get(0)); return;
                            } catch (Exception f) {
                                throw new RuntimeException("sort: unexpected error 3 - " + f);
                            }

                        }
                    });              
                }
            } catch (IOException e) {
                HelperMethods.outputError(unsafe, out, "sort: cannot open " + appArgs.get(0)); return;
            }

        }
    }

}