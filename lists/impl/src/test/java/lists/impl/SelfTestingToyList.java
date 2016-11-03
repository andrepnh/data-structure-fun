package lists.impl;

import io.atlassian.fugue.Either;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lists.impl.ToyList;
import static org.junit.Assert.*;

public class SelfTestingToyList<T> implements ToyList<T> {
    
    private final ToyList<T> underTest;
    
    private final List<T> reference;
    
    public SelfTestingToyList(ToyList<T> emptyList) {
        underTest = emptyList;
        reference = new ArrayList<>();
        assertEqualsToReference();
    }

    @Override
    public void add(T element) {
        underTest.add(element);
        reference.add(element);
        assertEqualsToReference();
    }

    @Override
    public void addAll(Collection<T> coll) {
        underTest.addAll(coll);
        reference.addAll(coll);
        assertEqualsToReference();
    }

    @Override
    public void addAt(int index, T value) {
        Optional<Exception> underTestEx 
                = callAndCatch(() -> underTest.addAt(index, value));
        Optional<Exception> referenceEx 
                = callAndCatch(() -> reference.add(index, value));
        
        assertEmptyOrIndexOutOfBounds(
                String.format("addAt(%d, %s) mismatch for state %s", 
                        index, value, underTest),
                referenceEx,
                underTestEx);
        
        assertEqualsToReference();
    }

    @Override
    public void clear() {
        underTest.clear();
        reference.clear();
        assertEqualsToReference();
    }

    @Override
    public T get(int index) {
        Either<Exception, T> referenceResult 
                = callAndCatch(() -> reference.get(index));
        Either<Exception, T> underTestResult 
                = callAndCatch(() -> underTest.get(index));
        
        assertEqualsOrIndexOutOfBounds(
                String.format("get(%d) mismatch for state %s",
                        index, underTest),
                referenceResult,
                underTestResult);
        
        return null;
    }

    @Override
    public int indexOf(T value) {
        int ret = reference.indexOf(value);
        assertEquals(
                String.format("indexOf(%s) mismatch for state %s",
                        value, underTest),
                ret, underTest.indexOf(value));
        return ret;
    }

    @Override
    public T removeAt(int index) {
        Either<Exception, T> referenceResult 
                = callAndCatch(() -> reference.remove(index));
        Either<Exception, T> underTestResult 
                = callAndCatch(() -> underTest.removeAt(index));
        
        assertEqualsOrIndexOutOfBounds(
                String.format("removeAt(%d) mismatch for state %s",
                        index, underTest),
                referenceResult,
                underTestResult);
        
        return null;
    }

    @Override
    public T set(int index, T value) {
        Either<Exception, T> referenceResult 
                = callAndCatch(() -> reference.set(index, value));
        Either<Exception, T> underTestResult 
                = callAndCatch(() -> underTest.set(index, value));
        
        assertEqualsOrIndexOutOfBounds(
                String.format("set(%d, %s) mismatch for state %s",
                        index, value, underTest),
                referenceResult,
                underTestResult);
        
        return null;
    }

    @Override
    public int size() {
        int ret = reference.size();
        assertEquals(
                String.format("size() mismatch for state %s", underTest),
                ret, 
                underTest.size());
        return ret;
    }

    @Override
    public List<T> asList() {
        return new ArrayList<>(reference);
    }

    private void assertEqualsToReference() {
        assertEquals(reference, underTest.asList());
    }
    
    private Optional<Exception> callAndCatch(Runnable operation) {
        try {
            operation.run();
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }
    
    private <T> Either<Exception, T> callAndCatch(Supplier<T> operation) {
        try {
            return Either.right(operation.get());
        } catch (Exception e) {
            return Either.left(e);
        }
    }

    /**
     * Some Lists' methods throw IndexOutOfBoundsException, others throw
     * ArrayIndexOutOfBoundsException. Here we consider that those two are
     * are the same.
     */
    private void assertEmptyOrIndexOutOfBounds(String message, 
            Optional<Exception> expected, Optional<Exception> actual) {
        assertEquals(message, 
                expected.map(ex -> ex instanceof IndexOutOfBoundsException
                    ? IndexOutOfBoundsException.class
                    : ex.getClass()),
                actual.map(ex -> ex instanceof IndexOutOfBoundsException
                    ? IndexOutOfBoundsException.class
                    : ex.getClass()));
    }

    /**
     * Some Lists' methods throw IndexOutOfBoundsException, others throw
     * ArrayIndexOutOfBoundsException. Here we consider that those two are
     * are the same.
     */
    private void assertEqualsOrIndexOutOfBounds(String message, 
            Either<Exception, T> expected, 
            Either<Exception, T> actual) {
        assertEquals(message, 
                expected.leftMap(ex -> ex instanceof IndexOutOfBoundsException
                    ? IndexOutOfBoundsException.class
                    : ex.getClass()),
                actual.leftMap(ex -> ex instanceof IndexOutOfBoundsException
                    ? IndexOutOfBoundsException.class
                    : ex.getClass()));
    }
    
}
