package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.nio.charset.StandardCharsets;
import java.util.List;

import uk.ac.ucl.jsh.Jsh;



public class Sort implements Application{

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if (appArgs.size() > 2) {
            throw new RuntimeException("sort: too many arguments");
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-r")) {
            throw new RuntimeException("sort: wrong argument " + appArgs.get(0));
        }

        boolean reversed = false;
        String sortArg = "";
        if (appArgs.size() == 2) { // -r tag and file path provided
            reversed = true;
            sortArg = appArgs.get(1);
        } else if (appArgs.size() == 1) { 
            if (appArgs.get(0).equals("-r")) { // Use InputSteam reversed
                reversed = true; 
            } else { // Use file path
                sortArg = appArgs.get(0);
            }
        }

        if (sortArg.isEmpty()) { // Take InputStream

            final int bufferSize = 1024 * 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder pipeStr = new StringBuilder();
            Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
                pipeStr.append(buffer, 0, charsRead);
            }
            String[] sortPipe = pipeStr.toString().split("\n");
            
            List<String> sortList = Arrays.asList(sortPipe);
            Collections.sort(sortList);

            if (reversed) {
                Collections.reverse(sortList);
            }

            sortList.forEach(line -> { 
                try {
                    writer.write(line);
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException("sort: unable to write");
                }
            });

        } else { // Use file path

            String sortFile = Jsh.currentDirectory + File.separator + sortArg;

            try (Stream<String> stream = Files.lines(Paths.get(sortFile))) {
                if (!reversed) {
                    stream.sorted().forEach(line -> { 
                        try {
                            writer.write(line);
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        } catch (IOException e) {
                            throw new RuntimeException("sort: cannot open " + appArgs.get(0));
                        }
                    });
                } else {
                    stream.sorted(Comparator.reverseOrder()).forEach(line -> { 
                        try {
                            writer.write(line);
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        } catch (IOException e)  {
                            throw new RuntimeException("sort: unchecked exception " + appArgs.get(0));
                        }
                    });              
                }
            } catch (IOException e) {
                throw new RuntimeException("sort: cannot open " + appArgs.get(0));
            }

        }
    }

}