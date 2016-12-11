package com.github.andrepnh.hybrid.list.benchmarks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import lists.impl.HybridList;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Benchmarks for HybridList's addAt method compared to its java.util counterparts.
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, batchSize = 10000)
@Measurement(iterations = 10, batchSize = 10000)
@BenchmarkMode(Mode.SingleShotTime)
public class AddAtBenchmark {

    @State(Scope.Thread)
    public static class DefaultArrayList {
        final ArrayList<Integer> list = new ArrayList<>(100000);

        @Setup(Level.Iteration)
        public void setup() {
            list.clear();
        }
    }
    
    @State(Scope.Thread)
    public static class DefaultLinkedList {
        final LinkedList<Integer> list = new LinkedList<>();

        @Setup(Level.Iteration)
        public void setup() {
            list.clear();
        }
    }
    
    @State(Scope.Thread)
    public static class LargeRowSizeHybridList {
        final HybridList<Integer> list = new HybridList<>(10000);

        @Setup(Level.Iteration)
        public void setup() {
            list.clear();
        }
    }

    @State(Scope.Thread)
    public static class MediumRowSizeHybridList {
        final HybridList<Integer> list = new HybridList<>(1000);

        @Setup(Level.Iteration)
        public void setup() {
            list.clear();
        }
    }

    @State(Scope.Thread)
    public static class SmallRowSizeHybridList {
        final HybridList<Integer> list = new HybridList<>(100);

        @Setup(Level.Iteration)
        public void setup() {
            list.clear();
        }
    }

    @Benchmark
    public void addAtBeginningArrayListBenchmark(DefaultArrayList state) {
        state.list.add(0, 42);
    }
    
    @Benchmark
    public void addAtBeginningLinkedListBenchmark(DefaultLinkedList state) {
        state.list.add(0, 42);
    }

    @Benchmark
    public void addAtBeginningLargeRowSizeHybridListBenchmark(LargeRowSizeHybridList state) {
        state.list.addAt(0, 42);
    }
    
    @Benchmark
    public void addAtBeginningMediumRowSizeHybridListBenchmark(MediumRowSizeHybridList state) {
        state.list.addAt(0, 42);
    }

    @Benchmark
    public void addAtBeginningSmallRowSizeHybridListBenchmark(
            SmallRowSizeHybridList state) {
        state.list.addAt(0, 42);
    }

    @Benchmark
    public void addAtMiddleArrayListBenchmark(DefaultArrayList state) {
        state.list.add(state.list.size() / 2, 42);
    }
    
    @Benchmark
    public void addAtMiddleLinkedListBenchmark(DefaultLinkedList state) {
        state.list.add(state.list.size() / 2, 42);
    }
    
    @Benchmark
    public void addAtMiddleLargeRowSizeHybridListBenchmark(LargeRowSizeHybridList state) {
        state.list.addAt(state.list.size() / 2, 42);
    }

    @Benchmark
    public void addAtMiddleMediumRowSizeHybridListBenchmark(MediumRowSizeHybridList state) {
        state.list.addAt(state.list.size() / 2, 42);
    }

    @Benchmark
    public void addAtMiddleSmallRowSizeHybridListBenchmark(
            SmallRowSizeHybridList state) {
        state.list.addAt(state.list.size() / 2, 42);
    }
    
    @Benchmark
    public void addAtEndArrayListBenchmark(DefaultArrayList state) {
        state.list.add(42);
    }
    
    @Benchmark
    public void addAtEndLinkedListBenchmark(DefaultLinkedList state) {
        state.list.add(42);
    }
    
    @Benchmark
    public void addAtEndLargeRowSizeHybridListBenchmark(LargeRowSizeHybridList state) {
        state.list.add(42);
    }

    @Benchmark
    public void addAtEndMediumRowSizeHybridListBenchmark(MediumRowSizeHybridList state) {
        state.list.add(42);
    }

    @Benchmark
    public void addAtEndSmallRowSizeHybridListBenchmark(
            SmallRowSizeHybridList state) {
        state.list.add(42);
    }
}
