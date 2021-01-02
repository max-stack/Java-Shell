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
        ArrayList<int[]> indexTuples = new ArrayList<int[]>();

        if (cutRanges.length == 0) {
            throw new RuntimeException("cut: wrong argument " + appArgs.get(1));
        }

        /*
        This for loop validates each range given and extracts start/end indexes from them
        It puts them in an ArrayList of int[] where each int[] holds the start and end indexes
        */
        ArrayList<int[]> startOverlaps = new ArrayList<int[]>();
        ArrayList<int[]> endOverlaps = new ArrayList<int[]>();
        int count = 0;

        for (String range : cutRanges) {
            Boolean valid = range.matches("^[0-9]*[-]?[0-9]*");
            if (!valid || range.isEmpty()) {
                throw new RuntimeException("cut: wrong argument " + appArgs.get(1));
            } else {
                int startIndex = 0;
                int endIndex = -1;

                if (range.endsWith("-")) {
                    startIndex = Integer.parseInt(range.replace("-", "")) - 1;
                    endOverlaps.add(new int[]{startIndex, count});
                } else if (range.startsWith("-")) {
                    endIndex = Integer.parseInt(range.replace("-", ""));
                    startOverlaps.add(new int[]{endIndex, count});
                } else if (range.contains("-")) {
                    String[] indexParts = range.split("-");
                    startIndex = Integer.parseInt(indexParts[0]) - 1;
                    endIndex = Integer.parseInt(indexParts[1]);
                } else {
                    startIndex = Integer.parseInt(range) - 1;
                    endIndex = startIndex + 1;
                }

                if (endIndex != -1 && startIndex > endIndex) {
                    throw new RuntimeException("cut: wrong argument " + appArgs.get(1));
                }

                indexTuples.add(new int[]{startIndex, endIndex});
                count++;
            }
        }
        

        /*/ Check for overlaps e.g. 3-,4-
        int boundary = -1;
        int indexToKeep = -1;
        for (int[] overlap : startOverlaps) {
            if (overlap[0] > boundary) {
                boundary = overlap[0];
                indexToKeep = overlap[1];
            }
        }
        for (int[] overlap : endOverlaps) {

        }*/
        
        if (appArgs.size() == 2) {

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
                for (int[] indexes : indexTuples) {
                    if (indexes[0] > line.length()) {
                        writer.write("");
                    } else if (indexes[1] == -1 || indexes[1] > line.length()) {
                        writer.write(line.substring(indexes[0]));
                    } else {
                        writer.write(line.substring(indexes[0], indexes[1]));
                    }
                }
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }

        } else if (appArgs.size() == 3) {
            String cutArg = appArgs.get(2);

            File cutFile = new File(Jsh.currentDirectory + File.separator + cutArg);
            if (cutFile.exists()) {
                Charset encoding = StandardCharsets.UTF_8;
                Path filePath = Paths.get((String) Jsh.currentDirectory + File.separator + cutArg);
                try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        for (int[] indexes : indexTuples) {
                            if (indexes[0] > line.length()) {
                                writer.write("");
                            } else if (indexes[1] == -1 || indexes[1] > line.length()) {
                                writer.write(line.substring(indexes[0]));
                            } else {
                                writer.write(line.substring(indexes[0], indexes[1]));
                            }
                        }
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