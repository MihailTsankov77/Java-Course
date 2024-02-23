package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.exception.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DetailsTest {

    private static final String testRocketName = "Test a1-3-Sad rocket";
    private static final String testPayload = "Sad very sad payload";
    private static final Detail testDetails = new Detail(testRocketName, testPayload);

    @Test
    public void testParseBasic() {
        assertEquals(testDetails, Detail.parse(STR. "\{ testRocketName }|\{ testPayload }" ),
                "The details are separated with |");
    }

    @Test
    public void testParseIntervals() {
        assertEquals(testDetails, Detail.parse(STR. "\{ testRocketName } | \{ testPayload }" ),
                "The details are separated with ' | '");
    }

    @Test
    public void testParseManyIntervals() {
        assertEquals(testDetails, Detail.parse(STR. "\{ testRocketName }     |   \{ testPayload }" ),
                "See if you are handling the case with more than 1 interval on each side");
    }

    @Test
    public void testParseWithWrongValue() {
        assertThrows(ParseException.class, () -> Detail.parse(""),
                "Test if a parse exception is throw when there is wrong data");
    }
}
