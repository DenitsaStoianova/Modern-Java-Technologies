package bg.sofia.uni.fmi.mjt.spellchecker;

import bg.sofia.uni.fmi.mjt.spellchecker.formatter.WordFormatter;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WordFormatterTest {

    private static WordFormatter wordFormatter;

    @BeforeClass
    public void setUp() {
        wordFormatter = new WordFormatter();
    }

    @Test
    public void testFormatWordCaseInsensitive() {
        final String word = "HeLLo";
        final String expected = "hello";

        String actual = wordFormatter.formatWord(word);

        assertEquals("Word is not formatted correctly, it should be case insensitive.",
                expected, actual);
    }

    @Test
    public void testFormatWordTrimLeadingWhitespaces() {
        final String word = "    hello";
        final String expected = "hello";

        String actual = wordFormatter.formatWord(word);

        assertEquals("Word is not formatted correctly, leading whitespaces should be removed.",
                expected, actual);
    }

    @Test
    public void testFormatWordTrimTailingWhitespaces() {
        final String word = "hello   ";
        final String expected = "hello";

        String actual = wordFormatter.formatWord(word);

        assertEquals("Word is not formatted correctly, tailing whitespaces should be removed.",
                expected, actual);
    }

    @Test
    public void testFormatWordTrimLeadingAndTailingWhitespaces() {
        final String word = "      hello  ";
        final String expected = "hello";

        String actual = wordFormatter.formatWord(word);

        assertEquals("Word is not formatted correctly, leading and tailing whitespaces should be removed.",
                expected, actual);
    }

    @Test
    public void testFormatWordRemoveLeadingSymbols() {
        final String word = "%$#he$ll0";
        final String expected = "he$ll0";

        String actual = wordFormatter.formatWord(word);

        assertEquals("Word is not formatted correctly, leading non-alphanumeric symbols should be removed.",
                expected, actual);
    }

    @Test
    public void testFormatWordRemoveTailingSymbols() {
        final String word = "he$ll0&@";
        final String expected = "he$ll0";

        String actual = wordFormatter.formatWord(word);

        assertEquals("Word is not formatted correctly, tailing non-alphanumeric symbols should be removed.",
                expected, actual);
    }

    @Test
    public void testFormatWordRemoveLeadingAndTailingSymbols() {
        final String word = "*^%he$ll0&@";
        final String expected = "he$ll0";

        String actual = wordFormatter.formatWord(word);

        assertEquals("Word is not formatted correctly, "
                        + "leading and tailing non-alphanumeric symbols should be removed.",
                expected, actual);
    }

    @Test
    public void testFormatWordRemoveOneSymbolWord() {
        final String word = "h";
        final String expected = "";

        String actual = wordFormatter.formatWord(word);

        assertEquals("Word is not formatted correctly, one-symbol words should be removed.",
                expected, actual);
    }
}
