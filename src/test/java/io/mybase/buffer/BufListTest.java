package io.mybase.buffer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BufListTest {
    private BufList<Integer> list;

    @Before
    public void setUp() {
        list = new BufList<>(5);
        assertThat(list.getCapacity(), is(5));
        assertThat(list.getAvailable(), is(5));
    }

    @Test
    public void crud() {
        int slot = list.add(100);
        assertThat(list.getValue(slot), is(100));
        list.setValue(slot, 200);
        assertThat(list.getValue(slot), is(200));
        assertThat(list.getAvailable(), is(4));
        list.remove(slot);
        assertThat(list.getValue(slot), nullValue());
        assertThat(list.getAvailable(), is(5));
    }
}
