package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Jsh;
import uk.ac.ucl.jsh.app.HelperMethods;

public class ChangeDirectory implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {

        if(Thread.currentThread().isInterrupted()){
            return;
        }

        if (appArgs.isEmpty()) {
            HelperMethods.outputError(unsafe, out, "cd: missing argument"); return;
        } else if (appArgs.size() > 1) {
            HelperMethods.outputError(unsafe, out, "cd: too many arguments"); return;
        }
        String dirString = appArgs.get(0);
        File dir = new File(Jsh.currentDirectory, dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            HelperMethods.outputError(unsafe, out, "cd: " + dirString + " is not an existing directory"); return;
        }
        Jsh.currentDirectory = dir.getCanonicalPath();
    
    }

}