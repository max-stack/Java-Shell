package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Echo implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(out);
        boolean atLeastOnePrinted = false;
        for (String arg : appArgs) {
            writer.write(arg);
            writer.write(" ");
            writer.flush();
            atLeastOnePrinted = true;
        }
        if (atLeastOnePrinted) {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    
    }

}