package uk.ac.ucl.jsh.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Jsh;

public class Head implements Application {

    public void exec(ArrayList<String> appArgs, OutputStream out) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(out);
        if (appArgs.isEmpty()) {
            throw new RuntimeException("head: missing arguments");
        }
        if (appArgs.size() != 1 && appArgs.size() != 3) {
            throw new RuntimeException("head: wrong arguments");
        }
        if (appArgs.size() == 3 && !appArgs.get(0).equals("-n")) {
            throw new RuntimeException("head: wrong argument " + appArgs.get(0));
        }
        int headLines = 10;
        String headArg;
        if (appArgs.size() == 3) {
            try {
                headLines = Integer.parseInt(appArgs.get(1));
            } catch (Exception e) {
                throw new RuntimeException("head: wrong argument " + appArgs.get(1));
            }
            headArg = appArgs.get(2);
        } else {
            headArg = appArgs.get(0);
        }
        File headFile = new File(Jsh.currentDirectory + File.separator + headArg);
        if (headFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) Jsh.currentDirectory + File.separator + headArg);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                for (int i = 0; i < headLines; i++) {
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("head: cannot open " + headArg);
            }
        } else {
            throw new RuntimeException("head: " + headArg + " does not exist");
        }
    
    }

}