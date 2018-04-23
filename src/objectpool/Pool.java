package objectpool;

public interface Pool<T> {
    void returnItem(T t);

    <T> T borrowItem();

    int remainingItems();
}
