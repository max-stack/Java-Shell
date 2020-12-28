package uk.ac.ucl.jsh;

import java.io.PipedInputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.ArrayList;

public class RunCommand implements Runnable{
    ArrayList<String> appArgs;
    OutputStream out;
    PipedInputStream in;

    public RunCommand(ArrayList<String> appArgs, OutputStream out, PipedInputStream in){
        this.appArgs = appArgs;
        this.out = out;
        this.in = in;
    }

    public void run(){
        try{
            for(String appArg : appArgs){
                out.write(appArg.getBytes());
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}