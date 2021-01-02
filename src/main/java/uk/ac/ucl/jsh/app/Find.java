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


class Find implements Application {
    // find -name sort.txt
    // find jsh -name sort.txt
    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(out);
        boolean atleastOnePrinted = false;
        int filePosition = 0;
        String dir;
        
        if(appArgs.isEmpty()){
            throw new RuntimeException("find: missing arguments");
        }
        else if(appArgs.size() > 3){
            throw new RuntimeException("find: too many arguments");
        } 
        else if(appArgs.size() == 3 && !appArgs.get(1).equals("-name")){
            throw new RuntimeException("find: missing -name argument");
        }
        else if(appArgs.size() == 2 && !appArgs.get(0).equals("-name")){
            throw new RuntimeException("find:missing -name argument");
        }

        if(appArgs.get(0).equals("-name")){
            dir = Jsh.currentDirectory;
            filePosition = 1;
        }
        else {
            dir = appArgs.get(0);
            filePosition = 2;
        }
        
        final int finalFilePosition = filePosition;
        try (Stream<Path> stream = Files.walk(Paths.get(dir))) {
            stream.forEach(line -> {
                try{
                    String file = appArgs.get(finalFilePosition);
                    StringBuilder relativePath = new StringBuilder("");
                    String folder = line.toString().substring(line.toString().lastIndexOf("/")+1);
                    if(file.substring(0,1).equals("*")){ //if wildcard then use pathMatcher to match on pattern
                        file = file.replace("*", "glob:**/*");
                        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(file);
                        if(pathMatcher.matches(line)){
                            if(finalFilePosition == 1){
                                relativePath.append(line.toString().replaceFirst(dir, ""));
                                if((relativePath.charAt(0) == '/')){
                                    relativePath.insert(0, ".");
                                } else{
                                    relativePath.insert(0, "./");
                                }
                                
                            } else if(finalFilePosition == 2){
                                relativePath.append(line.toString());
                            }
                            writer.write((relativePath.toString()));
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();    
                        }
                    } else{//else look for equivalence in file names
                        if(folder.equals(file)){
                            if(finalFilePosition == 1){
                                relativePath.append(line.toString().replaceFirst(dir, ""));
                                if((relativePath.charAt(0) == '/')){
                                    relativePath.insert(0, ".");
                                } else{
                                    relativePath.insert(0, "./");
                                }
                            } else if(finalFilePosition == 2){
                                relativePath.append(line.toString());
                            }


                            writer.write((relativePath.toString()));
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }  
                    }  
                }
                catch (IOException e)  {
                        throw new RuntimeException("find: cannot find directory " + dir);
                    }
                }
                );
        }
        catch (IOException e) {
            throw new RuntimeException("find: cannot find directory " + dir);
        }
    }

}