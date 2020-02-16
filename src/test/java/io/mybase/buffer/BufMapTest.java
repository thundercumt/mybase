package io.mybase.buffer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.mybase.storage.pf.PagedFile;

public class BufMapTest {
    private BufMap map;
    private PagedFile file;

    @Before
    public void setUp() throws IOException {
        map = new BufMap(10);
        File tmp = File.createTempFile("bufmaptest", "temp");
        tmp.deleteOnExit();
        file = new PagedFile(tmp, 1024);
    }

    @After
    public void tearDown() {
        file.close();
    }

    @Test
    public void crud() {
        assertThat(map.find(file, 0), is(-1));
        assertThat(map.find(file, 100), is(-1));
        assertThat(map.delete(file, 100), is(-1));
        map.insert(file, 1, 11);
        assertThat(map.find(file, 1), is(11));
        map.insert(file, 3, 33);
        assertThat(map.find(file, 3), is(33));
        map.insert(file, 5, 55);
        assertThat(map.delete(file, 5), is(55));
        assertThat(map.delete(file, 5), is(-1));
    }
}
