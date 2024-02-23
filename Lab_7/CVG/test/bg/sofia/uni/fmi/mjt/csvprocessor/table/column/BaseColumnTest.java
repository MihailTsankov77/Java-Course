package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BaseColumnTest {

    @Test
    public void testAddBlankData() {
        assertThrows(IllegalArgumentException.class, () -> new BaseColumn().addData(""));
    }

    @Test
    public void testAddData() {
        String data = "Blob";

        Column column = new BaseColumn();
        column.addData(data);

        assertEquals(Set.of(data), column.getData());
    }


    @Test
    public void testAddDataToExistingSet() {
        String data = "Blob";
        Set<String> existingData = new TreeSet<>();
        existingData.add("ba");
        existingData.add("fa");
        existingData.add("ga");
        Column column = new BaseColumn(existingData);
        column.addData(data);

        existingData.add(data);
        assertEquals(existingData, column.getData());
    }

    @Test
    public void testAddTheSameDataTwice() {
        String data = "Blob";
        Set<String> existingData = new TreeSet<>();

        existingData.add(data);
        Column column = new BaseColumn(existingData);
        column.addData(data);

        assertEquals(existingData, column.getData());
    }

}
