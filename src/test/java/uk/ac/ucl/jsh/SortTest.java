package uk.ac.ucl.jsh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import uk.ac.ucl.jsh.app.Sort;


public class SortTest {

    static File dir;
    static File file1;
    static File file2;
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeAll
    static void setup() throws Exception{
        file1 = new File("test1.txt");
        file2 = new File("test2.txt");
    
        if(!file1.exists()){
            file1.createNewFile();
        }
        if(!file2.exists()){
            file2.createNewFile();
        }

        dir = new File("./dir1");

        if(!dir.exists()){
            dir.mkdirs();
        }
        // dir = Files.createTempDirectory(Paths.get(""), "dir1").toFile();
        // dir.deleteOnExit();
        // file = File.createTempFile("file1", ".txt", dir);
        // file.deleteOnExit();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file1));
        bw.write("Zebra\n19\na\nhappy\ntest\n291\nA\nMonopoly234");
        bw.close();
    }

    @AfterAll
    static void end() throws Exception{
        file1.delete();
        file2.delete();
    }


    @BeforeEach
    public void changeStream() throws Exception {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
       
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setErr(standardErr);
    }

    @Test
    public void testSafeThrowsExceptionTooManyArguments() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add("-r");
        args.add(file1.getPath());
        args.add(file2.getPath());

        new Sort(new Safe()).exec(args, null, System.out);
        assertEquals("sort: too many arguments", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionNoRTag() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add(file1.getPath());
        args.add(file2.getPath());

        new Sort(new Safe()).exec(args, null, System.out);
        assertEquals("sort: wrong argument " + args.get(0), outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeReversedSort() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add("-r");
        args.add(file1.getPath());

        new Sort(new Safe()).exec(args, null, System.out);
        assertEquals("test\nhappy\na\nZebra\nMonopoly234\nA\n291\n19", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUnsafeThrowsExceptionUnableToWriteReversed() throws Exception {
        OutputStream closedOutputStream = OutputStream.nullOutputStream();
        closedOutputStream.close();
        ArrayList<String> args = new  ArrayList<String>();
        args.add("-r");
        args.add(file1.getPath());

        assertThrows(RuntimeException.class, () -> new Sort(new Unsafe()).exec(args, null, closedOutputStream));
    }

    @Test
    public void testSafeSort() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add(file1.getPath());

        new Sort(new Safe()).exec(args, null, System.out);
        assertEquals("19\n291\nA\nMonopoly234\nZebra\na\nhappy\ntest", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionUnableToWrite() throws Exception {
        OutputStream closedOutputStream = OutputStream.nullOutputStream();
        closedOutputStream.close();
        ArrayList<String> args = new  ArrayList<String>();
        args.add(file1.getPath());

        assertThrows(RuntimeException.class, () -> new Sort(new Unsafe()).exec(args, null, closedOutputStream));
    }

    @Test
    public void testSafeSortInputReverse() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        FileInputStream testInput = new FileInputStream(file1);
        args.add("-r");

        new Sort(new Safe()).exec(args, testInput, System.out);
        assertEquals("test\nhappy\na\nZebra\nMonopoly234\nA\n291\n19", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSortInput() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        FileInputStream testInput = new FileInputStream(file1);

        new Sort(new Safe()).exec(args, testInput, System.out);
        assertEquals("19\n291\nA\nMonopoly234\nZebra\na\nhappy\ntest", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionInputStreamUnableToWrite() throws Exception {
        OutputStream closedOutputStream = OutputStream.nullOutputStream();
        closedOutputStream.close();
        ArrayList<String> args = new  ArrayList<String>();
        FileInputStream testInput = new FileInputStream(file1);

        assertThrows(RuntimeException.class, () -> new Sort(new Unsafe()).exec(args, testInput, closedOutputStream));
    }

    @Test
    public void testSafeThrowsExceptionCannotOpenFile() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        String wrongFile = "wrongfile.txt";
        args.add(wrongFile);

        new Sort(new Safe()).exec(args, null, System.out);
        assertEquals("sort: cannot open " + args.get(0), outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUnsafeThrowsExceptionCannotOpenFile() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        String wrongFile = "wrongfile.txt";
        args.add(wrongFile);

        new Sort(new Unsafe()).exec(args, null, System.out);
        assertEquals("sort: cannot open " + args.get(0), outputStreamCaptor.toString().trim());
    }
}
