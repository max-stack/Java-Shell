package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Echo implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        boolean atLeastOnePrinted = false;
        for(int i = 0; i < appArgs.size() - 1; i++){
            writer.write(appArgs.get(i));
            writer.write(" ");
            writer.flush();
        }
        writer.write(appArgs.get(appArgs.size() - 1));
        writer.flush();
        atLeastOnePrinted = true;

        if (atLeastOnePrinted) {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

}