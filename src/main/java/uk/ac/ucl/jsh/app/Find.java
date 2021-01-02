package uk.ac.ucl.jsh.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import java.nio.file.PathMatcher;
import java.nio.file.FileSystems;

import uk.ac.ucl.jsh.Jsh;
import uk.ac.ucl.jsh.app.HelperMethods;


class Find implements Application {
    // find -name sort.txt
    // find jsh -name sort.txt
    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out, Boolean unsafe) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        int filePosition = 0;
        String dir;
        if(appArgs.isEmpty()){
            HelperMethods.outputError(unsafe, out, "find: missing arguments"); return;
        }
        else if(appArgs.get(0).equals("-name")){
            dir = Jsh.currentDirectory;
            filePosition = 1;
        }
        else {
            if(!appArgs.get(1).equals("-name")){
                HelperMethods.outputError(unsafe, out, "find: missing -name argument"); return;
            }
            dir = appArgs.get(0);
            filePosition = 2;
        }
        
        final int finalFilePosition = filePosition;
        try (Stream<Path> stream = Files.walk(Paths.get(dir))) {
            stream.forEach(line -> {
                try {
                    String file = appArgs.get(finalFilePosition);
                    StringBuilder relativePath = new StringBuilder("");
                    String folder = line.toString().substring(line.toString().lastIndexOf("/")+1);
                    if (file.substring(0,1).equals("*")) { //if wildcard then use pathMatcher to match on pattern
                        file = file.replace("*", "glob:**/*");
                        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(file);
                        if (pathMatcher.matches(line)) {
                            if (finalFilePosition == 1) {
                                relativePath.append(line.toString().replaceFirst(dir, ""));
                                if ((relativePath.charAt(0) == '/')) {
                                    relativePath.insert(0, ".");
                                } else {
                                    relativePath.insert(0, "./");
                                }
                                
                            } else if (finalFilePosition == 2) {
                                relativePath.append(line.toString());
                            }
                            writer.write((relativePath.toString()));
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();    
                        }
                    } else { //else look for equivalence in file names
                        if (folder.equals(file)) {
                            if (finalFilePosition == 1) {
                                relativePath.append(line.toString().replaceFirst(dir, ""));
                                if ((relativePath.charAt(0) == '/')) {
                                    relativePath.insert(0, ".");
                                } else {
                                    relativePath.insert(0, "./");
                                }
                            } else if(finalFilePosition == 2) {
                                relativePath.append(line.toString());
                            }
                            writer.write((relativePath.toString()));
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }  
                    }  
                } catch (Exception e) {

                    /* Not sure why outputError doesn't work without the try-catch block */
                    try {
                        //throw new RuntimeException("find: cannot find directory " + dir);
                        HelperMethods.outputError(unsafe, out, "find: cannot find directory " + dir); return;
                    } catch (Exception f) {
                        throw new RuntimeException("find: unexpected error - " + f);
                    }

                }
            });
        } catch (IOException e) {
            HelperMethods.outputError(unsafe, out, "find: cannot find directory " + dir); return;
        }
    }

}