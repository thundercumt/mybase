package io.mybase.buffer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import io.mybase.storage.pf.Page;
import io.mybase.storage.pf.PagedFile;

public class BufferMgrTest {
    private static final int PAGE_SIZE = 128;

    private BufferMgr bufMgr;
    private PagedFile file;

    @Before
    public void setUp() throws IOException {
        bufMgr = new BufferMgr(10);
        File tmp = File.createTempFile(BufferMgrTest.class.getSimpleName(), "temp");
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
    public void getPage() {
        assertThat(bufMgr.available(), is(10));
        bufMgr.getPage(file, 0);
        assertThat(bufMgr.available(), is(9));
    }

    @Test
    public void flushPage() {
        Page page = bufMgr.getPage(file, 0);
        byte[] data = page.getData();
        assertThat(data[0], is((byte) -1));
        assertThat(data[1], is((byte) 1));
        assertThat(data[2], is((byte) 2));
        data[0] = -101;
        data[1] = -102;
        data[2] = -103;

        page.setDirty(true);
        bufMgr.flush(file, 0);

        assertThat(page == bufMgr.getPage(file, 0), is(true));
        Page newPage = file.readPage(0);
        byte[] newData = newPage.getData();
        assertThat(newPage != page, is(true));
        assertThat(Arrays.equals(data, newData), is(true));
    }
}
