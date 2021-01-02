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

public class Tail implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        if (appArgs.size() > 3) {
            throw new RuntimeException("head: too many arguments");
        }
        if (appArgs.size() > 1 && !appArgs.get(0).equals("-n")) {
            throw new RuntimeException("head: wrong argument " + appArgs.get(0));
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
                throw new RuntimeException("head: wrong number " + appArgs.get(1));
            }
        }

        if (tailArg.isEmpty()) { // Take InputStream

            final int bufferSize = 1024 * 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder pipeStr = new StringBuilder();
            Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
                pipeStr.insert(0,buffer, 0, charsRead);
            }
            String[] tailPipe = pipeStr.toString().split("\n");

            for (int i = 0; i < tailLines; i++) {
                try {
                    writer.write(tailPipe[i]);
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
                    throw new RuntimeException("tail: cannot open " + tailArg);
                }
            } else {
                throw new RuntimeException("tail: " + tailArg + " does not exist");
            }
            
        }
    }

}