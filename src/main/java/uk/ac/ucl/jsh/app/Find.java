package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;
import uk.ac.ucl.jsh.Jsh;

public class Find implements Application {

    private boolean handleArguments(
        ArrayList<String> appArgs,
        OutputStream out,
        Boolean unsafe
    )
        throws IOException {
        if (appArgs.isEmpty()) {
            HelperMethods.outputError(unsafe, out, "find: missing arguments");
            return false;
        } else if (appArgs.size() > 3) {
            HelperMethods.outputError(unsafe, out, "find: too many arguments");
            return false;
        } else if (appArgs.size() == 3 && !appArgs.get(1).equals("-name")) {
            HelperMethods.outputError(
                unsafe,
                out,
                "find: missing -name argument"
            );
            return false;
        } else if (appArgs.size() == 2 && !appArgs.get(0).equals("-name")) {
            HelperMethods.outputError(
                unsafe,
                out,
                "find: missing -name argument"
            );
            return false;
        }
        return true;
    }

    private boolean handleOutput(
        ArrayList<String> appArgs,
        OutputStream out,
        Boolean unsafe,
        int filePosition,
        String dir
    )
        throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        final int finalFilePosition = filePosition;
        try (Stream<Path> stream = Files.walk(Paths.get(dir))) {
            stream.forEach(
                line -> {
                    try {
                        String file = appArgs.get(finalFilePosition);
                        StringBuilder relativePath = new StringBuilder("");
                        String folder = line
                            .toString()
                            .substring(line.toString().lastIndexOf("/") + 1);
                        if (file.substring(0, 1).equals("*")) { //if wildcard then use pathMatcher to match on pattern
                            file = file.replace("*", "glob:**/*");
                            PathMatcher pathMatcher = FileSystems
                                .getDefault()
                                .getPathMatcher(file);
                            if (pathMatcher.matches(line)) {
                                if (finalFilePosition == 1) {
                                    relativePath.append(
                                        line.toString().replaceFirst(dir, "")
                                    );
                                    if (relativePath.charAt(0) == '/') {
                                        relativePath.insert(0, ".");
                                    } else {
                                        relativePath.insert(0, "./");
                                    }
                                } else {
                                    relativePath.append(line.toString());
                                }
                                writer.write(relativePath.toString());
                                writer.write(
                                    System.getProperty("line.separator")
                                );
                                writer.flush();
                            }
                        } else { //else look for equivalence in file names
                            if (folder.equals(file)) {
                                if (finalFilePosition == 1) {
                                    relativePath.append(
                                        line.toString().replaceFirst(dir, "")
                                    );
                                    if (relativePath.charAt(0) == '/') {
                                        relativePath.insert(0, ".");
                                    } else {
                                        relativePath.insert(0, "./");
                                    }
                                } else {
                                    relativePath.append(line.toString());
                                }
                                writer.write(relativePath.toString());
                                writer.write(
                                    System.getProperty("line.separator")
                                );
                                writer.flush();
                            }
                        }
                    } catch (Exception e) {
                        /* Not sure why outputError doesn't work without the try-catch block */
                        try {
                            //throw new RuntimeException("find: cannot find directory " + dir);
                            HelperMethods.outputError(
                                unsafe,
                                out,
                                "find: cannot find directory " + dir
                            );
                            return;
                        } catch (Exception f) {
                            throw new RuntimeException(
                                "find: unexpected error - " + f
                            );
                        }
                    }
                }
            );
        } catch (IOException e) {
            HelperMethods.outputError(
                unsafe,
                out,
                "find: cannot find directory " + dir
            );
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
        int filePosition = 0;
        String dir;

        if (!handleArguments(appArgs, out, unsafe)) {
            return;
        }

        if (appArgs.get(0).equals("-name")) {
            dir = Jsh.currentDirectory;
            filePosition = 1;
        } else {
            dir = appArgs.get(0);
            filePosition = 2;
        }

        if (!handleOutput(appArgs, out, unsafe, filePosition, dir)) {
            return;
        }
    }
}
