package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class HelperMethods {

    /**
     * This method takes in an InputStream and goes line by line populating
     * a String array with each line. This is needed to convert the input stream
     * to a String as most Applications work on String based manipulations.
     *
     * @param in The inputstream that is going to be read.
     *
     * @return Array string of data from input stream seperated by newline.
     *
     * @exception IOException If an error occurs
     */

    public static String[] readInputStream(InputStream in) throws IOException {
        final int bufferSize = 1024 * 1024; // buffer size constant

        final char[] buffer = new char[bufferSize];
        final StringBuilder pipeStr = new StringBuilder();
        Reader rdr = new InputStreamReader(in);
        int charsRead;
        while ((charsRead = rdr.read(buffer, 0, buffer.length)) > 0) {
            pipeStr.append(buffer, 0, charsRead);
        }

        return pipeStr.toString().split("\n");
    }
    
}
