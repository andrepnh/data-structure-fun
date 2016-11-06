package lists.impl;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.junit.Assert.*;
import org.junit.Test;

public class HybridListTest {
    
    private HybridList<Integer> list;
    
    @Test
    public void addingOneIndexPastSizeShouldBehaveAsAdd() {
        SelfTestingToyList<Integer> selfTesting 
                = new SelfTestingToyList<>(new HybridList<>(3));
        
        selfTesting.add(5);
        selfTesting.addAt(1, Integer.MIN_VALUE);
    }
    
    @Test
    public void addingToAnEmptyListCreatesANewRow() {
        list = new HybridList<>();
        list.add(0);
        assertEquals(1, list.size());
        assertEquals(0, (int) list.get(0));
        assertEquals(1, list.table.size());
    }
    
    @Test
    public void addingToAListWithAFullLastRowCreatesANewRow() {
        int rowSize = 3;
        list = new HybridList<>(rowSize);
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        list.add(50);
        assertEquals(7, list.size());
        assertEquals(50, (int) list.get(6));
        assertEquals(3, list.table.size());
    }
    
    @Test
    public void removingFromAListWithASingleElementLastRowRemovesThatRow() {
        int rowSize = 3;
        list = new HybridList<>(rowSize);
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 47));
        assertEquals(47, (int) list.removeAt(6));
        assertEquals(6, list.size());
        assertEquals(2, list.table.size());
    }
    
    @Test
    public void clearingTheListClearsAllRows() {
        int rowSize = 3;
        list = new HybridList<>(rowSize);
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        list.clear();
        assertEquals(0, list.size());
        assertEquals(0, list.table.size());
    }
    
    @Test
    public void adddingToAPositionOfAFullRowRecursivelyShiftsElementsToTheNextRows() {
        int rowSize = 3;
        SelfTestingToyList<Integer> selfTesting 
                = new SelfTestingToyList<>(new HybridList<>(rowSize));
        
        selfTesting.addAll(IntStream.range(0, rowSize * 5 + 1)
                .boxed()
                .collect(Collectors.toList()));
        selfTesting.addAt(rowSize + 1, 55);
    }
    
    @Test
    public void removingFromAPositionOfAFullRowRecursivelyShiftsElementsBackFromTheNextRows() {
        int rowSize = 3;
        SelfTestingToyList<Integer> selfTesting = 
                new SelfTestingToyList<>(new HybridList<>(rowSize));
        
        selfTesting.addAll(IntStream.range(0, rowSize * 5 + 1)
                .boxed()
                .collect(Collectors.toList()));
        selfTesting.removeAt(rowSize + 1);
    } 
    
}
