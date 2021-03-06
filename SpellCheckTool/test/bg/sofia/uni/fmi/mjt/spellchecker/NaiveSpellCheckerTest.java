package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.spellchecker.exceptions.NegativeSuggestionsCountException;
import bg.sofia.uni.fmi.mjt.spellchecker.exceptions.NullInputParameterException;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NaiveSpellCheckerTest {

    private static NaiveSpellChecker naiveSpellChecker;

    @BeforeClass
    public static void setUp() {
        Reader dictionaryWordsReader = initializeDictionaryWords();
        Reader stopWordsReader = initializeStopWords();

        naiveSpellChecker = new NaiveSpellChecker(dictionaryWordsReader, stopWordsReader);
    }

    @Test(expected = NegativeSuggestionsCountException.class)
    public void testAnalyseThrowsNegativeParameterException() {
        Reader textReader = new StringReader("");
        Writer outputWriter = new StringWriter();

        naiveSpellChecker.analyze(textReader, outputWriter, -1);
    }

    @Test(expected = NullInputParameterException.class)
    public void testAnalyseThrowsNullParameterException() {
        naiveSpellChecker.analyze(null, null, 1);
    }

    @Test
    public void testAnalyzeEmptyFile() {
        final String inputText = "";

        final String[] resultText = {
            "= = = Metadata = = =",
            "0 characters, 0 words, 0 spelling issue(s) found",
            "= = = Findings = = =",
            "There is no misspelled words at the text." + System.lineSeparator()
        };

        final String expected = String.join(System.lineSeparator(), resultText);

        Reader inputReader = new StringReader(inputText);
        Writer outputWriter = new StringWriter();

        naiveSpellChecker.analyze(inputReader, outputWriter, 3);

        assertEquals("Output text is not generated correctly when input file is empty.",
                expected, outputWriter.toString());
    }

    @Test
    public void testAnalyseNoMisspelledWords() {
        final String inputText = "I live in Davisburg";

        final String[] resultText = {
            "I live in Davisburg",
            "= = = Metadata = = =",
            "16 characters, 2 words, 0 spelling issue(s) found",
            "= = = Findings = = =",
            "There is no misspelled words at the text." + System.lineSeparator()
        };
        final String expected = String.join(System.lineSeparator(), resultText);

        Reader inputReader = new StringReader(inputText);
        Writer outputWriter = new StringWriter();

        naiveSpellChecker.analyze(inputReader, outputWriter, 3);

        assertEquals("Output text is not generated correctly.",
                expected, outputWriter.toString());
    }

    @Test
    public void testAnalyseWithMisspelledWords() {
        final String[] text = {
            "Helloo I live in Davisburg",
            "We use smar tehnology every day",
            "= = = Metadata = = =",
            "48 characters, 8 words, 3 spelling issue(s) found",
            "= = = Findings = = =",
            "Line #1, {Helloo} - Possible suggestions are {hello, phello, chello}",
            "Line #2, {smar} - Possible suggestions are {smarr, smarm, smart}",
            "Line #2, {tehnology} - Possible suggestions are {technology, tenology, ichnology}"
                    + System.lineSeparator()
        };
        final String expected = String.join(System.lineSeparator(), text);

        Reader inputReader = initializeTextToAnalyse();
        Writer outputWriter = new StringWriter();

        naiveSpellChecker.analyze(inputReader, outputWriter, 3);

        assertEquals("Output text is not generated correctly.",
                expected, outputWriter.toString());
    }

    @Test(expected = NullInputParameterException.class)
    public void testMetadataThrowsNullParameterException() {
        naiveSpellChecker.metadata(null);
    }

    @Test
    public void testMetadataCorrectSymbolsCount() {
        final String inputText = "  He is very \t&8smart#%  $%^&." + System.lineSeparator();
        final int expectedSymbolsCount = 22;

        Reader inputReader = new StringReader(inputText);

        Metadata actualMetadata = naiveSpellChecker.metadata(inputReader);
        int actualSymbolsCount = actualMetadata.characters();

        assertEquals("Characters are not counted correctly, "
                        + "they should not include whitespace symbols.",
                expectedSymbolsCount, actualSymbolsCount);
    }

    @Test
    public void testMetadataCorrectWordsCount() {
        final String inputText = "He is very &8smart#%.He reads books $%^& day.";
        final int expectedWordsCount = 5;

        Reader inputReader = new StringReader(inputText);

        Metadata actualMetadata = naiveSpellChecker.metadata(inputReader);
        int actualWordsCount = actualMetadata.words();

        assertEquals("Words are not counted correctly, "
                        + "they should not include stopwords and non-alphanumberic words.",
                expectedWordsCount, actualWordsCount);
    }

    @Test
    public void testMetadataCorrectIssuesCount() {
        final String inputText = "They are very smart, they use Apache open source software.";
        final int expectedIssuesCount = 4;

        Reader inputReader = new StringReader(inputText);

        Metadata actualMetadata = naiveSpellChecker.metadata(inputReader);
        int actualIssuesCount = actualMetadata.mistakes();

        assertEquals("Issues are not counted correctly, "
                        + "they should not include stopwords and dictionary words.",
                expectedIssuesCount, actualIssuesCount);
    }

    @Test
    public void testMetadata() {
        final int charactersNumber = 48;
        final int wordsNumber = 8;
        final int issuesNumber = 3;
        final Metadata expected = new Metadata(charactersNumber, wordsNumber, issuesNumber);

        Reader inputReader = initializeTextToAnalyse();

        Metadata actual = naiveSpellChecker.metadata(inputReader);

        assertEquals("Metadata is not generated correctly.", expected, actual);
    }

    @Test(expected = NullInputParameterException.class)
    public void testFindClosestWordsThrowsNullParameterException() {
        naiveSpellChecker.findClosestWords(null, 1);
    }

    @Test(expected = NegativeSuggestionsCountException.class)
    public void testFindClosestWordsThrowsException() {
        naiveSpellChecker.findClosestWords("", -1);
    }

    @Test
    public void textFindClosestWords() {
        final String word = "Helloo";
        final int suggestionsCount = 3;
        final List<String> expected = List.of("hello", "phello", "chello");

        List<String> actual = naiveSpellChecker.findClosestWords(word, suggestionsCount);

        assertEquals("Closest words are not generated correctly.",
                expected, actual);
    }

    @Test
    public void textFindClosestWordsGenerateRandomSuggestions() {
        final String word = "Helloo";
        final int suggestionsCount = 5;
        final List<String> expected = List.of("hello", "phello", "chello", "apache", "tenology");

        List<String> actual = naiveSpellChecker.findClosestWords(word, suggestionsCount);

        assertEquals("Closest words are not generated correctly.",
                expected, actual);
    }

    public static Reader initializeDictionaryWords() {
        String[] dictionaryWords = {
            "Apache", "MAP", "firstborn", "Smart", "Davisburg", "hello", "phello", "live", "use",
            "every", "Day", "chello", "smarr", "smarm", "technology", "tenology", "ichnology"
        };

        return new StringReader(Arrays.stream(dictionaryWords).collect(Collectors.joining(System.lineSeparator())));
    }

    public static Reader initializeStopWords() {
        String[] stopWords = {
            "he", "they", "has", "we", "are", "i", "in", "is"
        };

        return new StringReader(Arrays.stream(stopWords).collect(Collectors.joining(System.lineSeparator())));
    }

    public static Reader initializeTextToAnalyse() {
        String[] text = {
            "Helloo I live in Davisburg",
            "We use smar tehnology every day",
        };

        return new StringReader(Arrays.stream(text).collect(Collectors.joining(System.lineSeparator())));
    }
}