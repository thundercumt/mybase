package io.mybase.buffer;

import io.mybase.CapacityException;

@SuppressWarnings("unchecked")
public class BufList<T> {
    private static final int NA = -1;
    private int capacity;
    private Object[] bufTable;
    private int free;
    private int totalFree;
    private int first;
    private int last;

    private class ListNode {
        int prev;
        int next;
        int pinCount;
        T value;

        ListNode() {
            this(NA, NA, NA, null);
        }

        ListNode(int prev, int next, int pinCount, T value) {
            this.prev = prev;
            this.next = next;
            this.pinCount = pinCount;
            this.value = value;
        }
    }

    public BufList(int capacity) {
        this.capacity = capacity;
        bufTable = new Object[capacity];
        for (int i = 0; i < capacity; ++i) {
            ListNode node = new ListNode();
            bufTable[i] = node;
            node.next = i + 1;
            node.prev = i - 1;
        }
        free = 0;
        totalFree = capacity;
        first = last = NA;
        get(0).prev = NA;
        get(capacity - 1).next = NA;
    }

    private ListNode get(int number) {
        if (number < 0 || number >= capacity)
            throw new IllegalArgumentException("invalid number");
        return (ListNode) bufTable[number];
    }

    private int unlinkFree() {
        if (free == NA) {
            throw new CapacityException();
        }
        int ret = free;
        ListNode node = get(free);
        free = node.next;
        if (node.next != NA) {
            ListNode next = get(node.next);
            next.prev = node.prev;
        }
        node.prev = NA;
        node.next = NA;
        return ret;
    }

    void linkFree(int slot) {
        ListNode node = get(slot);

        node.next = free;
        free = slot;
        if (node.next != NA) {
            ListNode next = get(node.next);
            node.prev = next.prev;
            next.prev = slot;
        } else {
            node.prev = NA;
        }
    }

    private void unlinkUsed(int slot) {
        ListNode node = get(slot);

        if (first == slot) {
            first = node.next;
        }

        if (last == slot) {
            last = node.prev;
        }

        if (node.next != NA) {
            get(node.next).prev = node.prev;
        }

        if (node.prev != NA) {
            get(node.prev).next = node.next;
        }

        node.pinCount = 0;
        node.prev = NA;
        node.next = NA;
    }

    private void linkUsed(int slot) {
        ListNode node = get(slot);
        node.next = first;
        first = slot;
        if (node.next != NA) {
            ListNode next = get(node.next);
            node.prev = next.prev;
            next.prev = slot;
        } else {
            node.prev = NA;
        }
    }

    int getCapacity() {
        return capacity;
    }

    public int add(T value) {
        int slot = unlinkFree();
        get(slot).value = value;
        linkUsed(slot);
        --totalFree;
        return slot;
    }

    public T remove(int slot) {
        ListNode node = get(slot);
        unlinkUsed(slot);
        linkFree(slot);
        T old = node.value;
        node.value = null;
        ++totalFree;
        return old;
    }

    public T getValue(int slot) {
        return get(slot).value;
    }

    public T setValue(int slot, T value) {
        ListNode node = get(slot);
        T old = node.value;
        node.value = value;
        return old;
    }

    public int getAvailable() {
        return totalFree;
    }
}
