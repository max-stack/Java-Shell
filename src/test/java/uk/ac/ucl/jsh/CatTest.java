package uk.ac.ucl.jsh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

import uk.ac.ucl.jsh.app.Concatenate;


public class CatTest {

    static File dir;
    static File dir2;
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
        dir2 = new File("./dir2");
        if(!dir2.exists()){
            dir2.mkdirs();
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
    public void testCatSafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("dir1/file1.txt");

        new Concatenate().exec(args, null, System.out, false);
        assertEquals("AAA\nBBB\nCCC" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testCatUnsafe() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add("dir1/file1.txt");
        new Concatenate().exec(args, null, System.out, true);
        assertEquals("AAA\nBBB\nCCC" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testCatMissingDir() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("dir3");

        new Concatenate().exec(args, null, System.out, false);
        assertEquals("cat: dir3 does not exist" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testCatDir() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("dir1");

        new Concatenate().exec(args, null, System.out, false);
        assertEquals("cat: cannot open dir1" , outputStreamErrCaptor.toString().trim());
    }


    @Test
    public void testCatStdin() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        String content = "dir1/file1.txt";
        InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        new Concatenate().exec(args, in, System.out, false);
        assertEquals("AAA\nBBB\nCCC" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testCatStdinFile() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        String content = "dir1/file1.txt";
        InputStream in = new FileInputStream(content);
        new Concatenate().exec(args, in, System.out, false);
        assertEquals("AAA\nBBB\nCCC" , outputStreamCaptor.toString().trim());
    }

}
