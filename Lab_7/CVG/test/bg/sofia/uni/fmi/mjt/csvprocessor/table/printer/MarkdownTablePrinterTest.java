package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkdownTablePrinterTest {

    static Table SmallTable;


    @BeforeAll
    public static void createTable() throws CsvDataNotCorrectException {
        SmallTable = new BaseTable();

        SmallTable.addData(new String[]{"1", "abba", "gogo", "NoNon"});
    }

    @Test
    public void testNoAlignment() {
        assertEquals(List.of(new String[]{
                "| 1   | abba | gogo | NoNon |",
                "| --- | ---- | ---- | ----- |"
        }), new MarkdownTablePrinter().printTable(SmallTable, ColumnAlignment.NOALIGNMENT,
                ColumnAlignment.NOALIGNMENT, ColumnAlignment.NOALIGNMENT, ColumnAlignment.NOALIGNMENT));
    }

    @Test
    public void testLeftAlignment() {
        assertEquals(List.of(new String[]{
                "| 1   | abba | gogo | NoNon |",
                "| :-- | :--- | :--- | :---- |"
        }), new MarkdownTablePrinter().printTable(SmallTable, ColumnAlignment.LEFT, ColumnAlignment.LEFT,
                ColumnAlignment.LEFT, ColumnAlignment.LEFT));
    }

    @Test
    public void testRightAlignment() {
        assertEquals(List.of(new String[]{
                "| 1   | abba | gogo | NoNon |",
                "| --: | ---: | ---: | ----: |"
        }), new MarkdownTablePrinter().printTable(SmallTable, ColumnAlignment.RIGHT,
                ColumnAlignment.RIGHT, ColumnAlignment.RIGHT, ColumnAlignment.RIGHT));
    }

    @Test
    public void testCenterAlignment() {
        assertEquals(List.of(new String[]{
                "| 1   | abba | gogo | NoNon |",
                "| :-: | :--: | :--: | :---: |"
        }), new MarkdownTablePrinter().printTable(SmallTable, ColumnAlignment.CENTER,
                ColumnAlignment.CENTER, ColumnAlignment.CENTER, ColumnAlignment.CENTER));
    }

    @Test
    public void testWithoutAlignment() {
        assertEquals(List.of(new String[]{
                "| 1   | abba | gogo | NoNon |",
                "| --- | ---- | ---- | ----- |"
        }), new MarkdownTablePrinter().printTable(SmallTable));
    }

    @Test
    public void testWithLessAlignment() {
        assertEquals(List.of(new String[]{
                "| 1   | abba | gogo | NoNon |",
                "| --: | ---- | ---- | ----- |"
        }), new MarkdownTablePrinter().printTable(SmallTable, ColumnAlignment.RIGHT));
    }

    @Test
    public void testWithMoreAlignment() {
        assertEquals(List.of(new String[]{
                "| 1   | abba | gogo | NoNon |",
                "| --- | ---- | ---- | ----- |"
        }), new MarkdownTablePrinter().printTable(SmallTable, ColumnAlignment.NOALIGNMENT,
                ColumnAlignment.NOALIGNMENT, ColumnAlignment.NOALIGNMENT, ColumnAlignment.NOALIGNMENT, ColumnAlignment.LEFT));
    }

    @Test
    public void testWithMixedAlignment() {
        assertEquals(List.of(new String[]{
                "| 1   | abba | gogo | NoNon |",
                "| --- | :--: | :--- | ----- |"
        }), new MarkdownTablePrinter().printTable(SmallTable, ColumnAlignment.NOALIGNMENT,
                ColumnAlignment.CENTER, ColumnAlignment.LEFT, ColumnAlignment.NOALIGNMENT, ColumnAlignment.LEFT));
    }

}
