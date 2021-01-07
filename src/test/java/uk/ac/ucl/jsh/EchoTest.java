package uk.ac.ucl.jsh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import uk.ac.ucl.jsh.app.Echo;
import uk.ac.ucl.jsh.app.Safe;
import uk.ac.ucl.jsh.app.Unsafe;


public class EchoTest {

    static File dir;
    static File file;
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outputStreamErrCaptor = new ByteArrayOutputStream();

    @BeforeAll
    static void setup() throws Exception{
        dir = new File("./dir1");
        if(!dir.exists()){
            dir.mkdirs();
        }
        file = new File("./dir1/file1.txt");
        file.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("AAA\nBBB\nCCC");
        bw.close();
    }

    @BeforeEach
    public void changeStream() throws Exception {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamErrCaptor));
       
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setErr(standardErr);
    }

    @Test
    public void testEchoSafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("Hello World!");

        new Echo(new Safe()).exec(args, null, System.out);
        assertEquals("Hello World!" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testEchoMultiArg() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("Hello World!");
        args.add("Goodbye World!");

        new Echo(new Safe()).exec(args, null, System.out);
        assertEquals("Hello World! Goodbye World!" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testEchoEmptyArgs() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        new Echo(new Safe()).exec(args, null, System.out);
        assertEquals("echo: missing arguments" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testEchoEmptyArgsUnsafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        new Echo(new Unsafe()).exec(args, null, System.out);
        assertEquals("echo: missing arguments" , outputStreamCaptor.toString().trim());
    }

}
