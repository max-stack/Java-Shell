package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Jsh;

public class List implements Application {

    private boolean handleOutput(OutputStreamWriter writer, OutputStream out, Boolean unsafe, File currDir) throws IOException{
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
            HelperMethods.outputError(unsafe, out, "ls: no such directory"); return false;
        }
        return true;
    }

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

        boolean successfullyPassed = handleOutput(writer, out, unsafe, currDir);
        if(!successfullyPassed) {return; }
    }

}