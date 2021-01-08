package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import uk.ac.ucl.jsh.app.Application;
import uk.ac.ucl.jsh.app.ApplicationFactory;

public class RunCommand implements Runnable {

    ArrayList<String> tokens;
    OutputStream out;
    InputStream in;

    public RunCommand(ArrayList<String> tokens, OutputStream out, InputStream in) {
        this.tokens = tokens;
        this.out = out;
        this.in = in;
    }

    /**
     * This method is ran when a new thread is added to the executor service.
     * The thread will then begin to run the Application. The PipedOutputStream
     * is closed here to prevent any broken pipe errors.
     */
    
    public void run() {
        try {
            String appName = tokens.get(0);
            ArrayList<String> appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));
            Application app = ApplicationFactory.make(appName);
            app.exec(appArgs, in, out);
            if (out.getClass().getName().toString() == "java.io.PipedOutputStream") {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
