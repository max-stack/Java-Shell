package uk.ac.ucl.jsh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

import uk.ac.ucl.jsh.app.Find;
import uk.ac.ucl.jsh.app.Safe;
import uk.ac.ucl.jsh.app.Unsafe;


public class FindTest {

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
    public void testFindMissingArgs() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("find: missing arguments" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testFindTooManyArgs() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add("a");
        args.add("b");
        args.add("c");
        args.add("d");
        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("find: too many arguments" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testFindMissingArg() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add("a");
        args.add("b");
        args.add("c");
        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("find: missing -name argument" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testFindMissingArg2() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add("a");
        args.add("b");
        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("find: missing -name argument" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testFindGlob() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("-name");
        args.add("*.txt");

        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("./dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindGlob2() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("dir1");
        args.add("-name");
        args.add("*.txt");

        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindRelDir() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("dir1");
        args.add("-name");
        args.add("file1.txt");

        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindAbsDir() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("-name");
        args.add("file1.txt");

        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("./dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindAbsDirUnsafe() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("-name");
        args.add("file1.txt");

        new Find(new Unsafe()).exec(args, null, System.out);
        assertEquals("./dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindRelDirUnsafe() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("-name");
        args.add("file1.txt");

        new Find(new Unsafe()).exec(args, null, System.out);
        assertEquals("./dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindMissingDir() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("dir");
        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("find: cannot find directory dir" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testFindAbsDir2() throws Exception {
        ArrayList<String> args = new ArrayList<String>();
        String jshDir;
        jshDir = Jsh.currentDirectory;
        Jsh.currentDirectory = "";
        args.add("-name");
        args.add("file1.txt");
        new Find(new Safe()).exec(args, null, System.out);
        Jsh.currentDirectory = jshDir;
        assertEquals("./dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindAbsDirGlob() throws Exception {
        ArrayList<String> args = new ArrayList<String>();
        String jshDir;
        jshDir = Jsh.currentDirectory;
        Jsh.currentDirectory = "";
        args.add("-name");
        args.add("*.txt");
        new Find(new Safe()).exec(args, null, System.out);
        Jsh.currentDirectory = jshDir;
        assertEquals("./dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindRelDir2() throws Exception {
        ArrayList<String> args = new ArrayList<String>();
        String jshDir;
        jshDir = Jsh.currentDirectory;
        Jsh.currentDirectory = "";
        args.add("./");
        args.add("-name");
        args.add("*.txt");
        new Find(new Safe()).exec(args, null, System.out);
        Jsh.currentDirectory = jshDir;
        assertEquals("./dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }
    
    @Test
    public void testFindRelDirGlob() throws Exception {
        ArrayList<String> args = new ArrayList<String>();
        String jshDir;
        jshDir = Jsh.currentDirectory;
        Jsh.currentDirectory = "";
        args.add("./");
        args.add("-name");
        args.add("file1.txt");
        new Find(new Safe()).exec(args, null, System.out);
        Jsh.currentDirectory = jshDir;
        assertEquals("./dir1/file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFindClosedStream() throws Exception {
        OutputStream closedOutputStream = OutputStream.nullOutputStream();
        closedOutputStream.close();
        ArrayList<String> args = new ArrayList<String>();
        args.add("-name");
        args.add("file1.txt");
        new Find(new Safe()).exec(args, null, closedOutputStream);
        assertEquals("find: cannot find directory /jsh", outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testFindClosedStream2() throws Exception {
        OutputStream closedOutputStream = OutputStream.nullOutputStream();
        closedOutputStream.close();
        ArrayList<String> args = new ArrayList<String>();
        args.add("dir1");
        args.add("-name");
        args.add("file1.txt");
        new Find(new Safe()).exec(args, null, closedOutputStream);
        assertEquals("find: cannot find directory dir1", outputStreamErrCaptor.toString().trim());
    }
    
    @Test
    public void testFileNotFoundGlob() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("dir2");
        args.add("-name");
        args.add("*.txt");

        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFileNotFound() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("dir2");
        args.add("-name");
        args.add("file1.txt");

        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFileNotFoundGlob2() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("-name");
        args.add("*.abc");

        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testFileNotFound2() throws Exception {

        ArrayList<String> args = new ArrayList<String>();
        args.add("-name");
        args.add("file2.txt");

        new Find(new Safe()).exec(args, null, System.out);
        assertEquals("" , outputStreamCaptor.toString().trim());
    }
}
