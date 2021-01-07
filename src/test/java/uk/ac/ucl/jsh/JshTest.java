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
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;



public class JshTest {

    static File dir1;
    static File dir2;
    static File file1;
    static File file2;
    static File file3;
    static File file4;
    static File file5;
    String temp;
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outputStreamErrCaptor = new ByteArrayOutputStream();


    @BeforeAll
    static void setup() throws Exception{
        file1 = new File("./test.txt");
        file1.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file1));
        bw.write("''");
        bw.close();

        dir1 = new File("./dir1");
        if(!dir1.exists()){
            dir1.mkdirs();
        }

        file2 = new File(dir1.getPath() + "/file1.txt");
        file2.createNewFile();
        bw = new BufferedWriter(new FileWriter(file2));
        bw.write("AAA\nBBB\nAAA");
        bw.close();

        file3 = new File(dir1.getPath() + "/file2.txt");
        file3.createNewFile();
        bw = new BufferedWriter(new FileWriter(file3));
        bw.write("CCC");
        bw.close();

        file4 = new File(dir1.getPath() + "/longfile.txt");
        file4.createNewFile();
        bw = new BufferedWriter(new FileWriter(file4));
        for(int i = 1; i <= 20; i++){
             bw.write(i + "\n");
        }
        bw.close();

        dir2 = new File("./dir2/subdir");
        if(!dir2.exists()){
            dir2.mkdirs();
        }

        file5 = new File(dir2.getPath() + "/file.txt");
        file5.createNewFile();
        bw = new BufferedWriter(new FileWriter(file5));
        bw.write("AAA\naaa\nAAA");
        bw.close();

    }

    @AfterAll
    static void end() throws Exception{
        file1.delete();
        file2.delete();
        file3.delete();
        file4.delete();
        file5.delete();
        dir2.delete();
        File newFile = new File("./newfile.txt");
        newFile.delete();
    }

    @BeforeEach
    public void changeStream() throws Exception {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamErrCaptor));
        temp = Jsh.currentDirectory;
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setErr(standardErr);
        Jsh.currentDirectory = temp;
    }

    @Test
    public void testSafeEcho() throws Exception {
        Jsh.eval("echo hello world");
        assertEquals("hello world", outputStreamCaptor.toString().trim());
    }

    // @Test
    // public void testSafeLs() throws Exception {
    //     Jsh.eval("ls dir1");
    //     assertEquals("longfile.txt file1.txt file2.txt", outputStreamCaptor.toString().trim());
    // }

    @Test
    public void testSafePwd() throws Exception {
        Jsh.eval("pwd");
        assertEquals("/jsh", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCdPwd() throws Exception {
        Jsh.eval("cd dir1; pwd");
        assertEquals("/jsh/dir1", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCat() throws Exception {
        Jsh.eval("cat dir1/file1.txt dir1/file2.txt");
        assertEquals("AAA\nBBB\nAAA\naaa", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCatStdin() throws Exception {
        Jsh.eval("cat < dir1/file1.txt");
        assertEquals("AAA\nBBB\nAAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeHead() throws Exception {
        Jsh.eval("head dir1/longfile.txt");
        assertEquals("1\n2\n3\n4\n5\n6\n7\n8\n9\n10", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeHeadStdin() throws Exception {
        Jsh.eval("head < dir1/longfile.txt");
        assertEquals("1\n2\n3\n4\n5\n6\n7\n8\n9\n10", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeHeadN5() throws Exception {
        Jsh.eval("head -n 5 dir1/longfile.txt");
        assertEquals("1\n2\n3\n4\n5", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeHeadN50() throws Exception {
        Jsh.eval("head -n 50 dir1/longfile.txt");
        assertEquals("1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19\n20", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeHeadN0() throws Exception {
        Jsh.eval("head -n 0 dir1/longfile.txt");
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeTail() throws Exception {
        Jsh.eval("tail dir1/longfile.txt");
        assertEquals("11\n12\n13\n14\n15\n16\n17\n18\n19\n20", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeTailStdin() throws Exception {
        Jsh.eval("tail < dir1/longfile.txt");
        assertEquals("11\n12\n13\n14\n15\n16\n17\n18\n19\n20", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeTailN5() throws Exception {
        Jsh.eval("tail -n 5 dir1/longfile.txt");
        assertEquals("16\n17\n18\n19\n20", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeTailN50() throws Exception {
        Jsh.eval("tail -n 50 dir1/longfile.txt");
        assertEquals("1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19\n20", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeTailN0() throws Exception {
        Jsh.eval("tail -n 0 dir1/longfile.txt");
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeGrep() throws Exception {
        Jsh.eval("grep AAA dir1/file1.txt");
        assertEquals("AAA\nAAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeGrepNoMatches() throws Exception {
        Jsh.eval("grep DDD dir1/file1.txt");
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeGrepRe() throws Exception {
        Jsh.eval("grep 'A..' dir1/file1.txt");
        assertEquals("AAA\nAAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeGrepFiles() throws Exception {
        Jsh.eval("grep '...' dir1/file1.txt dir1/file2.txt");
        assertEquals("dir1/file1.txt:AAA\ndir1/file1.txt:BBB\ndir1/file1.txt:AAA\ndir1/file2.txt:aaa", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeGrepStdin() throws Exception {
        Jsh.eval("cat dir1/file1.txt dir1/file2.txt | grep '...'");
        assertEquals("AAA\nBBB\nAAA\naaa", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSort() throws Exception {
        Jsh.eval("sort dir1/file1.txt");
        assertEquals("AAA\nAAA\nBBB", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSortStdin() throws Exception {
        Jsh.eval("sort < dir1/file1.txt");
        assertEquals("AAA\nAAA\nBBB", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSortR() throws Exception {
        Jsh.eval("sort -r dir1/file1.txt");
        assertEquals("BBB\nAAA\nAAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeUniq() throws Exception {
        Jsh.eval("uniq dir2/subdir/file.txt");
        assertEquals("AAA\naaa\nAAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeUniqStdin() throws Exception {
        Jsh.eval("uniq < dir2/subdir/file.txt");
        assertEquals("AAA\naaa\nAAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSortUniq() throws Exception {
        Jsh.eval("sort dir1/file1.txt | uniq");
        assertEquals("AAA\nBBB", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeUniqI() throws Exception {
        Jsh.eval("uniq -i dir2/subdir/file.txt");
        assertEquals("AAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCut() throws Exception {
        Jsh.eval("cut -b 1 dir1/file1.txt");
        assertEquals("A\nB\nA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCutInterval() throws Exception {
        Jsh.eval("cut -b 2-3 dir1/file1.txt");
        assertEquals("AA\nBB\nAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCutOpenInterval() throws Exception {
        Jsh.eval("cut -b 2- dir1/file1.txt");
        assertEquals("AA\nBB\nAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCutOverlapping() throws Exception {
        Jsh.eval("cut -b 2-,3- dir1/file1.txt");
        assertEquals("AA\nBB\nAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCutStdin() throws Exception {
        Jsh.eval("echo abc | cut -b 1");
        assertEquals("a", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeCutUnion() throws Exception {
        Jsh.eval("echo abc | cut -b -1,2-");
        assertEquals("abc", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeFind() throws Exception {
        Jsh.eval("find -name file.txt");
        assertEquals("./dir2/subdir/file.txt", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeFindDir() throws Exception {
        Jsh.eval("find dir1 -name '*.txt'");
        assertEquals("dir1/longfile.txt\ndir1/file1.txt\ndir1/file2.txt", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeInputRedirection() throws Exception {
        Jsh.eval("cat < dir1/file2.txt");
        assertEquals("CCC", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeInputRedirectionInfront() throws Exception {
        Jsh.eval("< dir1/file2.txt cat");
        assertEquals("aaa", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeRedirectionNoSpace() throws Exception {
        Jsh.eval("cat <dir1/file2.txt");
        assertEquals("CCC", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeOutputRedirection() throws Exception {
        Jsh.eval("echo foo > newfile.txt");
        Jsh.eval("cat newfile.txt");
        assertEquals("foo", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeOutputRedirectionOverwrite() throws Exception {
        Jsh.eval("echo foo > test.txt");
        Jsh.eval("cat test.txt");
        assertEquals("foo", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeGlobbing() throws Exception {
        Jsh.eval("echo *.txt");
        assertEquals("test.txt newfile.txt", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeGlobbingDir() throws Exception {
        Jsh.eval("echo dir1/*.txt");
        assertEquals("dir1/longfile.txt dir1/file1.txt dir1/file2.txt", outputStreamCaptor.toString().trim());
    }

    // @Test
    // public void testSafeSemicolon() throws Exception {
    //     Jsh.eval("echo AAA; echo BBB");
    //     assertEquals("AAA\nBBB", outputStreamCaptor.toString().trim());
    // }

    @Test
    public void testSafeSemicolonChain() throws Exception {
        Jsh.eval("echo AAA; echo BBB; echo CCC");
        assertEquals("AAA\nBBB\nCCC", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSemicolonException() throws Exception {
        Jsh.eval("ls dir3; echo BBB");
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUnsafeLs() throws Exception {
        Jsh.eval("_ls dir3; echo AAA > newfile.txt");
        Jsh.eval("cat newfile.txt");
        assertEquals("ls: no such directory\nAAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafePipeUniq() throws Exception {
        Jsh.eval("echo aaa > dir1/file2.txt; cat dir1/file1.txt dir1/file2.txt | uniq -i");
        assertEquals("AAA\nBBB\nAAA", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafePipeChainSortUniq() throws Exception {
        Jsh.eval("cat dir1/file1.txt dir1/file2.txt | sort | uniq");
        assertEquals("AAA\nBBB\naaa", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafePipeException() throws Exception {
        Jsh.eval("ls dir3 | echo foo");
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSubstitution() throws Exception {
        Jsh.eval("echo `echo foo`");
        assertEquals("foo", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSubstitutionInsideArg() throws Exception {
        Jsh.eval("echo a`echo a`a");
        assertEquals("aaa", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSubstitutionSplitting() throws Exception {
        Jsh.eval("echo `echo foo  bar`");
        assertEquals("foo bar", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSubstitutionSortFind() throws Exception {
        Jsh.eval("cat `find dir2 -name '*.txt'` | sort");
        assertEquals("AAA\nAAA\naaa", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSubstitutionSemicolon() throws Exception {
        Jsh.eval("echo `echo foo; echo bar`");
        assertEquals("foo bar", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSubstitutionKeywords() throws Exception {
        Jsh.eval("> test.txt echo \"''\"");
        Jsh.eval("echo `cat test.txt`");
        assertEquals("''", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSubstitutionApp() throws Exception {
        Jsh.eval("`echo echo` foo");
        assertEquals("foo", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSingleQuotes() throws Exception {
        Jsh.eval("echo 'a  b'");
        assertEquals("a  b", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeQuoteKeyword() throws Exception {
        Jsh.eval("echo ';'");
        assertEquals(";", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeDoubleQuotes() throws Exception {
        Jsh.eval("");
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafe() throws Exception {
        Jsh.eval("echo \"a  b\"");
        assertEquals("a  b", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSubstitutionDoubleQuotes() throws Exception {
        Jsh.eval("echo \"`echo foo`\"");
        assertEquals("foo", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeNestedDoubleQuotes() throws Exception {
        Jsh.eval("echo \"a `echo \"b\"`\"");
        assertEquals("a b", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeDiabledDoubleQuotes() throws Exception {
        Jsh.eval("echo '\"\"'");
        assertEquals("\"\"", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSafeSplitting() throws Exception {
        Jsh.eval("echo a\"b\"c");
        assertEquals("abc", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testUnknownApp() throws Exception {
        assertThrows(RuntimeException.class, () -> Jsh.eval("unknown"));
    }


}
