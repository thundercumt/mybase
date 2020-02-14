package io.mybase.buffer;

import java.util.LinkedList;
import java.util.stream.IntStream;

import io.mybase.storage.pf.Page;
import io.mybase.storage.pf.PagedFile;

public class BufferMgr {

    private static class Entry {
        private PagedFile file;
        private int pageNum;
        private Page page;

        public Entry(PagedFile file, int pageNum, Page page) {
            this.file = file;
            this.pageNum = pageNum;
            this.page = page;
        }
    }

    private static class BufHead {
        private LinkedList<Entry> bucket = new LinkedList<>();
    }

    private int capacity;
    private BufHead[] table;
    private LinkedList<Integer> used;
    private LinkedList<Integer> free;

    public BufferMgr(int capacity) {
        this.capacity = capacity;
        this.table = new BufHead[capacity];
        this.used = new LinkedList<>();
        this.free = new LinkedList<>();
        IntStream.range(0, capacity).forEach(i -> free.addLast(i));
    }

    private int hash(PagedFile file, int pageNum) {
        return (file.hashCode() * 2099 + pageNum) % capacity;
    }

    private Entry findEntry(PagedFile file, int pageNum) {
        int slot = hash(file, pageNum);
        return table[slot].bucket.stream().filter(e -> e.file == file && e.pageNum == pageNum).findFirst().orElse(null);
    }

    public Page getData(PagedFile file, int pageNum) {
        Entry entry = findEntry(file, pageNum);

        if (entry != null) {
            return entry.page;
        }

        Page page = file.readPage(pageNum);
        putDataDirect(file, pageNum, page);
        return page;
    }

    public Page putData(PagedFile file, int pageNum, Page page) {
        int slot = hash(file, pageNum);
        Entry old = findEntry(file, pageNum);
        Page oldPage = null;
        if (old != null) {
            oldPage = old.page;
            old.page = page;
        } else {
            old = new Entry(file, pageNum, page);
            table[slot].bucket.addFirst(old);
        }
        return oldPage;
    }

    private void putDataDirect(PagedFile file, int pageNum, Page page) {
        int slot = hash(file, pageNum);
        table[slot].bucket.addFirst(new Entry(file, pageNum, page));
    }
}
