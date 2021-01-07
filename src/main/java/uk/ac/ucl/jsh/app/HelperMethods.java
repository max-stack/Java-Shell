package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

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
    
}
