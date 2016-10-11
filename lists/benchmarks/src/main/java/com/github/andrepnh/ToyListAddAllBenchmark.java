package com.github.andrepnh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;
import lists.impl.BulkOptimizedDynamicArray;
import lists.impl.DynamicArray;
import lists.impl.ToyList;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class ToyListAddAllBenchmark {
    
    @State(Scope.Thread)
    public static class BigCollection {
        public Collection<Integer> value = new Random()
            .ints(100000)
            .boxed()
            .collect(Collectors.toList());
        
        /**
         * Had a theory that perhaps (part of) the bulk optimized version's
         * poor performance could be caused by a funky list created by 
         * Collectors.toList(). 
         * It's not.
         */
        public ArrayList<Integer> valueAsArrayList = new ArrayList<>(value);
    }
    
    @Benchmark
    public ToyList<Integer> benchmarkDynamicArray(BigCollection coll) {
        DynamicArray<Integer> darray = new DynamicArray<>();
        darray.addAll(coll.value);
        return darray;
    }
    
    /**
     * I could swear this version would be at least as fast as the original toy
     * list, but it's actually much slower. Two orders of magnitude slower on
     * my machine. There's something deeper going on here.
     * Anyway, serves to prove that guessing performance is tricky business. 
     * Just measure it.
     */
    @Benchmark
    public ToyList<Integer> benchmarkBulkOptimizedDynamicArray(BigCollection coll) {
        BulkOptimizedDynamicArray<Integer> darray = new BulkOptimizedDynamicArray<>();
        darray.addAll(coll.value);
        return darray;
    }
    
    @Benchmark
    public ToyList<Integer> benchmarkDynamicArrayConsumingArrayList(BigCollection coll) {
        DynamicArray<Integer> darray = new DynamicArray<>();
        darray.addAll(coll.valueAsArrayList);
        return darray;
    }
    
    @Benchmark
    public ToyList<Integer> benchmarkBulkOptimizedDynamicArrayConsumingArrayList(BigCollection coll) {
        BulkOptimizedDynamicArray<Integer> darray = new BulkOptimizedDynamicArray<>();
        darray.addAll(coll.valueAsArrayList);
        return darray;
    }

}
