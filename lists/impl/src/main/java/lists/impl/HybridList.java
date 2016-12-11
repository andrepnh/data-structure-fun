package lists.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A tabular list of linked dynamic arrays. The idea is that it could hold more
 * elements than an ArrayList, but still perform better then a LinkedList.
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
