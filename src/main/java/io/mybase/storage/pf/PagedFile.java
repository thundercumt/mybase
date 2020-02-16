package io.mybase.storage.pf;

import java.io.Closeable;
import java.io.File;

public class PagedFile implements Closeable {

    private FileIO file;
    private int pageSize;

    public PagedFile(File file, int pageSize) {
        this.file = new FileIO(file);
        this.pageSize = pageSize;
    }

    public Page readPage(int pageNum) {
        file.seek(pageNum * pageSize);
        byte[] bytes = new byte[pageSize];
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

    public long getFileLength() {
        return file.length();
    }

    public int getPageCount() {
        return (int) file.length() / pageSize;
    }
}
