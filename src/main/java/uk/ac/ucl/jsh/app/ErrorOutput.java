package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;

public interface ErrorOutput {
    void output(OutputStream out, String message) throws IOException;
}
