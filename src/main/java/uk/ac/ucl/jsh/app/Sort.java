package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        /*
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
        */

        boolean reversed = false;
        String sortArg = "";
        
        //appArgs = -r , sort.txt
        //appArgs = sort.txt
        //appArgs = -r

        if (appArgs.size() == 2){// execute without stream
            reversed = true;
            sortArg = appArgs.get(1);
        } else if (appArgs.size() == 1){ 
            if (appArgs.get(0).equals("-r")){ //use stream but reverse it
                reversed = true; 
            }
            else{ // else execute without stream and not reversed
                sortArg = appArgs.get(0);
            }
        }


        if (sortArg.isEmpty()){ // take inputstream
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

            if(reversed){
                Collections.reverse(sortList);
            }

            sortList.forEach(line -> { 
                try {
                    writer.write(line);
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                } 
                catch (IOException e)  {
                    throw new RuntimeException("sort: unable to write");
                }
            }
            );

        } else {// open file
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
    
}