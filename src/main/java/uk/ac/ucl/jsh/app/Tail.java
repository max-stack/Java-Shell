package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Jsh;
import uk.ac.ucl.jsh.app.HelperMethods;

public class Tail implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if(Thread.currentThread().isInterrupted()){
            return;
        }
        
        if (appArgs.size() > 3) {
            HelperMethods.outputError(unsafe, out, "tail: too many arguments"); return;
        }
        if (appArgs.size() > 1 && !appArgs.get(0).equals("-n")) {
            HelperMethods.outputError(unsafe, out, "tail: wrong argument " + appArgs.get(0)); return;
        }        

        int tailLines = 10;
        String tailArg = "";
        if (appArgs.size() == 3) { // Number of lines and file path provided
            tailArg = appArgs.get(2);
        } else if (appArgs.size() == 1) { // File path provided (use default number of lines: 10)
            tailArg = appArgs.get(0);
        }

        if (appArgs.size() > 1) {
            try {
                tailLines = Integer.parseInt(appArgs.get(1));
            } catch (NumberFormatException e) {
                HelperMethods.outputError(unsafe, out, "tail: wrong number " + appArgs.get(1)); return;
            }
        }

        if (tailArg.isEmpty()) { // Take InputStream
            
            String[] pipeInput = HelperMethods.readInputStream(in);
            int index;
            if (tailLines > pipeInput.length) {
                index = 0;
            } else {
                index = pipeInput.length - tailLines;
            }
            for (int i = index; i < pipeInput.length; i++) {
                try {
                    writer.write(pipeInput[i]);
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                } catch (Exception e) {
                    break;
                }
            }

        } else { // Use file path

            File tailFile = new File(Jsh.currentDirectory + File.separator + tailArg);
            if (tailFile.exists()) {
                Charset encoding = StandardCharsets.UTF_8;
                Path filePath = Paths.get((String) Jsh.currentDirectory + File.separator + tailArg);
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
                    HelperMethods.outputError(unsafe, out, "tail: cannot open " + tailArg); return;
                }
            } else {
                HelperMethods.outputError(unsafe, out, "tail: " + tailArg + " does not exist"); return;
            }
            
        }
    }

}