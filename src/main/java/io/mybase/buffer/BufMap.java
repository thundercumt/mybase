package io.mybase.buffer;

import static io.mybase.buffer.BufferConstants.NA;

import io.mybase.StorageException;
import io.mybase.storage.pf.PagedFile;

public class BufMap {
    MapEntry[] buckets;

    private static class MapEntry {
        private MapEntry prev;
        private MapEntry next;
        private PagedFile file;
        private int pageNum;
        private int slot;

        public MapEntry(PagedFile file, int pageNum, int slot) {
            this.file = file;
            this.pageNum = pageNum;
            this.slot = slot;
        }
    }

    public BufMap(int bucketNum) {
        buckets = new MapEntry[bucketNum];
    }

    /**
     * find page slot
     * 
     * @param file
     * @param pageNum
     * @return the page slot or -1 when the page is not found
     */
    public int find(PagedFile file, int pageNum) {
        int bucket = bucket(file, pageNum);
        for (MapEntry p = buckets[bucket]; p != null; p = p.next) {
            if (p.file == file && p.pageNum == pageNum) {
                return p.slot;
            }
        }
        return NA;
    }

    /**
     * insert page slot
     * 
     * @param file
     * @param pageNum
     * @param slot
     * @throws StorageException if duplicate is found.
     */
    public void insert(PagedFile file, int pageNum, int slot) {
        if (find(file, pageNum) != -1) {
            throw new StorageException("duplicate page exists.");
        }
        int bucket = bucket(file, pageNum);
        MapEntry entry = new MapEntry(file, pageNum, slot);
        if (buckets[bucket] == null) {
            buckets[bucket] = entry;
        } else {
            entry.next = buckets[bucket];
            entry.next.prev = entry;
            buckets[bucket] = entry;
        }
    }

    /**
     * delete page slot from map
     * 
     * @param file
     * @param pageNum
     * @return the page slot or -1 when the page is not found
     */
    public int delete(PagedFile file, int pageNum) {
        int bucket = bucket(file, pageNum);
        MapEntry p = null;
        for (p = buckets[bucket]; p != null; p = p.next) {
            if (p.file == file && p.pageNum == pageNum) {
                break;
            }
        }
        if (p != null) {
            if (p.next != null)
                p.next.prev = p.prev;
            if (p.prev != null)
                p.prev.next = p.next;
            if (buckets[bucket] == p)
                buckets[bucket] = p.next;
            return p.slot;
        }
        return NA;
    }

    private int bucket(PagedFile file, int pageNum) {
        return Math.abs(file.hashCode() * 2099 + pageNum) % buckets.length;
    }
}
