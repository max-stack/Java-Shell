package uk.ac.ucl.jsh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

import uk.ac.ucl.jsh.app.GlobalRegExPrint;


public class GrepTest {

    static File dir;
    static File file1;
    static File file2;
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outputStreamErrCaptor = new ByteArrayOutputStream();

    @BeforeAll
    static void setup() throws Exception {
        dir = new File("./dir1");
        if(!dir.exists()){
            dir.mkdirs();
        }
        file1 = new File("./fruits.txt");
        file1.createNewFile();
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1));
        bw1.write("A is for Apple\nB is for Banana\nC is for Cherry");
        bw1.close();
        file2 = new File("./vegetables.txt");
        file2.createNewFile();
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(file2));
        bw2.write("A is for Asparagus\nB is for Broccoli\nC is for Carrot");
        bw2.close();
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
    public void testGrepUnsafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        new GlobalRegExPrint().exec(args, null, System.out, true);
        assertEquals("grep: missing arguments", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testGrepEmptyArgs() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        new GlobalRegExPrint().exec(args, null, System.out, false);
        assertEquals("grep: missing arguments", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testGrepInvalidPattern() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("{}");
        args.add("fruits.txt");

        new GlobalRegExPrint().exec(args, null, System.out, false);
        assertEquals("grep: wrong pattern syntax {}", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testGrepSingleFile() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("Banana");
        args.add("fruits.txt");

        new GlobalRegExPrint().exec(args, null, System.out, false);
        assertEquals("B is for Banana", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testGrepMultipleFiles() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("[C]");
        args.add("fruits.txt");
        args.add("vegetables.txt");

        new GlobalRegExPrint().exec(args, null, System.out, false);
        assertEquals("fruits.txt:C is for Cherry\nvegetables.txt:C is for Carrot", 
                     outputStreamCaptor.toString().trim());
    }

    @Test
    public void testGrepFileDoesntExist() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("[a-z]");
        args.add("invalid.abc");

        new GlobalRegExPrint().exec(args, null, System.out, false);
        assertEquals("grep: wrong file argument", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testGrepOnDirectory() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("[a-z]");
        args.add("dir1");

        new GlobalRegExPrint().exec(args, null, System.out, false);
        assertEquals("grep: wrong file argument", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testGrepStdin() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("[a-z]");

        String content = "ABCDEF\nabcdef\n123456";
        InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        new GlobalRegExPrint().exec(args, in, System.out, false);
        assertEquals("abcdef", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testGrepClosedOutput() throws Exception {

        OutputStream closedOutputStream = OutputStream.nullOutputStream();
        closedOutputStream.close();

        ArrayList<String> args = new ArrayList<String>();
        args.add("[a-z]");
        args.add("fruits.txt");

        new GlobalRegExPrint().exec(args, null, closedOutputStream, false);
        assertEquals("grep: cannot open fruits.txt", outputStreamErrCaptor.toString().trim());
    }

}
