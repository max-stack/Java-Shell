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

public class Head implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        if (appArgs.size() > 3) {
            throw new RuntimeException("head: too many arguments");
        }
        if (appArgs.size() > 1 && !appArgs.get(0).equals("-n")) {
            throw new RuntimeException("head: wrong argument " + appArgs.get(0));
        }        

        int headLines = 10;
        String headArg = "";
        if (appArgs.size() == 3) { // Number of lines and file path provided
            headArg = appArgs.get(2);
        } else if (appArgs.size() == 1) { // File path provided (use default number of lines: 10)
            headArg = appArgs.get(0);
        }

        if (appArgs.size() > 1) {
            try {
                headLines = Integer.parseInt(appArgs.get(1));
            } catch (NumberFormatException e) {
                throw new RuntimeException("head: wrong number " + appArgs.get(1));
            }
        }

        if (headArg.isEmpty()) { // Take InputStream
            
            final int bufferSize = 1024 * 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder pipeStr = new StringBuilder();
            Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
                pipeStr.append(buffer, 0, charsRead);
            }
            String[] headPipe = pipeStr.toString().split("\n");

            for (int i = 0; i < headLines; i++) {
                try {
                    writer.write(headPipe[i]);
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                } catch (Exception e) {
                    break;
                }
            }

        } else { // Use file path

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

}