package io.mybase.buffer;

import java.util.LinkedList;
import java.util.function.Predicate;

import io.mybase.storage.pf.Page;
import io.mybase.storage.pf.PagedFile;

public class BufferMgr {

    private static class Entry {
        private PagedFile file;
        private int pageNum;
        private Page page;

        public Entry(PagedFile file, int pageNum, int pageSize) {
            this.file = file;
            this.pageNum = pageNum;
            page = new Page(pageSize);
        }
    }

    private static class BufHead {
        private LinkedList<Entry> bucket = new LinkedList<>();
    }

    private int capacity;
    private BufHead[] table;
    private LinkedList<Entry> used;
    private LinkedList<Entry> free;
    private final Predicate<Entry> predicate = (e -> e.file == null);

    public BufferMgr(int capacity) {
        this.capacity = capacity;
        this.table = new BufHead[capacity];
        this.used = new LinkedList<>();
        this.free = new LinkedList<>();
    }

    private int hash(Entry entry) {
        PagedFile file = entry.file;
        int pageNum = entry.pageNum;
        return hash(file, pageNum);
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
        putData(file, pageNum, page);
        return page;
    }

    public Page putData(PagedFile file, int pageNum, Page page) {
        int slot = hash(file, pageNum);
        Entry old = findEntry(file, pageNum);
        Page oldPage = null;
        if (old != null) {
            oldPage = old.page;
            old.page = page;
        }
        return oldPage;
    }
}
