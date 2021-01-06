package uk.ac.ucl.jsh.app;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

import uk.ac.ucl.jsh.Jsh;

public class HelperMethods {

    public static String[] readInputStream(InputStream in) throws IOException {
        final int bufferSize = 1024 * 1024; // buffer size constant

        final char[] buffer = new char[bufferSize];
        final StringBuilder pipeStr = new StringBuilder();
        Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
        int charsRead;
        while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
            pipeStr.append(buffer, 0, charsRead);
        }

        return pipeStr.toString().split("\n");
    }

    public static void outputError(Boolean unsafe, OutputStream out, String message) throws IOException {
        if (unsafe) {
            OutputStreamWriter writer = new OutputStreamWriter(out);
            
            writer.write(message);
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        } else {
            Jsh.testBoolean = true;
            System.err.println(message);
        }
    }

}