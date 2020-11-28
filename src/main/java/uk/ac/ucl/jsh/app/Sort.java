package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import uk.ac.ucl.jsh.Jsh;



public class Sort implements Application{

    public void exec(ArrayList<String> appArgs, OutputStream out) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if (appArgs.isEmpty()) {
            throw new RuntimeException("sort: missing arguments");
        }
        if (appArgs.size() != 1 && appArgs.size() != 2) {
            throw new RuntimeException("sort: wrong arguments");
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-r")) {
            throw new RuntimeException("sort: wrong argument " + appArgs.get(0));
        }

        boolean reversed = false; 
        String sortArg = appArgs.get(0);

        if (appArgs.size() == 2){
            reversed = true;
            sortArg = appArgs.get(1);
        }

        String sortFile = Jsh.currentDirectory + File.separator + sortArg;

        try (Stream<String> stream = Files.lines(Paths.get(sortFile))) {
            if (!reversed){
            stream.sorted().forEach(line -> { 
                    try {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    } 
                    catch (IOException e)  {
                        throw new RuntimeException("sort: cannot open " + appArgs.get(0));
                    }
                }
                );
            } else {
             stream.sorted(Comparator.reverseOrder()).forEach(line -> { 
                    try {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    } 
                    catch (IOException e)  {
                        throw new RuntimeException("sort: unchecked exception " + appArgs.get(0));
                    }
                } 
             );              
            }
        } catch (IOException e) {
            throw new RuntimeException("sort: cannot open " + appArgs.get(0));
        }
    }
    
}