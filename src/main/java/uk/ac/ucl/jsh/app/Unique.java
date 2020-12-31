package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import uk.ac.ucl.jsh.Jsh;

public class Unique implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(out);

        /*
        if (appArgs.isEmpty()) {
            throw new RuntimeException("uniq: missing arguments");
        }
        if (appArgs.size() != 1 && appArgs.size() != 2) {
            throw new RuntimeException("uniq: wrong arguments");
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-i")) {
            throw new RuntimeException("uniq: wrong argument " + appArgs.get(0));
        }
        */

        boolean caseSensitive = true; 
        String uniqArg = "";
        if (appArgs.size() == 2) { // -i tag and file path provided
            caseSensitive = false;
            uniqArg = appArgs.get(1);
        } else if (appArgs.size() == 1) { // -i tag OR file path provided
            if (appArgs.get(0).equals("-i")) { // Use pipedInput
                caseSensitive = false;
            } else { // Use file path
                uniqArg = appArgs.get(0);
            }
        } else if (appArgs.isEmpty()) { // Case sensitive and use pipedInput
            // Do nothing
        }

        String previousLine = "";

        if (uniqArg.isEmpty()) { // Take pipedInputStream
            final int bufferSize = 1024 * 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder pipeStr = new StringBuilder();
            Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
                pipeStr.append(buffer, 0, charsRead);
            }
            String[] uniqPipe = pipeStr.toString().split("\n");
            
            for (String line : uniqPipe) {
                if (caseSensitive) {

                    if (!line.equals(previousLine)) {
                        writer.append(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                        previousLine = line;
                    }
    
                } else {
    
                    if (!(line.toLowerCase()).equals(previousLine.toLowerCase())) {
                        writer.append(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                        previousLine = line;
                    }
                }
            }

        } else {

            String uniqFile = Jsh.currentDirectory + File.separator + uniqArg;
            String input = null;

            Scanner sc = new Scanner(new File(uniqFile));

            while (sc.hasNextLine()) {

                input = sc.nextLine();
                if(caseSensitive){

                    if( !input.equals(previousLine)) {
                        writer.append(input);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                        previousLine = input;
                    }

                } else{

                    if( !(input.toLowerCase()).equals(previousLine.toLowerCase()) ) {
                        writer.append(input);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                        previousLine = input;
                    }
                }
            }

        }
    }
   
}
