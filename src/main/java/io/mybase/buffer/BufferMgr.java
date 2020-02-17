package io.mybase.buffer;

import static io.mybase.buffer.BufferConstants.MAP_BUCKETS;
import static io.mybase.buffer.BufferConstants.NA;

import io.mybase.storage.pf.Page;
import io.mybase.storage.pf.PagedFile;

public class BufferMgr {
    private BufList<Page> bufTable;
    private BufMap bufMap;

    public BufferMgr(int capacity) {
        bufTable = new BufList<>(capacity);
        bufMap = new BufMap(MAP_BUCKETS);
    }

    public Page getPage(PagedFile file, int pageNum) {
        int slot = bufMap.find(file, pageNum);
        Page page;
        if (slot == NA) {
            file.open();
            page = file.readPage(pageNum);
            slot = bufTable.add(page);
            bufMap.insert(file, pageNum, slot);
        } else {
            page = bufTable.getValue(slot);
        }
        return page;
    }

    public void flush(PagedFile file, int pageNum) {
        Page page = getPage(file, pageNum);
        if (page.isDirty()) {
            file.open();
            file.writePage(pageNum, page);
        }
    }

    public int available() {
        return bufTable.getAvailable();
    }
}
