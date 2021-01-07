package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import uk.ac.ucl.jsh.Jsh;

public class ChangeDirectory implements Application {

    private ErrorOutput error;

    public ChangeDirectory(ErrorOutput error) {
        this.error = error;
    }

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {
        if (!handleArguments(appArgs, out)) { return; }

        String dirString = appArgs.get(0);
        File dir = new File(Jsh.currentDirectory, dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            error.output(out, "cd: " + dirString + " is not an existing directory"); return;
        }
        Jsh.currentDirectory = dir.getCanonicalPath();
    }

    private boolean handleArguments(ArrayList<String> appArgs, OutputStream out) throws IOException {
        if (appArgs.isEmpty()) {
            error.output(out, "cd: missing argument"); return false;
        } else if (appArgs.size() > 1) {
            error.output(out, "cd: too many arguments"); return false;
        }
        return true;
    }

}
