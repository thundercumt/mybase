package io.mybase.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class StupidFileStorage extends HeapFileStorage {

    private File file;
    private Map<byte[], byte[]> data;

    public StupidFileStorage(File folder) {
        super(folder);
        file = new File(folder, "data.mbs");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void open() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            data = (Map<byte[], byte[]>) in.readObject();
        } catch (Exception e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(data);
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

}
