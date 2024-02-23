package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BaseTableTest {

    @Test
    public void testPassBlankDataColumnToGetData() {
        assertThrows(IllegalArgumentException.class, () -> new BaseTable().getColumnData(""));
    }

    @Test
    public void testPassNonExistingColumnToGetData() {
        assertThrows(IllegalArgumentException.class, () -> new BaseTable().getColumnData(""));
    }

    @Test
    public void testGetColumnData() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        table.addData(new String[]{"table", "Bob", "Kok"});
        table.addData(new String[]{"table", "Bob", "Kok"});

        assertEquals(Set.of("Bob"), table.getColumnData("Bob"));
    }

    @Test
    public void testGetRowCount() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        table.addData(new String[]{"table", "Bob", "Kok"});
        table.addData(new String[]{"table", "Bob", "Kok"});

        assertEquals(2, table.getRowsCount());
    }

    @Test
    public void testGetColumnNames() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        String[] titles = new String[]{"table", "Bob", "Kok"};
        table.addData(titles);

        assertEquals(List.of(titles), table.getColumnNames());
    }

    @Test
    public void testAddNullData() {
        assertThrows(IllegalArgumentException.class, () -> new BaseTable().addData(null));
    }

    @Test
    public void testAddMatchingTitles() {
        assertThrows(CsvDataNotCorrectException.class,
                () -> new BaseTable().addData(new String[]{"table", "Bob", "Kok", "Bob"}));
    }

    @Test
    public void testAddDifferentCountColumns() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        table.addData(new String[]{"table", "Bob", "Kok"});
        assertThrows(CsvDataNotCorrectException.class,
                () -> table.addData(new String[]{"table", "Bob", "Kok", "aa"}));
    }

    @Test
    public void testAddColumns() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        table.addData(new String[]{"table", "Bob", "Kok"});
        table.addData(new String[]{"table", "Bob", "Kok"});
        assertEquals(2, table.getRowsCount());
    }
}
