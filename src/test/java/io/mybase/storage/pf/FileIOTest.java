package io.mybase.storage.pf;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileIOTest {

    private FileIO file;

    @Before
    public void setup() throws IOException {
        file = new FileIO(File.createTempFile("FileIOTest", ".temp"));
    }

    @After
    public void destroy() {
        file.delete();
    }

    @Test
    public void testExist() {
        assertTrue(file.getFile().exists());
    }

    @Test
    public void testDelete() {
        file.delete();
        assertFalse(file.getFile().exists());
    }

    @Test
    public void testWrite() {
        byte[] output = "1234567890".getBytes();
        assertThat(output.length, is(10));
        file.open();
        file.write(output);
        byte[] input = new byte[512];
        file.seek(0);
        int count = file.read(input);
        assertThat(count, is(10));
        assertTrue(Arrays.equals(output, Arrays.copyOf(input, count)));
        file.close();
    }

    @Test
    public void testSeek() {
        file.open(); 
        file.seek(10);
        file.write("abc".getBytes());
        byte[] input = new byte[512];
        assertThat(file.read(input), is(-1));
        file.seek(0);
        assertThat(file.read(input), is(10 + 3));
        for (int i = 0; i < 9; ++i)
            assertTrue(input[i] == 0);
        assertTrue(input[10] == 97);
        assertTrue(input[11] == 98);
        assertTrue(input[12] == 99);
    }
}
