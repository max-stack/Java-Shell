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
import java.util.Collections;
import uk.ac.ucl.jsh.Jsh;

public class Cut implements Application {

    public void exec(
        ArrayList<String> appArgs,
        InputStream in,
        OutputStream out,
        Boolean unsafe
    )
        throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
<<<<<<< HEAD

        if (appArgs.isEmpty()) {
            HelperMethods.outputError(unsafe, out, "cut: missing arguments");
            return;
        }
        if (appArgs.size() != 3 && appArgs.size() != 2) {
            HelperMethods.outputError(unsafe, out, "cut: wrong arguments");
            return;
        }
        if (!appArgs.get(0).equals("-b")) {
            HelperMethods.outputError(
                unsafe,
                out,
                "cut: wrong argument " + appArgs.get(0)
            );
            return;
=======
        
        boolean successfullyPassed = handleArguments(appArgs, out, unsafe);
        if (!successfullyPassed) { return; }

        String rangeArg = appArgs.get(1);
        String[] cutRanges = rangeArg.split("[,]+");
        
        // Get boundary values (e.g. -4,8-)
        Integer start, end;
        int[] boundaries = getBoundaries(cutRanges);
        if (boundaries == null) {
            HelperMethods.outputError(unsafe, out, "cut: invalid argument " + rangeArg); return;
        } else {
            start = boundaries[0];
            end = boundaries[1];
        }

        // Get individual and range values (e.g. 1,2,3-6)
        ArrayList<Integer> indexes = getIndexes(cutRanges, start, end);
        if (indexes == null) {
            HelperMethods.outputError(unsafe, out, "cut: invalid range " + rangeArg); return;
        }
        Collections.sort(indexes);
        
        if (appArgs.size() == 2) { // Take InputStream
            handleInput(writer, in, start, end, indexes);
        } else { // Use file path
            handleOutput(out, appArgs, unsafe, start, end, indexes);
        }
    }


    private boolean handleArguments(ArrayList<String> appArgs, OutputStream out, Boolean unsafe) throws IOException {
        if (appArgs.isEmpty()) {
            HelperMethods.outputError(unsafe, out, "cut: missing arguments"); return false;
        }
        if (appArgs.size() != 3 && appArgs.size() != 2) {
            HelperMethods.outputError(unsafe, out, "cut: wrong arguments"); return false;
        }
        if (!appArgs.get(0).equals("-b")) {
            HelperMethods.outputError(unsafe, out, "cut: wrong argument " + appArgs.get(0)); return false;
        }
        if (appArgs.get(1).split("[,]+").length == 0) {
            HelperMethods.outputError(unsafe, out, "cut: wrong argument " + appArgs.get(1)); return false;
>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2
        }
        return true;
    }


<<<<<<< HEAD
        if (cutRanges.length == 0) {
            HelperMethods.outputError(
                unsafe,
                out,
                "cut: wrong argument " + appArgs.get(1)
            );
            return;
        }

        // Get boundary values first (e.g. -4, 8-)
        Integer startBoundary = -1;
        Integer endBoundary = Integer.MAX_VALUE;
=======
    private void handleInput(OutputStreamWriter writer, InputStream in, Integer start, Integer end, ArrayList<Integer> indexes) throws IOException {
        String[] pipeInput = HelperMethods.readInputStream(in);
        for (String line : pipeInput) { cutFromLine(writer, line, start, end, indexes); }
    }


    private void handleOutput(OutputStream out, ArrayList<String> appArgs, boolean unsafe, Integer start, Integer end, ArrayList<Integer> indexes) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);

        String cutArg = appArgs.get(2);
        String cutFile = Jsh.currentDirectory + File.separator + cutArg;

        if (new File(cutFile).exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get(cutFile);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                String line;
                while ((line = reader.readLine()) != null) { cutFromLine(writer, line, start, end, indexes); }
            } catch (IOException e) {
                HelperMethods.outputError(unsafe, out, "cut: cannot open " + cutArg); return;
            }
        } else {
            HelperMethods.outputError(unsafe, out, "cut: " + cutArg + " does not exist"); return;
        }
    }

