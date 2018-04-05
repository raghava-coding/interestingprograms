import java.util.HashMap;
import java.util.Map;

/*
                           Least Recently used Cache
1) Fixed Size: Cache needs to have some bounds to limit memory usages.
2) Fast Access: Cache Insert and lookup operation should be fast , preferably O(1) time.
3) Replacement of Entry in case , Memory Limit is reached:
   A cache should have efficient algorithm to evict the entry when memory is full.
 */
public class LRUCache {
    private Map<Integer, Entry> map;
    private static final int LRU_SIZE = 4;
    private Entry start, end;

    public LRUCache() {
        map = new HashMap<>();
    }

    public int getEntry(int key) {
        if (map.containsKey(key)) {
            Entry entry = map.get(key);
            removeNode(entry);
            addAtTop(entry);
            return entry.value;
        }
        return -1;
    }

    public void putEntry(int key, int value) {
        // Key Already Exist, just update the value and move it to top
        if (map.containsKey(key)) {
            Entry entry = map.get(key);
            entry.value = value;
            removeNode(entry);
            addAtTop(entry);
        } else {
            // We have reached maxium size so need to make room for new element.
            if (map.size() > LRU_SIZE) {
                map.remove(end.key);
                removeNode(end);
            }
            Entry entry = new Entry();
            entry.key = key;
            entry.value = value;
            addAtTop(entry);
            map.put(key, entry);
        }
    }

    private void addAtTop(Entry node) {
        if (start != null) {
            start.left = node;
            node.right = start;
        }
        //if size is 1
        if (end == null) {
            end = node;
        }
        start = node;
    }

    private void removeNode(Entry node) {
        if (node.left != null) {
            node.left.right = node.right;
        } else {
            start = node.right;
        }
        if (node.right != null) {
            node.right.left = node.left;
        } else {
            end = node.left;
        }
        node.left = null;
        node.right = null;
    }

    public static void main(String[] args) throws java.lang.Exception {
        // your code goes here
        LRUCache lrucache = new LRUCache();
        lrucache.putEntry(1, 1);
        lrucache.putEntry(10, 15);
        lrucache.putEntry(15, 10);
        lrucache.putEntry(10, 16);
        lrucache.putEntry(12, 15);
        lrucache.putEntry(18, 10);
        lrucache.putEntry(13, 16);

        System.out.println(lrucache.getEntry(1));
        System.out.println(lrucache.getEntry(10));
        System.out.println(lrucache.getEntry(15));

    }
}

class Entry {
    int key;
    int value;
    Entry left;
    Entry right;
}
