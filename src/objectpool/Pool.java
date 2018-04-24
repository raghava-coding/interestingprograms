package objectpool;

/**
 Generic & thread-safe pool
**/
public interface Pool<T> {
    void returnItem(T t);

    <T> T borrowItem() throws InterruptedException, PoolDepletionException;
}
