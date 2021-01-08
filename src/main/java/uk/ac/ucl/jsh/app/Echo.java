package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Echo implements Application {

    private ErrorOutput error;

    public Echo(ErrorOutput error) {
        this.error = error;
    }

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
        System.out.println(writer.getEncoding().toString());
        if (!handleArguments(appArgs, out)) { return; }
        handleOutput(appArgs, writer);
    }

    private boolean handleArguments(ArrayList<String> appArgs, OutputStream out) throws IOException {
        if (appArgs.isEmpty()) {
            error.output(out, "echo: missing arguments"); return false;
        }
        return true;
    }

    private void handleOutput(ArrayList<String> appArgs, OutputStreamWriter writer) throws IOException {
        for (int i = 0; i < appArgs.size() - 1; i++) {
            writer.write(appArgs.get(i));
            writer.write(" ");
            writer.flush();
        }
        writer.write(appArgs.get(appArgs.size() - 1));
        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }
    
}
