package uk.ac.ucl.jsh.app;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.Arrays;

import uk.ac.ucl.jsh.Jsh;

public class Concatenate implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if(in != null && in.getClass().getName().toString() == "java.io.FileInputStream"){
            String[] pipeInput = HelperMethods.readInputStream(in);
            for(String line : pipeInput){
                writer.write(String.valueOf(line));
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
        }
        else{
            if (appArgs.isEmpty()) { // Take InputStream
                
                String[] pipeInput = HelperMethods.readInputStream(in);
                appArgs = new ArrayList<String>(Arrays.asList(pipeInput));
            }

            for (String arg : appArgs) {
                Charset encoding = StandardCharsets.UTF_8;
                File currFile = new File(Jsh.currentDirectory + File.separator + arg);
                if (currFile.exists()) {
                    Path filePath = Paths.get(Jsh.currentDirectory + File.separator + arg);
                    try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            writer.write(String.valueOf(line));
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }
                    } catch (IOException e) {
                        HelperMethods.outputError(unsafe, out, "cat: cannot open " + arg); return;
                    }
                } else {
                    HelperMethods.outputError(unsafe, out, "cat: " + arg + " does not exist"); return;
                }
            }
        }

    }

}