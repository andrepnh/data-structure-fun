package lists.impl;

import java.util.Collection;
import java.util.List;

public interface ToyList<T> {

    void add(T element);

    void addAll(Collection<T> coll);

    void addAt(int index, T value);

    void clear();

    T get(int index);

    int indexOf(T value);

    T removeAt(int index);

    T set(int index, T value);

    int size();
    
    List<T> asList();
}
