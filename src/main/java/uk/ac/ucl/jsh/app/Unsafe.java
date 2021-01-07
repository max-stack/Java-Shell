package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Unsafe implements ErrorOutput {
    public void output(OutputStream out, String message) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        writer.write(message);
        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }
}