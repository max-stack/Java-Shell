package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import uk.ac.ucl.jsh.Jsh;
import java.nio.charset.StandardCharsets;

public class PrintWorkingDirectory implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

        writer.write(Jsh.currentDirectory);
        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }
}
