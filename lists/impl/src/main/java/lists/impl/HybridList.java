package lists.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A tabular list of linked dynamic arrays with fixed lengths. The idea is that 
 * it could hold more elements than an ArrayList, but still perform better than 
 * a LinkedList.
 * 
 * Benchmarking showed its performance is similar to an ArrayList's, but slower 
 * - specially for smaller row sizes. Curiously, on my machine appending is 
 * slightly faster, around 30%, even when the ArrayList has plenty extra 
 * capacity. That doesn't make much sense, so there could be a glitch on the 
 * benchmarks.
 * 
 * Memory-wise, a couple of tests showed that this toy list can somewhat hold 
 * more elements before the JVM runs out of heap space.
 * The first test consisted of using a small heap size (64m) and separately 
 * adding elements to each list until the heap bursts. For that test, a 
 * HybridList managed to hold 35% more elements than an ArrayList.
 * 
 * The second test simulates something closer to a real world workload. Instead 
 * of having one huge list with a bunch of numbers, we have multiple lists holding 
 * some dummy domain objects - around 300k each. We keep creating those lists 
 * until a pre-determined tipping point where we expect to run out of heap space
 * soon. After that point, we populate a new benchmark list with 1k dummy objects
 * and if that works we unreference the new list and let GC do its job. Then we 
 * repeat this process for 2k objects, then 3k and so on, until we run out of 
 * memory. That way we should be able to know how much effective heap space we 
 * managed to use, even with fragmentation.
 * However, the difference was insignificant: a HybridList was able to hold close
 * to 1% more elements before failing. Since we shouldn't expect the very same
 * memory layout between one test and the other, that difference is more than
 * likely to be due to that.
 * 
 * This was a fun exercise, but there isn't much point in taking this data 
 * structure seriously. It's main selling point turns out to be trading 
 * performance for some resilience on very specific high memory footprint 
 * workloads. 
 * You're much better off tackling that memory issue right away, since GC might 
 * be all over the place trying to free some space, taking a lot of CPU cycles 
 * in the process.
 * 
 * This implementation is, however, a good starting point for another fragmented
 * list with arbitrary row size.
 */
public class HybridList<T> implements ToyList<T> {

    private static final int DEFAULT_ROW_SIZE = 100000;

    // Let's use real implementations for benchmarking purposes.
    final LinkedList<ArrayList<T>> table;

    private final int rowSize;

    private int size;

    public HybridList() {
        this(DEFAULT_ROW_SIZE);
    }

    public HybridList(Collection<T> collection) {
        this();
        addAll(collection);
    }

    public HybridList(int rowSize) {
        table = new LinkedList<>();
        this.rowSize = rowSize;
        size = 0;
    }

    @Override
    public void add(T element) {
        if (table.isEmpty() || table.getLast().size() == rowSize) {
            if (!table.isEmpty()) {
                table.getLast().trimToSize();
            }
            // We expect a lot of data
            table.add(new ArrayList<>(rowSize));
        }
        table.getLast().add(element);
        size++;
    }

    @Override
    public void addAll(Collection<T> coll) {
        for (T value : coll) {
            add(value);
        }
    }

    @Override
    public void addAt(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    String.format("!(0 < %d <= %d)", index, size));
        }

        int[] position = translateToPosition(index);
        T shifted = value;
        int shiftTo = position[1];
        for (ListIterator<ArrayList<T>> it = table.listIterator(position[0]);
                it.hasNext();) {
            ArrayList<T> row = it.next();
            row.add(shiftTo, shifted);
            if (row.size() > rowSize) {
                shifted = row.remove(rowSize);
                shiftTo = 0;
            } else {
                shifted = null;
            }
        }
        boolean danglingElement = shifted != null;
        if (danglingElement) {
            ArrayList<T> row = new ArrayList<>(rowSize);
            row.add(shifted);
            table.add(row);
        }
        size++;
    }

    @Override
    public void clear() {
        table.clear();
        size = 0;
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        int[] position = translateToPosition(index);
        return table.get(position[0]).get(position[1]);
    }

    @Override
    public int indexOf(T value) {
        int index = 0;
        for (ArrayList<T> row : table) {
            for (T element : row) {
                if (Objects.equals(element, value)) {
                    return index;
                }
                index++;
            }
        }

        return -1;
    }

    @Override
    public T removeAt(int index) {
        checkIndex(index);
        int[] position = translateToPosition(index);
        
        T removed = null;
        for (ListIterator<ArrayList<T>> it = table.listIterator(position[0]);;) {
            ArrayList<T> row = it.next();
            // Doing this here saves us from traversing the table more than once
            if (removed == null) {
                removed = row.remove(position[1]);
            }
            if (!it.hasNext()) {
                break;
            } else {
                ArrayList<T> nextRow = it.next();
                row.add(nextRow.remove(0));
                if (!it.hasNext()) { 
                    break;
                } else {
                    // Rewind to fix the hole we left on nextRow
                    it.previous();
                }
            }
        }
        size--;
        if (table.getLast().isEmpty()) {
            table.pollLast();
        }
        return removed;
    }

    @Override
    public T set(int index, T value) {
        checkIndex(index);
        int[] position = translateToPosition(index);
        return table.get(position[0]).set(position[1], value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<T> asList() {
        return table.stream()
                .flatMap(ArrayList::stream)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return asList().toString();
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    String.format("!(0 < %d < %d)", index, size));
        }
    }

    private int[] translateToPosition(int index) {
        int row = index / rowSize;
        int column = index % rowSize;
        return new int[]{row, column};
    }

}
