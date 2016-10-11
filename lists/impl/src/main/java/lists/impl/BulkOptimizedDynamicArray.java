package lists.impl;

import java.util.Arrays;
import java.util.Collection;
import static lists.impl.DynamicArray.GROWTH_FACTOR;

public class BulkOptimizedDynamicArray<T> extends DynamicArray<T> {

    @Override
    public void addAll(Collection<T> coll) {
        ensureCapacity(coll.size());
        for (T element: coll) {
            array[size++] = element;
        }
    }
    
    private void ensureCapacity(int extras) {
        int newSize = size + extras;
        if (newSize >= array.length) {
            // Maintaining growth ratio
            double timesToApplyGrowthFactor 
                    = Math.ceil(((double) newSize) / GROWTH_FACTOR * array.length);
            array = Arrays.copyOf(array, 
                    (int)(array.length * timesToApplyGrowthFactor * GROWTH_FACTOR));
        }
    }
}
