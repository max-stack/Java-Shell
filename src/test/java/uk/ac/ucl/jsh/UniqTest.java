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

import uk.ac.ucl.jsh.app.Unique;


public class UniqTest {

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
        bw.write("UNIQ test\nuniq test\nuniq test\nThis is line 4\nuniq test");
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
    public void testUniqUnsafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-i");
        args.add("file1.txt");
        args.add("extraArg");

        new Unique().exec(args, null, System.out, true);
        assertEquals("uniq: too many arguments", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUniqTooManyArgs() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-i");
        args.add("file1.txt");
        args.add("extraArg");

        new Unique().exec(args, null, System.out, false);
        assertEquals("uniq: too many arguments", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testUniqWrongTag() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-j");
        args.add("file1.txt");

        new Unique().exec(args, null, System.out, false);
        assertEquals("uniq: wrong argument -j", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testUniqFileCaseSensitive() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("file1.txt");

        new Unique().exec(args, null, System.out, false);
        assertEquals("UNIQ test\nuniq test\nThis is line 4\nuniq test", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUniqFileCaseInsensitive() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("-i");
        args.add("file1.txt");

        new Unique().exec(args, null, System.out, false);
        assertEquals("UNIQ test\nThis is line 4\nuniq test", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUniq() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("invalid.abc");

        new Unique().exec(args, null, System.out, false);
        assertEquals("uniq: wrong file argument", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testUniqStdinCaseSensitive() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        String content = "AAAA\naaaa\nBBBB\nbbbb";
        InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        new Unique().exec(args, in, System.out, false);
        assertEquals("AAAA\naaaa\nBBBB\nbbbb", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUniqStdinCaseInsensitive() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("-i");

        String content = "AAAA\naaaa\nBBBB\nbbbb";
        InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        new Unique().exec(args, in, System.out, false);
        assertEquals("AAAA\nBBBB", outputStreamCaptor.toString().trim());
    }

}
