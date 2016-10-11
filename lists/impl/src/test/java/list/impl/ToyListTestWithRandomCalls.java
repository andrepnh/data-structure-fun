package list.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lists.impl.ToyList;

public class ToyListTestWithRandomCalls {

    public <T> void test(ToyList<T> underTest, Supplier<T> randomElementSupplier) {
        SelfTestingToyList<T> selfTestingList = new SelfTestingToyList<>(underTest);
        RandomArgsToyListAdpater<T> argless = new RandomArgsToyListAdpater(
                selfTestingList, randomElementSupplier);
        List<Runnable> operations = listOperations(argless);
        Random rng = new Random();
        for (int i = 0; i < 5000; i++) {
            Runnable op = operations.get(rng.nextInt(operations.size()));
            op.run();
        }
    }
    
    private <T> List<Runnable> listOperations(RandomArgsToyListAdpater<T> argless) {
        return Arrays.asList(
                argless::add,
                argless::addAll,
                argless::addAt,
                argless::clear,
                argless::set,
                argless::get,
                argless::indexOf,
                argless::removeAt,
                argless::size
        );
    }
}

class RandomArgsToyListAdpater<T> {
    
    private final Random rng = new Random();
    
    private final Supplier<T> randomElementSupplier;
    
    private final ToyList<T> instance;

    public RandomArgsToyListAdpater(
            ToyList<T> instance, Supplier<T> randomElementSupplier) {
        this.randomElementSupplier = randomElementSupplier;
        this.instance = instance;
    }
    
    public void add() {
        instance.add(randomElementSupplier.get());
    }

    public void addAll() {
        List<T> randomCollection = Stream.generate(randomElementSupplier::get)
                .limit(rng.nextInt(1000))
                .collect(Collectors.toList());
        instance.addAll(randomCollection);
    }

    public void addAt() {
        int index = generatePossiblyOutOfBoundsIndex();
        instance.addAt(index, randomElementSupplier.get());
    }

    public void clear() {
        instance.clear();
    }

    public T get() {
        int index = generatePossiblyOutOfBoundsIndex();
        return instance.get(index);
    }

    public int indexOf() {
        boolean shouldContain = instance.size() != 0 && rng.nextInt(100) < 75;
        T value = shouldContain
                ? instance.get(rng.nextInt(instance.size()))
                : randomElementSupplier.get();
        return instance.indexOf(value);
    }

    public T removeAt() {
        return instance.removeAt(generatePossiblyOutOfBoundsIndex());
    }

    public void set() {
        instance.set(generatePossiblyOutOfBoundsIndex(), 
                randomElementSupplier.get());
    }

    public int size() {
        return instance.size();
    }
    
    private int generatePossiblyOutOfBoundsIndex() {
        double around15PercentOfSize = (instance.size() + 1) * 0.15;
        int floor = (int) -Math.floor(around15PercentOfSize),
            ceil =  instance.size() + ((int) Math.ceil(around15PercentOfSize));
        return rng.ints(floor, ceil)
                .findFirst()
                .orElse(rng.nextInt());
    }
}