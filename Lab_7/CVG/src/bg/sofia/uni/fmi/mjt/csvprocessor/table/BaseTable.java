package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseTable implements Table {

    private final List<String> titles = new ArrayList<>();
    private final Map<String, Column> columns = new HashMap<>();
    private int rowsCount = 0;

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null) {
            throw new IllegalArgumentException("BaseTable#addData: arg are null");
        }
        rowsCount++;
        if (titles.isEmpty()) {
            titles.addAll(List.of(data));
            for (String column : data) {
                if (columns.containsKey(column)) {
                    throw new CsvDataNotCorrectException();
                }

                columns.put(column, new BaseColumn());
            }
            return;
        }

        if (data.length != titles.size()) {
            throw new CsvDataNotCorrectException();
        }

        for (int i = 0; i < data.length; i++) {
            columns.get(titles.get(i)).addData(data[i]);
        }
    }

    @Override
    public Collection<String> getColumnNames() {
        return Collections.unmodifiableList(titles);
    }

    @Override
    public Collection<String> getColumnData(String column) {
        if (column == null || column.isBlank()) {
            throw new IllegalArgumentException("BaseTable#getColumnData: arg is null");
        }

        if (columns.containsKey(column)) {
            return columns.get(column).getData();
        }

        throw new IllegalArgumentException("BaseTable#getColumnData: there is no such column");
    }

    @Override
    public int getRowsCount() {
        return rowsCount;
    }
}
