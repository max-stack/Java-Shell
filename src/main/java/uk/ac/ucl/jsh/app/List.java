package uk.ac.ucl.jsh.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import uk.ac.ucl.jsh.Jsh;
import java.nio.charset.StandardCharsets;

public class List implements Application {

    private ErrorOutput error;

    public List(ErrorOutput error) {
        this.error = error;
    }

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        
        File currDir;
        if (appArgs.isEmpty()) {
            currDir = new File(Jsh.currentDirectory);
        } else if (appArgs.size() == 1) {
            currDir = new File(appArgs.get(0));
        } else {
            error.output(out, "ls: too many arguments"); return;
        }

        if (!handleOutput(writer, out, currDir)) { return; }
    }

    private boolean handleOutput(OutputStreamWriter writer, OutputStream out, File currDir) throws IOException{
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
            error.output(out, "ls: no such directory"); return false;
        }
        return true;
    }

}
