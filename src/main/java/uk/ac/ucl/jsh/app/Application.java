package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public interface Application {
    void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException;
}
