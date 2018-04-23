import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Generic & thread safe blocking queue implemented
 * using advanced locking techniques.
 */
public class BlockingQueue<T> {
    private final ArrayList<T> list;
    private final int queueSize;
    private final Lock reentrantLock;
    private final Condition notFullCondition;
    private final Condition notEmptyCondition;

    public BlockingQueue(int queueSize) {
        list = new ArrayList<>(queueSize);
        this.queueSize = queueSize;
        reentrantLock = new ReentrantLock();
        notFullCondition = reentrantLock.newCondition();
        notEmptyCondition = reentrantLock.newCondition();
    }

    public void offer(T t) throws InterruptedException {
        reentrantLock.lock();
        while (list.size() > queueSize) {
            notFullCondition.await();
        }
        list.add(t);
        notEmptyCondition.signal();
    }

    public <T> T poll() throws InterruptedException {
        reentrantLock.lock();
        try {
            while (list.size() == 0) {
                notEmptyCondition.await();
            }
            notFullCondition.signal();
            return (T) list.get(0);
        } finally {
            reentrantLock.unlock();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(2);
        for (int i = 0; i < 3; i++) {
            queue.offer(i);
        }
        for (int i = 0; i < 3; i++) {
            System.out.println(queue.poll());
        }
    }
}
