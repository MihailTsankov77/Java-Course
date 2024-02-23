package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvProcessorTest {

    @Test
    public void testReadFromFileAndWriteBack() throws CsvDataNotCorrectException {
        String data = "aa,aaa,A,B" + System.lineSeparator() +
                "bb,bbb,BB,AB" + System.lineSeparator();

        CsvProcessor csvProcessor = new CsvProcessor();

        csvProcessor.readCsv(new StringReader(data.trim()), ",");

        StringWriter writer = new StringWriter();
        csvProcessor.writeTable(writer);
        assertEquals("| aa  | aaa | A   | B   |\n| --- | --- | --- | --- |\n| bb  | bbb | BB  | AB  |",
                writer.toString());
    }

}
