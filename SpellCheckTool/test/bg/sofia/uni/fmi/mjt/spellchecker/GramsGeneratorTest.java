package bg.sofia.uni.fmi.mjt.spellchecker;

import bg.sofia.uni.fmi.mjt.spellchecker.generator.GramsGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GramsGeneratorTest {
    private static GramsGenerator gramsGenerator;

    @BeforeClass
    public static void setUp() {
        gramsGenerator = new GramsGenerator();
    }

    @Test
    public void testGenerateWordTwoGramsSimpleWord() {
        final String word = "hello";
        final Map<String, Integer> expectedTwoGrams = Map.ofEntries(
                Map.entry("ll", 1), Map.entry("lo", 1),
                Map.entry("el", 1), Map.entry("he", 1));

        Map<String, Integer> actualTwoGrams = gramsGenerator.generateWordTwoGrams(word);

        assertEquals("Two grams are not generated correctly.",
                expectedTwoGrams, actualTwoGrams);
    }

    @Test
    public void testGenerateWordTwoGramsDifferentSymbolsWord() {
        final String word = "tam-o'-sh";
        final Map<String, Integer> expectedTwoGrams = Map.ofEntries(
                Map.entry("m-", 1), Map.entry("-o", 1),
                Map.entry("sh", 1), Map.entry("'-", 1),
                Map.entry("-s", 1), Map.entry("o'", 1),
                Map.entry("am", 1), Map.entry("ta", 1));

        Map<String, Integer> actualTwoGrams = gramsGenerator.generateWordTwoGrams(word);

        assertEquals("Two grams are not generated correctly.",
                expectedTwoGrams, actualTwoGrams);
    }

    @Test
    public void testGenerateWordTwoGramsOccurrencesWord() {
        final String word = "hellohell";
        final Map<String, Integer> expectedTwoGrams = Map.ofEntries(
                Map.entry("ll", 2), Map.entry("lo", 1),
                Map.entry("el", 2), Map.entry("oh", 1), Map.entry("he", 2));

        Map<String, Integer> actualTwoGrams = gramsGenerator.generateWordTwoGrams(word);

        assertEquals("Two grams are not generated correctly with occurrences.",
                expectedTwoGrams, actualTwoGrams);
    }

    @Test
    public void testGenerateSuggestions() {
        Map<String, Map<String, Integer>> dictionaryWords = generateDictionaryWords();

        final String word = "helloo";
        final int suggestionsCount = 2;
        final List<String> expected = List.of("hello", "phello");

        List<String> actual = gramsGenerator.generateSuggestions(dictionaryWords, word, suggestionsCount);

        assertEquals("Closest words are not generated correctly.", expected, actual);
    }

    public static Map<String, Map<String, Integer>> generateDictionaryWords() {
        return Map.ofEntries(
                Map.entry("hello", Map.ofEntries(
                        Map.entry("ll", 1), Map.entry("lo", 1),
                        Map.entry("el", 1), Map.entry("he", 1))),
                Map.entry("phello", Map.ofEntries(
                        Map.entry("ll", 1), Map.entry("lo", 1),
                        Map.entry("el", 1), Map.entry("ph", 1), Map.entry("he", 1))),
                Map.entry("chelo", Map.ofEntries(
                        Map.entry("lo", 1), Map.entry("ch", 1),
                        Map.entry("el", 1), Map.entry("he", 1)))
        );
    }

}
