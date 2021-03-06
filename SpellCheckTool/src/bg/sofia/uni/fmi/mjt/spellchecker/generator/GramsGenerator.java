package bg.sofia.uni.fmi.mjt.spellchecker.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GramsGenerator {

    private final int twoGramsNumber = 2;

    public List<String> generateSuggestions(Map<String, Map<String, Integer>> dictionaryWords,
                                            String formatted, int suggestionsCount) {
        Map<String, Integer> lineWordVector = generateWordTwoGrams(formatted);
        return dictionaryWords.entrySet()
                .stream()
                .sorted((w1, w2) -> Double.compare(calculateCosineSimilarity(lineWordVector, w2.getValue()),
                        calculateCosineSimilarity(lineWordVector, w1.getValue())))
                .limit(suggestionsCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableList());
    }

    public Map<String, Integer> generateWordTwoGrams(String word) {
        Map<String, Integer> wordTwoGrams = new HashMap<>();
        String singleTwoGram;
        int occurrences;

        for (int i = 0; i <= word.length() - twoGramsNumber; i++) {
            singleTwoGram = word.substring(i, i + twoGramsNumber);
            occurrences = countTwoGramOccurrences(word, singleTwoGram);
            wordTwoGrams.put(singleTwoGram, occurrences);
        }

        return wordTwoGrams;
    }

    private int countTwoGramOccurrences(String word, String wordTwoGram) {
        int positionInWord = 0;
        int occurrences = 0;

        while ((positionInWord = word.indexOf(wordTwoGram, positionInWord)) != -1) {
            positionInWord += twoGramsNumber;
            occurrences++;
        }

        return occurrences;
    }

    private double calculateVectorLength(Map<String, Integer> wordVector) {
        return Math.sqrt(wordVector.values()
                .stream()
                .mapToDouble(v -> Math.pow(v, 2))
                .sum());
    }

    private double calculateCosineSimilarity(Map<String, Integer> lineWordVector,
                                             Map<String, Integer> dictionaryWordVector) {
        double lineWordVectorLength = calculateVectorLength(lineWordVector);
        double dictionaryWordVectorLength = calculateVectorLength(dictionaryWordVector);

        int vectorsMultiplication = lineWordVector.entrySet()
                .stream()
                .filter(e -> dictionaryWordVector.containsKey(e.getKey()))
                .mapToInt(e -> e.getValue() * dictionaryWordVector.get(e.getKey()))
                .sum();

        return vectorsMultiplication / (lineWordVectorLength * dictionaryWordVectorLength);
    }
}
