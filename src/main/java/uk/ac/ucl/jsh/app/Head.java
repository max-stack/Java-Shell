package uk.ac.ucl.jsh.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import uk.ac.ucl.jsh.Jsh;

public class Head implements Application {

    public void exec(
        ArrayList<String> appArgs,
        InputStream in,
        OutputStream out,
        Boolean unsafe
    )
        throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        if (appArgs.size() > 3) {
            HelperMethods.outputError(unsafe, out, "head: too many arguments");
            return;
        }
        if (appArgs.size() > 1 && !appArgs.get(0).equals("-n")) {
            HelperMethods.outputError(
                unsafe,
                out,
                "head: wrong argument " + appArgs.get(0)
            );
            return;
        }

        int headLines = 10;
        String headArg = "";
        if (appArgs.size() == 3) { // Number of lines and file path provided
            headArg = appArgs.get(2);
        } else if (appArgs.size() == 1) { // File path provided (use default number of lines: 10)
            headArg = appArgs.get(0);
        }

        if (appArgs.size() > 1) {
            try {
                headLines = Integer.parseInt(appArgs.get(1));
            } catch (NumberFormatException e) {
                HelperMethods.outputError(
                    unsafe,
                    out,
                    "head: wrong number " + appArgs.get(1)
                );
                return;
            }
        }

        if (headArg.isEmpty()) { // Take InputStream
            String[] pipeInput = HelperMethods.readInputStream(in);
            for (int i = 0; i < headLines; i++) {
                try {
                    writer.write(pipeInput[i]);
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                } catch (Exception e) {
                    break;
                }
            }
        } else { // Use file path
            File headFile = new File(
                Jsh.currentDirectory + File.separator + headArg
            );
            if (headFile.exists()) {
                Charset encoding = StandardCharsets.UTF_8;
                Path filePath = Paths.get(
                    (String) Jsh.currentDirectory + File.separator + headArg
                );
                try (
                    BufferedReader reader = Files.newBufferedReader(
                        filePath,
                        encoding
                    )
                ) {
                    for (int i = 0; i < headLines; i++) {
                        String line = null;
                        if ((line = reader.readLine()) != null) {
                            writer.write(line);
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }
                    }
                } catch (IOException e) {
                    HelperMethods.outputError(
                        unsafe,
                        out,
                        "head: cannot open " + headArg
                    );
                    return;
                }
            } else {
                HelperMethods.outputError(
                    unsafe,
                    out,
                    "head: " + headArg + " does not exist"
                );
                return;
            }
        }
    }
}
