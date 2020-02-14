package io.mybase.storage.pf;

import java.io.Closeable;
import java.io.File;

public class PagedFile implements Closeable {
    private static final int DEFAULT_PAGE_SIZE = 16 * 1024;

    private FileIO file;
    private int pageSize;

    public PagedFile(File file, int pageSize) {
        if (pageSize < DEFAULT_PAGE_SIZE) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.file = new FileIO(file);
        this.pageSize = pageSize;
    }

    public Page readPage(int pageNum) {
        file.forceOpen();
        byte[] bytes = new byte[pageSize];
        file.seek(pageNum * pageSize);
        file.read(bytes);
        return new Page(pageSize, bytes);
    }

    public void writePage(int pageNum, Page page) {
        file.forceOpen();
        file.seek(pageNum * pageSize);
        file.write(page.getData());
    }
    
    @Override
    public void close() {
        file.close();
    }
}
