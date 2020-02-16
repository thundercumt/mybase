package io.mybase.storage.pf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import io.mybase.StorageException;

public class PagedFileTest {
    private static final int PAGE_SIZE = 128;
    private PagedFile file;

    @Before
    public void setUp() throws IOException {
        File tmp = File.createTempFile(PagedFileTest.class.getSimpleName(), "temp");
        tmp.deleteOnExit();

        byte[] bytes = new byte[PAGE_SIZE];
        for (int i = 0; i < PAGE_SIZE; ++i) {
            bytes[i] = (byte) i;
        }

        try (FileOutputStream fout = new FileOutputStream(tmp)) {
            bytes[0] = -1;
            fout.write(bytes);
            bytes[0] = -2;
            fout.write(bytes);
            bytes[0] = -3;
            fout.write(bytes);
            fout.flush();
        }

        file = new PagedFile(tmp, PAGE_SIZE);
    }

    @Test
    public void getPageSize() {
        assertThat(file.getPageSize(), is(PAGE_SIZE));
    }

    @Test(expected = StorageException.class)
    public void notOpen() {
        file.readPage(0);
    }

    @Test(expected = StorageException.class)
    public void closedPage() {
        file.open();
        file.close();
        file.readPage(0);
    }

    @Test
    public void readPage() {
        file.open();
        Page page = file.readPage(0);
        checkPageContent(page.getData(), (byte) -1);
        page = file.readPage(1);
        checkPageContent(page.getData(), (byte) -2);
        page = file.readPage(2);
        checkPageContent(page.getData(), (byte) -3);
    }

    @Test
    public void writePage() {
        file.open();
        Page page = file.readPage(0);
        byte[] bytes = page.getData();
        for (int i = 0; i < PAGE_SIZE; ++i) {
            bytes[i] = -1;
        }
        file.writePage(1, page);
        page = file.readPage(1);
        bytes = page.getData();
        for (int i = 0; i < PAGE_SIZE; ++i) {
            assertThat(bytes[i], is((byte) -1));
        }
    }

    @Test
    public void openAgain() {
        file.open();
        file.close();
        file.open();
        file.readPage(0);
    }

    @Test
    public void getLength() {
        file.open();
        assertThat(file.getFileLength(), is((long) 3 * PAGE_SIZE));
    }

    @Test
    public void getPages() {
        file.open();
        assertThat(file.getPageCount(), is(3));
    }

    @Test
    public void readNonExistent() {
        file.open();
        file.readPage(10000);
    }

    @Test
    public void writeWithHole() {
        file.open();
        byte[] bytes = new byte[PAGE_SIZE];
        bytes[0] = 1;
        bytes[1] = 2;
        file.writePage(100, new Page(PAGE_SIZE, bytes));
        assertThat(file.getFileLength(), is(PAGE_SIZE * 101L));
        assertThat(file.getPageCount(), is(101));
        byte[] bytesRead = file.readPage(100).getData();
        assertThat(Arrays.equals(bytes, bytesRead), is(true));
    }

    private void checkPageContent(byte[] bytes, byte firstByte) {
        assertThat(bytes, notNullValue());
        assertThat(bytes.length, is(PAGE_SIZE));
        assertThat(bytes[0], is(firstByte));
        for (int i = 1; i < PAGE_SIZE; ++i) {
            assertThat(bytes[i], is((byte) i));
        }
    }
}
