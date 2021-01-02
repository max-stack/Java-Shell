package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ucl.jsh.Jsh;

public class Cut implements Application {
    
    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if (appArgs.isEmpty()) {
            throw new RuntimeException("cut: missing arguments");
        }
        if (appArgs.size() != 3 && appArgs.size() != 2) {
            throw new RuntimeException("cut: wrong arguments");
        }
        if (!appArgs.get(0).equals("-b")) {
            throw new RuntimeException("cut: wrong argument " + appArgs.get(0));
        }

        String[] cutRanges = appArgs.get(1).split("[,]+");
        ArrayList<Integer> indexes = new ArrayList<Integer>();

        if (cutRanges.length == 0) {
            throw new RuntimeException("cut: wrong argument " + appArgs.get(1));
        }
        
        // Get boundary values first (e.g. -4, 8-)
        Integer startBoundary = -1;
        Integer endBoundary = Integer.MAX_VALUE;

        for (String range : cutRanges) {
            Boolean valid = range.matches("^[0-9]*[-]?[0-9]*");
            if (!valid || range.isEmpty()) {
                throw new RuntimeException("cut: wrong argument " + appArgs.get(1));
            } else {
                int value;
                if (range.endsWith("-")) {
                    value = Integer.parseInt(range.replace("-", "")) - 1;
                    if (value < endBoundary) { endBoundary = value; }
                } else if (range.startsWith("-")) {
                    value = Integer.parseInt(range.replace("-", ""));
                    if (value > startBoundary) { startBoundary = value; }
                }
            }
        }

        // Get individual and range values
        for (String range : cutRanges) {
            int l_value = 0;
            int r_value = 0;
            if (range.matches("^[0-9]+[-][0-9]+")) {
                String[] indexParts = range.split("-");
                l_value = Integer.parseInt(indexParts[0]) - 1;
                r_value = Integer.parseInt(indexParts[1]);
                if (l_value > r_value) {
                    throw new RuntimeException("cut: wrong argument " + appArgs.get(1));
                }
            } else if (!range.contains("-")) {
                l_value = Integer.parseInt(range) - 1;
                r_value = l_value + 1;
            }
            for (int num = l_value; num < r_value; num++) {
                if (!indexes.contains(num) && num > startBoundary && num < endBoundary) { indexes.add(num); }
            }
        }

        Collections.sort(indexes);
        
        if (appArgs.size() == 2) { // Take InputStream

            final int bufferSize = 1024 * 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder pipeStr = new StringBuilder();
            Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
                pipeStr.append(buffer, 0, charsRead);
            }
            String[] cutPipe = pipeStr.toString().split("\n");

            for (String line : cutPipe) {
                int start = Math.min(startBoundary, line.length());
                int end = Math.min(endBoundary, line.length());

                if (start != -1) { writer.write(line.substring(0, start)); }
                
                for (Integer index : indexes) {
                    if (index >= line.length()) {
                        writer.write("");
                    } else {
                        writer.write(line.charAt(index));
                    }
                }
                
                if (end != Integer.MAX_VALUE) { writer.write(line.substring(end)); }

                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }

        } else if (appArgs.size() == 3) { // Use file path
            String cutArg = appArgs.get(2);

            File cutFile = new File(Jsh.currentDirectory + File.separator + cutArg);
            if (cutFile.exists()) {
                Charset encoding = StandardCharsets.UTF_8;
                Path filePath = Paths.get((String) Jsh.currentDirectory + File.separator + cutArg);
                try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        int start = Math.min(startBoundary, line.length());
                        int end = Math.min(endBoundary, line.length());

                        if (start != -1) { writer.write(line.substring(0, start)); }
                        
                        for (Integer index : indexes) {
                            if (index >= line.length()) {
                                writer.write("");
                            } else {
                                writer.write(line.charAt(index));
                            }
                        }
                        
                        if (end != Integer.MAX_VALUE) { writer.write(line.substring(end)); }
        
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                    
                } catch (IOException e) {
                    throw new RuntimeException("cut: cannot open " + cutArg);
                }
            } else {
                throw new RuntimeException("cut: " + cutArg + " does not exist");
            }

        }
    }
}