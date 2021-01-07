package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import uk.ac.ucl.jsh.Jsh;

public class List implements Application {

<<<<<<< HEAD
    public void exec(
        ArrayList<String> appArgs,
        InputStream in,
        OutputStream out,
        Boolean unsafe
    )
        throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        File currDir;
        if (appArgs.isEmpty()) {
            currDir = new File(Jsh.currentDirectory);
        } else if (appArgs.size() == 1) {
            currDir = new File(appArgs.get(0));
        } else {
            HelperMethods.outputError(unsafe, out, "ls: too many arguments");
            return;
        }
=======
    private void handleOutput(OutputStreamWriter writer, OutputStream out, Boolean unsafe, File currDir) throws IOException{
>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2
        try {
            File[] listOfFiles = currDir.listFiles();
            boolean atLeastOnePrinted = false;
            for (File file : listOfFiles) {
                if (!file.getName().startsWith(".")) {
                    writer.write(file.getName());
                    writer.write("\t");
                    writer.flush();
                    atLeastOnePrinted = true;
                }
            }
            if (atLeastOnePrinted) {
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
        } catch (NullPointerException e) {
            HelperMethods.outputError(unsafe, out, "ls: no such directory");
            return;
        }
    }
<<<<<<< HEAD
}
=======

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        File currDir;
        if (appArgs.isEmpty()) {
            currDir = new File(Jsh.currentDirectory);
        } else if (appArgs.size() == 1) {
            currDir = new File(appArgs.get(0));
        } else {
            HelperMethods.outputError(unsafe, out, "ls: too many arguments"); return;
        }

        handleOutput(writer, out, unsafe, currDir);
    }

}
>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2
