package lists.impl;

import java.util.Random;
import lists.impl.DynamicArray;
import org.junit.Test;

public class DynamicArrayTest {
    
    @Test
    public void testAllOperationsRandomly() {
        Random rng = new Random();
        new ToyListTestWithRandomCalls()
                .test(new DynamicArray<Long>(), () -> rng.nextLong());
    }
    
}
