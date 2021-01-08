package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Unsafe implements ErrorOutput {

    /**
     * This method keeps a hold of any error messages that occur and 
     * writes them to the output stream.
     *
     * @param out The output stream to write any error messages that are caught.
     * @param message The error message itself.
     *
     *
     * @exception IOException If an error occurs
     */
    
    public void output(OutputStream out, String message) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

        writer.write(message);
        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }
}