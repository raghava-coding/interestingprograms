package objectpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractPool<T> implements Pool<T> {
    private final BlockingQueue<T> blockingQueue;
    private final Supplier<T> supplier;
    private final int minIdle;
    private final int maxIdle;
    private final long maxWait;

    protected AbstractPool(Supplier<T> supplier) {
        this(supplier, 1, Integer.MAX_VALUE, 0);
    }

    protected AbstractPool(Supplier<T> supplier, int minIdle, int maxIdle, long maxWait) {
        if (minIdle < 0 || maxIdle <= minIdle) {
            throw new IllegalArgumentException("minIdel must be non-negative and also strictly less than maxIdle");
        }
        this.blockingQueue = new LinkedBlockingDeque<>(maxIdle);
        this.supplier = supplier;
        this.minIdle = minIdle;
        this.maxIdle = maxIdle;
        this.maxWait = maxWait;

        lazyAdd(minIdle);
    }

    /**
     * lazyAdd will start a background thread to add instances to the queue (if
     * possible). The cost of creating a thread is assumed to be insignificant
     * compared to the cost of creating the instance.
     *
     * @param count the number of items to add to the pool
     */
    private void lazyAdd(final int count) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < count; i++) {
                if (!blockingQueue.offer(supplier.get())) {
                    return;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void returnItem(T t) {
        if (t == null) {
            return;
        }
        blockingQueue.offer(t);
    }

    @Override
    public T borrowItem() throws InterruptedException, PoolDepletionException {
        T t = blockingQueue.poll();
        //if pool size is less than allowed size
        if (blockingQueue.size() < minIdle) {
            lazyAdd(1);
        }
        if (t != null) {
            return t;
        }
         /*Block till something is available.
         The lazy add above may be the instance we pull out!
         It is also possible that some other thread may steal the one we
         added!*/
        if (maxWait > 0) {
            t = blockingQueue.poll(maxWait, TimeUnit.MILLISECONDS);
        }

        if (t != null) {
            return t;
        }

        return handelDepleted(supplier);
    }

    /**
     * What to do if there is nothing to return within the expected time. options are
     * to create an instance manually, or alternatively throw an exception.
     * returning null would be a bad idea though.
     *
     * @param supplier
     * @return
     */
    public abstract T handelDepleted(Supplier<T> supplier) throws PoolDepletionException;

    @Override
    public String toString() {
        return String.format("Pool with %d items and min %d, max %d wait %dms", blockingQueue.size(), minIdle, maxIdle,
                maxWait);
    }
}
