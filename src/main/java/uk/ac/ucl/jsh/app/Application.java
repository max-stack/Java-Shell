package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public interface Application{

    public void exec(ArrayList<String> appArgs, OutputStream out) throws IOException;
        

}