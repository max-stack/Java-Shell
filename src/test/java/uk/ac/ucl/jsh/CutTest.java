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
import java.io.PrintStream;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

import uk.ac.ucl.jsh.app.Cut;


public class CutTest {

    static File file;
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outputStreamErrCaptor = new ByteArrayOutputStream();

    @BeforeAll
    static void setup() throws Exception {
        file = new File("./file1.txt");
        file.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("ABCDEFGHIJ\n1234567890\nAABBCCDDEE");
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
    public void testCutUnsafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        new Cut().exec(args, null, System.out, true);
        assertEquals("cut: missing arguments", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testCutEmptyArgs() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        new Cut().exec(args, null, System.out, false);
        assertEquals("cut: missing arguments", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testCutTooManyArgs() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-b");
        args.add("1");
        args.add("3");
        args.add("5");
        args.add("file1.txt");

        new Cut().exec(args, null, System.out, false);
        assertEquals("cut: wrong arguments", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testCutWrongTag() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-a");
        args.add("1,3,5");
        args.add("file1.txt");

        new Cut().exec(args, null, System.out, false);
        assertEquals("cut: wrong argument -a", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testCutEmptyBytes() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-b");
        args.add(",,,");
        args.add("file1.txt");

        new Cut().exec(args, null, System.out, false);
        assertEquals("cut: wrong argument ,,,", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testCutInvalidBytes() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-b");
        args.add("a,b,e-h");
        args.add("file1.txt");

        new Cut().exec(args, null, System.out, false);
        assertEquals("cut: wrong argument a,b,e-h", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testCutIndividualBytes() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-b");
        args.add("1,3,5,7,9,10,11");
        args.add("file1.txt");

        new Cut().exec(args, null, System.out, false);
        assertEquals("ACEGIJ\n135790\nABCDEE", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testCutByteRange() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-b");
        args.add("2-4,7-9");
        args.add("file1.txt");

        new Cut().exec(args, null, System.out, false);
        assertEquals("BCDGHI\n234789\nABBDDE", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testCutInvalidByteRange() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-b");
        args.add("8-2");
        args.add("file1.txt");

        new Cut().exec(args, null, System.out, false);
        assertEquals("cut: invalid range 8-2", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testCutByteOverlaps() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-b");
        args.add("2,-3,5,7-,8-,10,11");
        args.add("file1.txt");

        new Cut().exec(args, null, System.out, false);
        assertEquals("ABCEGHIJ\n12357890\nAABCDDEE", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testCutInvalidFile() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-b");
        args.add("1,3,5");
        args.add("invalid.abc");

        new Cut().exec(args, null, System.out, false);
        assertEquals("cut: invalid.abc does not exist", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testCutStdin() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("-b");
        args.add("1,3,5,7");

        String content = "ABCDEF\nInput Stream\n123456";
        InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        new Cut().exec(args, in, System.out, false);
        assertEquals("ACE\nIptS\n135", outputStreamCaptor.toString().trim());
    }

}
