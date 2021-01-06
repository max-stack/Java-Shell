package uk.ac.ucl.jsh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
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
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import uk.ac.ucl.jsh.app.ChangeDirectory;
import uk.ac.ucl.jsh.app.PrintWorkingDirectory;


public class ChangeDirectoryTest {

    static File dir1;
    static File dir2;
    static File file1;
    String temp;
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeAll
    static void setup() throws Exception{
        dir1 = new File("./dir1");
        dir2 = new File("./dir2");

        file1 = new File("test1.txt");

        if(!file1.exists()){
            file1.createNewFile();
        }

        if(!dir1.exists()){
            dir1.mkdirs();
        }
        if(!dir2.exists()){
            dir2.mkdirs();
        }
    }

    @AfterAll
    static void end() throws Exception{
        file1.delete();
    }

    @BeforeEach
    public void changeStream() throws Exception {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
        temp = Jsh.currentDirectory;
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setErr(standardErr);
        Jsh.currentDirectory = temp;
    }

    @Test
    public void testSafeThrowsExceptionNoArguments() throws Exception {
        //Throw exception - unable to open the file.
        ArrayList<String> args = new  ArrayList<String>();

        new ChangeDirectory().exec(args, null, System.out, false);
        assertEquals("cd: missing argument", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionMoreThanOneArgument() throws Exception {
        //Throw exception - unable to open the file.
        ArrayList<String> args = new  ArrayList<String>();
        args.add(dir1.getPath());
        args.add(dir2.getPath());

        new ChangeDirectory().exec(args, null, System.out, false);
        assertEquals("cd: too many arguments", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionNotExistingDirectory() throws Exception {
        //Throw exception - unable to open the file.
        ArrayList<String> args = new  ArrayList<String>();
        String wrongDirectory = "wrongdirectory";
        args.add(wrongDirectory);

        new ChangeDirectory().exec(args, null, System.out, false);
        assertEquals("cd: " + args.get(0) + " is not an existing directory", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeThrowsExceptionNotDirectory() throws Exception {
        //Throw exception - unable to open the file.
        ArrayList<String> args = new  ArrayList<String>();
        args.add(file1.getPath());

        new ChangeDirectory().exec(args, null, System.out, false);
        assertEquals("cd: " + args.get(0) + " is not an existing directory", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUnsafeThrowsExceptionNotExistingDirectory() throws Exception {
        //Throw exception - unable to open the file.
        ArrayList<String> args = new  ArrayList<String>();
        String wrongDirectory = "wrongdirectory";
        args.add(wrongDirectory);

        new ChangeDirectory().exec(args, null, System.out, true);
        assertEquals("cd: " + args.get(0) + " is not an existing directory", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeChangeDirectory() throws Exception {
        //Throw exception - unable to open the file.
        ArrayList<String> args = new  ArrayList<String>();
        args.add(dir1.getPath());

        new ChangeDirectory().exec(args, null, System.out, false);
        new PrintWorkingDirectory().exec(args, null, System.out, false);
        assertEquals(dir1.getCanonicalPath(), outputStreamCaptor.toString().trim());
    }
}
