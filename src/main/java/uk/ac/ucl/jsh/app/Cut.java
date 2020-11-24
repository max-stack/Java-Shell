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
import java.util.Arrays;

import uk.ac.ucl.jsh.Jsh;

/* WORK IN PROGESS */

public class Cut implements Application {
    
    public void exec(ArrayList<String> appArgs, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        if (appArgs.isEmpty()) {
           throw new RuntimeException("cut: missing arguments");
        } else if (!appArgs.get(0).equals("-b")) {
            throw new RuntimeException("cut: wrong argument " + appArgs.get(0));
        } else if (appArgs.size() > 3) {
           throw new RuntimeException("cut: too many arguments");
        }

        String cutArg = appArgs.get(1);        
        ArrayList<String> cutBytes = new ArrayList<String>(Arrays.asList(cutArg.split(",")));

        // Convert to indexes:
        ArrayList<ArrayList<Integer>> cutByteIndexes = new ArrayList<ArrayList<Integer>>();
        for (String cutByte : cutBytes) {
            int startIndex = 0;
            int endIndex = cutByte.length() - 1;
            if (cutByte.startsWith("-")) {
                endIndex = Integer.parseInt(cutByte.substring(1));
            } else if (cutByte.endsWith("-")) {
                startIndex = Integer.parseInt(cutByte.substring(0, cutByte.length() - 1)) - 1;
            } else {
               ArrayList<String> temp = new ArrayList<String>(Arrays.asList(cutByte.split("-")));
                if (temp.size() > 2) {
                    throw new RuntimeException("cut: wrong argument " + cutByte);
                } else {
                    // n and n-n cases
                    startIndex = Integer.parseInt(temp.get(0));
                    endIndex = Integer.parseInt(temp.get(temp.size() - 1));
                    if (startIndex == endIndex) {
                        endIndex++;
                    }
                }
            }
            cutByteIndexes.add(new ArrayList<Integer>(Arrays.asList(startIndex, endIndex)));
        }

        cutArg = appArgs.get(2);
        File cutFile = new File(Jsh.currentDirectory + File.separator + cutArg);
        if (cutFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) Jsh.currentDirectory + File.separator + cutArg);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    for (ArrayList<Integer> indexes : cutByteIndexes) {
                        try {
                            writer.write(line.substring(indexes.get(0), indexes.get(1)));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            writer.write(line.substring(indexes.get(0)));
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