package io.mybase.storage.pf;

public class Page {
    private byte[] data;
    private boolean dirty;

    public Page(int pageSize) {
        this(pageSize, new byte[pageSize]);
    }

    public Page(int pageSize, byte[] bytes) {
        if (pageSize != bytes.length) {
            throw new IllegalArgumentException();
        }
        this.data = bytes;
        this.dirty = false;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
