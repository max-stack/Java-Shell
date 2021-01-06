package uk.ac.ucl.jsh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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



public class JshTest {

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
    public void testSafeEcho() throws Exception {
        Jsh.eval("echo foo");
        assertEquals("foo" , outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeLS() throws Exception {
        Jsh.eval("ls dir1");
        assertEquals("file1.txt",outputStreamCaptor.toString().trim());
    }

    @Test
    public void pwd() throws Exception {
        Jsh.eval("pwd");
        assertEquals("/jsh",outputStreamCaptor.toString().trim());
    }

}
