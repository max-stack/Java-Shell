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
import java.util.*;

import uk.ac.ucl.jsh.app.List;
import uk.ac.ucl.jsh.app.Safe;
import uk.ac.ucl.jsh.app.Unsafe;


public class ListTest {

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
    public void testListSafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();

        new List(new Safe()).exec(args, null, System.out);
        assertEquals("src jsh Dockerfile tools pom.xml dir2 target dir1" , outputStreamCaptor.toString().trim().replaceAll("[\\n\\t ]", " "));
    }

    @Test
    public void testListMultiArg() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("a");
        args.add("b");

        new List(new Safe()).exec(args, null, System.out);
        assertEquals("ls: too many arguments" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testListEmptyArgsUnsafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        new List(new Unsafe()).exec(args, null, System.out);
        assertEquals("src jsh Dockerfile tools pom.xml dir2 target dir1" , outputStreamCaptor.toString().trim().replaceAll("[\\n\\t ]", " "));
    }

    @Test
    public void testListMissingDir() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("dir3");

        new List(new Safe()).exec(args, null, System.out);
        assertEquals("ls: no such directory" , outputStreamErrCaptor.toString().trim());
    }

    @Test
    public void testListDir() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("dir1");

        new List(new Safe()).exec(args, null, System.out);
        assertEquals("file1.txt" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testListMissingDirUnsafe() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("dir3");

        new List(new Unsafe()).exec(args, null, System.out);
        assertEquals("ls: no such directory" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testListEmptyDir() throws Exception {

        ArrayList<String> args = new  ArrayList<String>();
        args.add("dir2");

        new List(new Unsafe()).exec(args, null, System.out);
        assertEquals("" , outputStreamCaptor.toString().trim());
    }

}
