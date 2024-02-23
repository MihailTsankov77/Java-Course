package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment.LEFT;
import static bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment.NOALIGNMENT;

public class MarkdownTablePrinter implements TablePrinter {

    private static final int MIN_COLUMN_SIZE = 3;

    private static Map<String, ColumnAlignment> getColumnAlignment(Table table, ColumnAlignment[] alignments) {
        Map<String, ColumnAlignment> columnAlignments = new HashMap<>();

        Collection<String> titles = table.getColumnNames();
        int i = 0;

        for (String title : titles) {
            if (i >= alignments.length) {
                columnAlignments.put(title, NOALIGNMENT);
                continue;
            }

            columnAlignments.put(title, alignments[i]);
            ++i;
        }

        return columnAlignments;
    }

    private static Map<String, Integer> getColumnsMaxSize(Table table) {
        Map<String, Integer> maxSizes = new HashMap<>();
        Collection<String> titles = table.getColumnNames();

        for (String title : titles) {
            maxSizes.putIfAbsent(title, Math.max(MIN_COLUMN_SIZE, title.length()));
            for (String columnRow : table.getColumnData(title)) {
                maxSizes.put(title, Math.max(maxSizes.get(title), columnRow.length()));
            }
        }

        return maxSizes;
    }

    private static String getOffset(String cell, Integer maxSize, ColumnAlignment alignment, boolean isPrefix) {
        int leftSize = maxSize - cell.length();
        String emptySpace = " ";

        return switch (alignment) {
            case NOALIGNMENT, LEFT -> emptySpace.repeat(isPrefix ? 1 : leftSize + 1);
            case RIGHT -> emptySpace.repeat(isPrefix ? leftSize + 1 : 1);
            case CENTER -> emptySpace.repeat((int) (isPrefix ?
                    Math.ceil((double) leftSize / 2) : Math.floor((double) leftSize / 2)) + 1); // Problem :)
        };

    }

    private static ArrayList<ArrayList<String>> getRows(Table table) {
        ArrayList<ArrayList<String>> rows = new ArrayList<>(table.getRowsCount() - 1);

        for (int i = 0; i < table.getRowsCount() - 1; i++) {
            rows.add(new ArrayList<>());
        }

        for (String title : table.getColumnNames()) {
            List<String> columnsData = new ArrayList<>(table.getColumnData(title));
            for (int i = 0; i < columnsData.size(); i++) {
                rows.get(i).add(columnsData.get(i));
            }
        }

        return rows;
    }

    private static String getRowString(ArrayList<String> rows, Map<String, ColumnAlignment> columnAlignments,
                                       Map<String, Integer> maxSizes, ArrayList<String> titles) {
        StringBuilder row = new StringBuilder();
        row.append("|");

        for (int i = 0; i < rows.size(); i++) {
            String title = titles.get(i);
            row.append(getOffset(rows.get(i), maxSizes.get(title), LEFT, true));
            row.append(rows.get(i));
            row.append(getOffset(rows.get(i), maxSizes.get(title), LEFT, false));
            row.append("|");
        }

        return row.toString();
    }

    private static String getLeftString(ColumnAlignment alignment) {
        return switch (alignment) {
            case NOALIGNMENT, RIGHT -> " ";
            case LEFT, CENTER -> " :";
        };
    }

    private static String getRightString(ColumnAlignment alignment) {
        return switch (alignment) {
            case NOALIGNMENT, LEFT -> "";
            case RIGHT, CENTER -> ":";
        };
    }

    private static int getDashCount(ColumnAlignment alignment, int maxSize) {
        return switch (alignment) {
            case NOALIGNMENT -> maxSize;
            case LEFT, RIGHT -> maxSize - 1;
            case CENTER -> maxSize - 2;
        };
    }

    private static String getAlignment(Map<String, ColumnAlignment> columnAlignments,
                                       Map<String, Integer> maxSizes, ArrayList<String> titles) {
        StringBuilder row = new StringBuilder();
        row.append("|");

        for (String title : titles) {
            row.append(getLeftString(columnAlignments.get(title)));

            row.append("-".repeat(getDashCount(columnAlignments.get(title), maxSizes.get(title))));

            row.append(getRightString(columnAlignments.get(title)));
            row.append(" |");
        }

        return row.toString();
    }

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        Map<String, ColumnAlignment> columnAlignments = getColumnAlignment(table, alignments);
        Map<String, Integer> maxSizes = getColumnsMaxSize(table);

        ArrayList<ArrayList<String>> rows = getRows(table);

        List<String> tableData = new ArrayList<>(table.getRowsCount() + 1);
        ArrayList<String> titles = new ArrayList<>(table.getColumnNames());

        tableData.add(getRowString(titles, columnAlignments, maxSizes, titles));
        tableData.add(getAlignment(columnAlignments, maxSizes, titles));

        for (ArrayList<String> row : rows) {
            tableData.add(getRowString(row, columnAlignments, maxSizes, titles));
        }

        return tableData;

    }
}
