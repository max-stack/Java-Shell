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

import uk.ac.ucl.jsh.Jsh;


class Find implements Application {

    public void exec(ArrayList<String> appArgs, InputStream in, OutputStream out) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(out);
        boolean atleastOnePrinted = false;
        int filePosition = 0;
        String dir;
        if(appArgs.isEmpty()){
            throw new RuntimeException("find: missing arguments");
        }
        else if(appArgs.get(0).equals("-name")){
            dir = Jsh.currentDirectory;
            filePosition = 1;
        }
        else {
            if(!appArgs.get(1).equals("-name")){
                throw new RuntimeException("find: missing -name argument");
            }
            dir = appArgs.get(0);
            filePosition = 2;
        }
        final int finalFilePosition = filePosition;
        try (Stream<Path> stream = Files.walk(Paths.get(dir))) {
            stream.forEach(line -> {
                try{
                    String folder = line.toString().substring(line.toString().lastIndexOf("/")+1);
                    if(folder.equals(appArgs.get(finalFilePosition))){
                        String relativePath = line.toString().replaceFirst(dir, "");
                        writer.write(relativePath);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
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