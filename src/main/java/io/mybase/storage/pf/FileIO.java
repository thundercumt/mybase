package io.mybase.storage.pf;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicBoolean;

import io.mybase.StorageException;

public class FileIO implements Closeable {
    private File file;
    private RandomAccessFile raf;
    private AtomicBoolean opened = new AtomicBoolean(false);

    public FileIO(File file) {
        this.file = file;
        this.opened.set(false);
    }

    public void open() {
        if (opened.get()) {
            return;
        }
        try {
            this.raf = new RandomAccessFile(file, "rwd");
            while(!opened.compareAndSet(false, true));
        } catch (FileNotFoundException e) {
            throw new StorageException("not found", e);
        }
    }

    public void close() {
        if (!opened.get()) {
            return;
        }
        try {
            raf.close();
            while(!opened.compareAndSet(true, false));
        } catch (IOException e) {
            throw new StorageException("close failed", e);
        }
    }

    public void seek(long pos) {
        if (!opened.get()) {
            throw new IllegalStateException("not opened");
        }
        try {
            raf.seek(pos);
        } catch (IOException e) {
            throw new StorageException("seek failed", e);
        }
    }

    public int read(byte[] bytes, int offset, int size) {
        if (!opened.get()) {
            throw new IllegalStateException("not opened");
        }
        try {
            return raf.read(bytes, offset, size);
        } catch (IOException e) {
            throw new StorageException("read failed", e);
        }
    }

    public int read(byte[] bytes) {
        return read(bytes, 0, bytes.length);
    }

    public void write(byte[] bytes, int offset, int size) {
        if (!opened.get()) {
            throw new IllegalStateException("not opened");
        }
        try {
            raf.write(bytes, offset, size);
        } catch (IOException e) {
            throw new StorageException("write failed", e);
        }
    }

    public void write(byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    public void truncate(long size) {
        if (!opened.get()) {
            throw new IllegalStateException("not opened");
        }
        try {
            raf.setLength(size);
        } catch (IOException e) {
            throw new StorageException("setLength failed", e);
        }
    }

    public void flush() {
        if (!opened.get()) {
            throw new IllegalStateException("not opened");
        }
        try {
            raf.getFD().sync();
        } catch (IOException e) {
            throw new StorageException("flush all failed", e);
        }
    }

    public void delete() {
        if (opened.get()) {
            try {
                raf.close();
            } catch (IOException e) {
                throw new StorageException("close before remove failed", e);
            }
        }
        if (!file.exists()) {
            return;
        }

        if (file.canWrite()) {
            boolean done = file.delete();
            if (!done && file.exists()) {
                file.deleteOnExit();
            }
        } else {
            throw new StorageException("cannot delete");
        }
    }

    File getFile() {
        return file;
    }

}
