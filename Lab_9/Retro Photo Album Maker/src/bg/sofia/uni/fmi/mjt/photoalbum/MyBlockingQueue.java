package bg.sofia.uni.fmi.mjt.photoalbum;

import java.util.LinkedList;
import java.util.Queue;

public class MyBlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized void add(T item) {
        queue.add(item);
        this.notify();
    }

    public synchronized T get() {
        while (queue.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return queue.remove();
    }
}