>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2

    private int[] getBoundaries(String[] ranges) {
        Integer start = -1;
        Integer end = Integer.MAX_VALUE;
        int value;

        for (String range : ranges) {
            Boolean valid = range.matches("^[0-9]*[-]?[0-9]*");
            if (!valid) {
<<<<<<< HEAD
                HelperMethods.outputError(
                    unsafe,
                    out,
                    "cut: invalid argument " + appArgs.get(1)
                );
                return;
=======
                return null;
>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2
            } else {
                if (range.endsWith("-")) {
                    value = Integer.parseInt(range.replace("-", "")) - 1;
<<<<<<< HEAD
                    if (value < endBoundary) {
                        endBoundary = value;
                    }
                } else if (range.startsWith("-")) {
                    value = Integer.parseInt(range.replace("-", ""));
                    if (value > startBoundary) {
                        startBoundary = value;
                    }
=======
                    if (value < end) { end = value; }
                } else if (range.startsWith("-")) {
                    value = Integer.parseInt(range.replace("-", ""));
                    if (value > start) { start = value; }
>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2
                }
            }
        }

        return new int[] {start, end};
    }


    private ArrayList<Integer> getIndexes(String[] ranges, Integer start, Integer end) {
        ArrayList<Integer> indexes = new ArrayList<Integer>();

        for (String range : ranges) {
            int l_value = 0;
            int r_value = 0;
            if (range.matches("^[0-9]+[-][0-9]+")) {
                String[] indexParts = range.split("-");
                l_value = Integer.parseInt(indexParts[0]) - 1;
                r_value = Integer.parseInt(indexParts[1]);
                if (l_value > r_value) {
<<<<<<< HEAD
                    HelperMethods.outputError(
                        unsafe,
                        out,
                        "cut: invalid range " + appArgs.get(1)
                    );
                    return;
=======
                    return null;
>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2
                }
            } else if (!range.contains("-")) {
                l_value = Integer.parseInt(range) - 1;
                r_value = l_value + 1;
            }
            for (int num = l_value; num < r_value; num++) {
<<<<<<< HEAD
                if (
                    !indexes.contains(num) &&
                    num > startBoundary - 1 &&
                    num < endBoundary
                ) {
                    indexes.add(num);
                }
            }
        }

        Collections.sort(indexes);

        if (appArgs.size() == 2) { // Take InputStream
            String[] pipeInput = HelperMethods.readInputStream(in);
            for (String line : pipeInput) {
                int start = Math.min(startBoundary, line.length());
                int end = endBoundary;

                if (start != -1) {
                    writer.write(line.substring(0, start));
                }

                for (Integer index : indexes) {
                    if (index >= line.length()) {
                        writer.write("");
                    } else {
                        writer.write(line.charAt(index));
                    }
                }

                if (end < line.length()) {
                    writer.write(line.substring(end));
                }

                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
        } else { // Use file path
            String cutArg = appArgs.get(2);

            File cutFile = new File(
                Jsh.currentDirectory + File.separator + cutArg
            );
            if (cutFile.exists()) {
                Charset encoding = StandardCharsets.UTF_8;
                Path filePath = Paths.get(
                    (String) Jsh.currentDirectory + File.separator + cutArg
                );
                try (
                    BufferedReader reader = Files.newBufferedReader(
                        filePath,
                        encoding
                    )
                ) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int start = Math.min(startBoundary, line.length());
                        int end = endBoundary;

                        if (start != -1) {
                            writer.write(line.substring(0, start));
                        }

                        for (Integer index : indexes) {
                            if (index >= line.length()) {
                                writer.write("");
                            } else {
                                writer.write(line.charAt(index));
                            }
                        }

                        if (end < line.length()) {
                            writer.write(line.substring(end));
                        }

                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                } catch (IOException e) {
                    HelperMethods.outputError(
                        unsafe,
                        out,
                        "cut: cannot open " + cutArg
                    );
                    return;
                }
            } else {
                HelperMethods.outputError(
                    unsafe,
                    out,
                    "cut: " + cutArg + " does not exist"
                );
                return;
=======
                if (!indexes.contains(num) && num > start - 1 && num < end) { indexes.add(num); }
            }
        }

        return indexes;
    }


    private void cutFromLine(OutputStreamWriter writer, String line,
                             Integer startBound, Integer endBound, ArrayList<Integer> indexes) throws IOException {
        int start = Math.min(startBound, line.length());
        int end = endBound;

        if (start != -1) { writer.write(line.substring(0, start)); }
        for (Integer index : indexes) {
            if (index >= line.length()) {
                writer.write("");
            } else {
                writer.write(line.charAt(index));
>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2
            }
        }
        if (end < line.length()) { writer.write(line.substring(end)); }

        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }
<<<<<<< HEAD
}
=======

}
>>>>>>> 6bfcec97cb2eb0fd51092d817e0096e1d58dccc2
