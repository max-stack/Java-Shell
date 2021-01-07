package uk.ac.ucl.jsh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.OutputStream;
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

import uk.ac.ucl.jsh.app.Tail;


public class TailTest {

    static File dir;
    static File file1;
    static File file2;
    static File file3;
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeAll
    static void setup() throws Exception{
        file1 = new File("test1.txt");
        file2 = new File("test2.txt");
        file3 = new File ("test3.txt");

        if(!file1.exists()){
            file1.createNewFile();
        }
        if(!file2.exists()){
            file2.createNewFile();
        }
        if(!file3.exists()){
            file3.createNewFile();
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
        bw.write("A A\nB B\nC C\nD D\nE E\nF F\nG G\nH H\nI I\nJ J\nK K");
        bw.close();
    }

    @AfterAll
    static void end() throws Exception{
        file1.delete();
        file2.delete();
        file3.delete();
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
        args.add("-n");
        args.add(file1.getPath());
        args.add(file2.getPath());
        args.add(file3.getPath());

        new Tail(new Safe()).exec(args, null, System.out);
        assertEquals("tail: too many arguments", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionNoNFlag() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add(file1.getPath());
        args.add(file2.getPath());

        new Tail(new Safe()).exec(args, null, System.out);
        assertEquals("tail: wrong argument " + args.get(0), outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThreeArguments() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        String num_lines_tail = "6";
        args.add("-n");
        args.add(num_lines_tail);
        args.add(file1.getPath());

        new Tail(new Safe()).exec(args, null, System.out);
        assertEquals("F F\nG G\nH H\nI I\nJ J\nK K", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeGreaterThanStorageSize() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        String num_lines_tail = "100";
        args.add("-n");
        args.add(num_lines_tail);
        args.add(file1.getPath());

        new Tail(new Safe()).exec(args, null, System.out);
        assertEquals("A A\nB B\nC C\nD D\nE E\nF F\nG G\nH H\nI I\nJ J\nK K", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeOneArgument() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add(file1.getPath());

        new Tail(new Safe()).exec(args, null, System.out);
        assertEquals("B B\nC C\nD D\nE E\nF F\nG G\nH H\nI I\nJ J\nK K", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionWrongNumber() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        String num_lines_head = "6";
        args.add("-n");
        args.add(file1.getPath());
        args.add(num_lines_head);

        new Tail(new Safe()).exec(args, null, System.out);
        assertEquals("tail: wrong number " + args.get(1), outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeInputStream() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        FileInputStream testInput = new FileInputStream(file1);

        new Tail(new Safe()).exec(args, testInput, System.out);
        assertEquals("B B\nC C\nD D\nE E\nF F\nG G\nH H\nI I\nJ J\nK K", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeInputStreamGreaterThanStorageSize() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        FileInputStream testInput = new FileInputStream(file1);
        String num_lines_tail = "100";
        args.add("-n");
        args.add(num_lines_tail);

        new Tail(new Safe()).exec(args, testInput, System.out);
        assertEquals("A A\nB B\nC C\nD D\nE E\nF F\nG G\nH H\nI I\nJ J\nK K", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionInputStream() throws Exception {
        OutputStream closedOutputStream = OutputStream.nullOutputStream();
        closedOutputStream.close();
        ArrayList<String> args = new  ArrayList<String>();
        FileInputStream testInput = new FileInputStream(file1);

        new Tail(new Safe()).exec(args, testInput, closedOutputStream);
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionCannotOpenFile() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        args.add(dir.getPath());

        new Tail(new Safe()).exec(args, null, System.out);
        assertEquals("tail: cannot open " + args.get(0) , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionFileDoesNotExist() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        String wrongFile = "wrongfile.txt";
        args.add(wrongFile);

        new Tail(new Safe()).exec(args, null, System.out);
        assertEquals("tail: " + args.get(0) +  " does not exist", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUnsafeThrowsExceptionFileDoesNotExist() throws Exception {
        ArrayList<String> args = new  ArrayList<String>();
        String wrongFile = "wrongfile.txt";
        args.add(wrongFile);

        new Tail(new Unsafe()).exec(args, null, System.out);
        assertEquals("tail: " + args.get(0) +  " does not exist", outputStreamCaptor.toString().trim());
    }
}
