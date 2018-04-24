package objectpool;

import java.util.function.Supplier;

public class SlowStringPool extends AbstractPool<String> {

    public SlowStringPool() {
        super(buildSupplier(), 10, 20, 99);
    }

    private static Supplier<String> buildSupplier() {
        return () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //nothing
            }
            return String.valueOf(System.currentTimeMillis());
        };
    }

    public SlowStringPool(Supplier<String> supplier) {
        super(supplier);
    }

    @Override
    public String handelDepleted(Supplier<String> supplier) throws PoolDepletionException {
        throw new PoolDepletionException();
    }

    public static void main(String[] args) throws InterruptedException, PoolDepletionException {
        SlowStringPool mypool = new SlowStringPool();
        Thread.sleep(2000);
        int i = 0;
        while (true) {
            long start = System.nanoTime();
            String s = mypool.borrowItem();
            long durn = System.nanoTime() - start;
            System.out.printf("%4d -> %s in %10.3fms -> %s\n", i++, s, durn / 1000000.0, mypool.toString());
            if (i % 2 == 0) {
                mypool.returnItem(s);
            }
        }
    }
}
