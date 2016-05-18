package au.edu.unimelb.utils;

/**
 * Inspired by circular buffer.
 *
 * CircularArray ensures that only n=capacity elements can exist in the array.
 * When the array is full and someone try to add new value, then the oldest
 * value will be dropped, the new value will be added.
 *
 * Created by HeguangMiao on 3/05/2016.
 */
public class CircularArray<T> {
    private Object[] arr;
    private int head = 0;
    private int tail = -1;
    private int size = 0;
    private int capacity;

    public CircularArray(int capacity) {
        this.arr = new Object[capacity];
        this.capacity = capacity;
    }

    private T getElement(int realIndex) {
        if (realIndex >= capacity || realIndex < 0) {
            return null;
        }
        return (T)arr[realIndex];
    }

    public T get(int index) {
        if(index >= capacity || index < 0) {
            return null;
        }
        return getElement((index + head) % capacity);
    }

    public T getOldest() {
        return getElement(head);
    }

    public T getNewest() {
        return getElement(tail);
    }

    public void add(T obj) {
        int pt = (tail + 1) % capacity;
        if(pt == head && tail != -1) {
            // full, remove oldest one
            head = (head + 1) % capacity;
        }
        arr[pt] = obj;
        tail = pt;
        if (size + 1 <= capacity) {
            size++;
        }
    }

    public Object[] allElements() {
        Object[] cp = new Object[size];
        for (int i = 0; i < size; i++) {
            // we need to keep the order
            cp[i] = get(i);
        }
        return cp;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }
}
