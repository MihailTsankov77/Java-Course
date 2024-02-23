package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CsvProcessor implements CsvProcessorAPI {

    private final Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    private static List<String> getRows(Reader reader) {
        List<String> rows = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String row;

            while ((row = bufferedReader.readLine()) != null) {
                rows.add(row);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Reader failed to open", e);
        }

        return rows;
    }

    private static void writeInFile(Writer writer, Collection<String> rows) {
        StringBuilder data = new StringBuilder();

        for (String row : rows) {
            data.append(row).append(System.lineSeparator());
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            bufferedWriter.write(data.toString().trim());
        } catch (IOException e) {
            throw new IllegalStateException("Writer failed to open", e);
        }
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {
        if (delimiter == null || delimiter.isBlank()) {
            throw new IllegalArgumentException("invalid delimiter");
        }

        for (String row : getRows(reader)) {
            table.addData(row.split(STR. "\\Q\{ delimiter }\\E" ));
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        Collection<String> rows = new MarkdownTablePrinter().printTable(table, alignments);
        writeInFile(writer, rows);
    }
}
