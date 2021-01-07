package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import uk.ac.ucl.jsh.Jsh;

public class ChangeDirectory implements Application {

    private boolean handleArguments(
        ArrayList<String> appArgs,
        OutputStream out,
        Boolean unsafe
    )
        throws IOException {
        if (appArgs.isEmpty()) {
            HelperMethods.outputError(unsafe, out, "cd: missing argument");
            return false;
        } else if (appArgs.size() > 1) {
            HelperMethods.outputError(unsafe, out, "cd: too many arguments");
            return false;
        }
        return true;
    }

    public void exec(
        ArrayList<String> appArgs,
        InputStream in,
        OutputStream out,
        Boolean unsafe
    )
        throws IOException {
        if (!handleArguments(appArgs, out, unsafe)) {
            return;
        }

        String dirString = appArgs.get(0);
        File dir = new File(Jsh.currentDirectory, dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            HelperMethods.outputError(
                unsafe,
                out,
                "cd: " + dirString + " is not an existing directory"
            );
            return;
        }
        Jsh.currentDirectory = dir.getCanonicalPath();
    }
}
