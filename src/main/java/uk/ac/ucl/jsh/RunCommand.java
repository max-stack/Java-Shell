package uk.ac.ucl.jsh;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

import uk.ac.ucl.jsh.app.Application;
import uk.ac.ucl.jsh.app.ApplicationFactory;

public class RunCommand implements Runnable{
    ArrayList<String> tokens;
    OutputStream out;
    InputStream in;
    Boolean unsafe;

    public RunCommand(ArrayList<String> tokens, OutputStream out, InputStream in, Boolean unsafe) {
        this.tokens = tokens;
        this.out = out;
        this.in = in;
        this.unsafe = unsafe;
    }

    public void run() {
        try {
            String appName = tokens.get(0);
            ArrayList<String> appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));
            Application app = ApplicationFactory.make(appName);
            app.exec(appArgs, in, out, unsafe);
            if (out.getClass().getName().toString() == "java.io.PipedOutputStream") {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}