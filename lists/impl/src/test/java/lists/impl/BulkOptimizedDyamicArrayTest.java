package lists.impl;

import java.util.Random;
import lists.impl.BulkOptimizedDynamicArray;
import org.junit.Test;

public class BulkOptimizedDyamicArrayTest {
    
    @Test
    public void testAllOperationsRandomly() {
        Random rng = new Random();
        new ToyListTestWithRandomCalls()
                .test(new BulkOptimizedDynamicArray<Long>(), () -> rng.nextLong());
    }
    
}
