package uk.ac.ucl.jsh;

import java.io.PipedInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.ArrayList;

import uk.ac.ucl.jsh.app.Application;
import uk.ac.ucl.jsh.app.ApplicationFactory;

public class RunCommand implements Runnable{
    ArrayList<String> tokens;
    OutputStream out;
    InputStream in;

    public RunCommand(ArrayList<String> tokens, OutputStream out, InputStream in){
        this.tokens = tokens;
        this.out = out;
        this.in = in;
    }

    public void run(){
        try{
            String appName = tokens.get(0);
            ArrayList<String> appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));
            Application app = ApplicationFactory.make(appName);
            app.exec(appArgs, in, out);
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}