package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import uk.ac.ucl.jsh.Jsh;

public class Unique implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(out);
        if (appArgs.isEmpty()) {
            throw new RuntimeException("uniq: missing arguments");
        }
        if (appArgs.size() != 1 && appArgs.size() != 2) {
            throw new RuntimeException("uniq: wrong arguments");
        }
        if (appArgs.size() == 2 && !appArgs.get(0).equals("-i")) {
            throw new RuntimeException("uniq: wrong argument " + appArgs.get(0));
        }

        boolean caseSensitive = true; 
        String uniqArg = appArgs.get(0);

        if (appArgs.size() == 2){
            caseSensitive = false;
            uniqArg = appArgs.get(1);
        }

        String uniqFile = Jsh.currentDirectory + File.separator + uniqArg;
        String input = null;
        String previousLine = "";

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