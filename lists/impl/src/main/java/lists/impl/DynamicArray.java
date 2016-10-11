package lists.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DynamicArray<T> implements ToyList<T> {    
    
    protected static final double GROWTH_FACTOR = 2; 
    
    protected Object[] array;
    
    protected int size;
    
    public DynamicArray() {
        this(32);
    }
    
    public DynamicArray(int capacity) {
        array = new Object[capacity];
    }
    
    @Override
    public void add(T element) {
        ensureCapacity();
        array[size++] = element;
    }
    
    @Override
    public void addAll(Collection<T> coll) {
        // A tradeoff here. If "coll" is big enough, it may trigger multiple
        // internal array resizing. What if we ensured capacity based on "coll"
        // size? The problem is that coll's size may no be trivial
        for (T element : coll) {
            add(element);
        }
    }
    
    @Override
    public int indexOf(T value) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public void addAt(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    String.format("0 <= i <= %d failed for i = %d", size, index));
        }
        ensureCapacity();
        for (int i = size; i > index; i--) {
            array[i] = array[i - 1];
        }
        array[index] = value;
        size++;
    }
    
    @Override
    public T set(int index, T value) {
        checkIndex(index);
        T old = (T) array[index];
        array[index] = value;
        return old;
    }
    
    @Override
    public T get(int index) {
        checkIndex(index);
        return (T) array[index];
    }
    
    @Override
    public T removeAt(int index) {
        checkIndex(index);
        T removed = (T) array[index];
        if (index != size - 1) {
            for (int i = index; i < size - 1; i++) {
                array[i] = array[i + 1];
            }
        }
        size--;
        array[size] = null;
        return removed;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            array[size] = null;
        }
        size = 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    String.format("0 <= i < %d failed for i = %d", size, index));
        }
    }
    
    @Override
    public int size() {
        return size;
    }

    private void ensureCapacity() {
        if (array.length == size) {
            array = Arrays.copyOf(array, (int)(array.length * GROWTH_FACTOR));
        }
    }

    @Override
    public List<T> asList() {
        return Arrays.asList((T[]) Arrays.copyOfRange(array, 0, size));
    }

    @Override
    public String toString() {
        return asList().toString();
    }
    
}
