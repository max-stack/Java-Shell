package uk.ac.ucl.jsh.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Jsh;

public class Tail implements Application {

    private boolean handleArguments(ArrayList<String> appArgs, OutputStream out, Boolean unsafe) throws IOException{
        if (appArgs.size() > 3) {
            HelperMethods.outputError(unsafe, out, "tail: too many arguments"); return false;
        }
        if (appArgs.size() > 1 && !appArgs.get(0).equals("-n")) {
            HelperMethods.outputError(unsafe, out, "tail: wrong argument " + appArgs.get(0)); return false;
        }  
        return true;
    }

    private void handleInput(OutputStreamWriter writer, InputStream in,  int tailLines) throws IOException{
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

    }

    private boolean handleOutput(OutputStreamWriter writer, OutputStream out, Boolean unsafe, int tailLines, String tailArg) throws IOException{
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
                HelperMethods.outputError(unsafe, out, "tail: cannot open " + tailArg); return false;
            }
        } else {
            HelperMethods.outputError(unsafe, out, "tail: " + tailArg + " does not exist"); return false;
        }
        return true;
    }

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        boolean successfullyPassed = handleArguments(appArgs, out, unsafe);
        if(!successfullyPassed) {return; }

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
            handleInput(writer, in, tailLines);

        } else { // Use file path
            successfullyPassed = handleOutput(writer, out, unsafe, tailLines, tailArg);
            if(!successfullyPassed) {return; }
            
            
        }
    }

}