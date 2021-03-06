package bg.sofia.uni.fmi.mjt.spellchecker;

import bg.sofia.uni.fmi.mjt.spellchecker.formatter.LinesFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LinesFormatterTest {

    private static LinesFormatter linesFormatter;

    @Before
    public void setUp() {
        linesFormatter = new LinesFormatter();
    }

    @Test
    public void testFormatOutputEmptyTextNoSuggestions() {
        final String[] resultText = {
            "= = = Metadata = = =",
            "0 characters, 0 words, 0 spelling issue(s) found",
            "= = = Findings = = =",
            "There is no misspelled words at the text." + System.lineSeparator()
        };
        final String expectedFormat = String.join(System.lineSeparator(), resultText);

        String actualFormat = linesFormatter.formatOutput();

        assertEquals("Result is not in correct format when there is no misspelled words.",
                expectedFormat, actualFormat);
    }

    @Test
    public void testFormatOutputOneWordSuggestion() {
        final String[] resultText = {
            "= = = Metadata = = =",
            "6 characters, 1 words, 1 spelling issue(s) found",
            "= = = Findings = = =",
            "Line #1, {helloo} - Possible suggestions are {hello, phello, chello}"
                        + System.lineSeparator()
        };
        final String expectedFormat = String.join(System.lineSeparator(), resultText);

        linesFormatter.incrementLineNumber();
        linesFormatter.incrementCharsNumber(6);
        linesFormatter.incrementWordsNumber();
        linesFormatter.incrementIssuesNumber();
        linesFormatter.addLineInfoElements("helloo", List.of("hello", "phello", "chello"));

        String actualFormat = linesFormatter.formatOutput();

        assertEquals("Result is not in correct format.",
                expectedFormat, actualFormat);
    }

    @Test
    public void testFormatOutputMoreLinesSuggestions() {
        final String[] resultText = {
            "= = = Metadata = = =",
            "48 characters, 8 words, 3 spelling issue(s) found",
            "= = = Findings = = =",
            "Line #1, {helloo} - Possible suggestions are {hello, phello, chello}",
            "Line #2, {smar} - Possible suggestions are {smarr, smarm, smart}",
            "Line #2, {tehnology} - Possible suggestions are {technology, tenology, ichnology}"
                        + System.lineSeparator()
        };
        final String expectedFormat = String.join(System.lineSeparator(), resultText);

        linesFormatter.incrementCharsNumber(48);
        incrementWords(8);
        incrementIssues(3);
        linesFormatter.incrementLineNumber();
        linesFormatter.addLineInfoElements("helloo", List.of("hello", "phello", "chello"));
        linesFormatter.incrementLineNumber();
        linesFormatter.addLineInfoElements("smar", List.of("smarr", "smarm", "smart"));
        linesFormatter.addLineInfoElements("tehnology", List.of("technology", "tenology", "ichnology"));

        String actualFormat = linesFormatter.formatOutput();

        assertEquals("Result is not in correct format when there is more lines with misspelled words.",
                expectedFormat, actualFormat);
    }

    private void incrementWords(int timesToIncrement) {
        for (int i = 0; i < timesToIncrement; i++) {
            linesFormatter.incrementWordsNumber();
        }
    }

    private void incrementIssues(int timesToIncrement) {
        for (int i = 0; i < timesToIncrement; i++) {
            linesFormatter.incrementIssuesNumber();
        }
    }
}
