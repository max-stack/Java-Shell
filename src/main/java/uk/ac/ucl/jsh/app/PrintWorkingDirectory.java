package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Jsh;

public class PrintWorkingDirectory implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if(Thread.currentThread().isInterrupted()){
            return;
        }

        writer.write(Jsh.currentDirectory);
        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }

}