package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Echo implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if(Thread.currentThread().isInterrupted()){
            return;
        }

        if (appArgs.isEmpty()) {
            HelperMethods.outputError(unsafe, out, "echo: missing arguments"); return;
        }

        for(int i = 0; i < appArgs.size() - 1; i++){
            writer.write(appArgs.get(i));
            writer.write(" ");
            writer.flush();
        }
        writer.write(appArgs.get(appArgs.size() - 1));
        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }

}