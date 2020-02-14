package io.mybase.storage.pf;

import java.io.Closeable;
import java.io.File;

public class PagedFile implements Closeable {

    private FileIO file;
    private int pageSize;

    PagedFile(File file, int pageSize) {
        this.file = new FileIO(file);
        this.pageSize = pageSize;
    }

    public Page readPage(int pageNum) {
        byte[] bytes = new byte[pageSize];
        file.seek(pageNum * pageSize);
        file.read(bytes);
        return new Page(pageSize, bytes);
    }

    void open() {
        file.open();
    }

    public void writePage(int pageNum, Page page) {
        file.seek(pageNum * pageSize);
        file.write(page.getData());
    }

    @Override
    public void close() {
        file.close();
    }

    public int getPageSize() {
        return pageSize;
    }
}
