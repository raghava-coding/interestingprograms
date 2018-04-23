package objectpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

public class AbstractPool<T> implements Pool<T> {
    private final BlockingQueue<T> blockingQueue;
    private final Supplier<T> supplier;
    private final int poolSize;

    public AbstractPool(Supplier<T> supplier, int poolSize) {
        this.blockingQueue = new ArrayBlockingQueue<T>(poolSize);
        this.supplier = supplier;
        this.poolSize = poolSize;
    }

    private void lazyAdd(final int count){
        Thread thread = new Thread(()->{
            for(int i=0;i<count;i++){
                blockingQueue.offer(supplier.get());
            }
        })
    }
    @Override
    public void returnItem(T t) {

    }

    @Override
    public <T> T borrowItem() {
        return null;
    }

    @Override
    public int remainingItems() {
        return 0;
    }
}
