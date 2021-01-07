package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import uk.ac.ucl.jsh.Jsh;

public class Safe implements ErrorOutput {
    public void output(OutputStream out, String message) throws IOException {
        Jsh.quitCommand = true;
        System.err.println(message);
    }
}