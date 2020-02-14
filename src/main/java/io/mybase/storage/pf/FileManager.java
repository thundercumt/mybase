package io.mybase.storage.pf;

import java.io.File;

public class FileManager {
    private static final int DEFAULT_PAGE_SIZE = 16 * 1024;

    private final int pageSize;

    public FileManager() {
        this(DEFAULT_PAGE_SIZE);
    }

    public FileManager(int pageSize) {
        this.pageSize = pageSize;
    }

    public PagedFile open(File file) {
        PagedFile f = new PagedFile(file, pageSize);
        f.open();
        return f;
    }
}
