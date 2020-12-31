package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ucl.jsh.Jsh;

public class GlobalRegExPrint implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(out);

        /*
        if (appArgs.size() < 2) {
            throw new RuntimeException("grep: wrong number of arguments");
        }
        */

        Pattern grepPattern = Pattern.compile(appArgs.get(0));
        int numOfFiles = appArgs.size() - 1;
        if (numOfFiles == 0) {

            final int bufferSize = 1024 * 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder pipeStr = new StringBuilder();
            Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
            pipeStr.append(buffer, 0, charsRead);
            }
            String[] grepPipe = pipeStr.toString().split("\n");

            for (String line : grepPipe) {
                Matcher matcher = grepPattern.matcher(line);
                if (matcher.find()) {
                    writer.write(line);
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
            }

        } else {

            Path filePath;
            Path[] filePathArray = new Path[numOfFiles];
            Path currentDir = Paths.get(Jsh.currentDirectory);
            for (int i = 0; i < numOfFiles; i++) {
                filePath = currentDir.resolve(appArgs.get(i + 1));
                if (Files.notExists(filePath) || Files.isDirectory(filePath) || 
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("grep: wrong file argument");
                }
                filePathArray[i] = filePath;
            }

            for (int j = 0; j < filePathArray.length; j++) {
                Charset encoding = StandardCharsets.UTF_8;
                try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = grepPattern.matcher(line);
                        if (matcher.find()) {
                            if (numOfFiles > 1) {
                                writer.write(appArgs.get(j+1));
                                writer.write(":");
                            }
                            writer.write(line);
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("grep: cannot open " + appArgs.get(j + 1));
                }
            }

        }
    }

}