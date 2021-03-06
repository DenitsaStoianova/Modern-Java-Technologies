package bg.sofia.uni.fmi.mjt.spellchecker;

import bg.sofia.uni.fmi.mjt.spellchecker.exceptions.NullInputParameterException;
import bg.sofia.uni.fmi.mjt.spellchecker.formatter.LinesFormatter;
import bg.sofia.uni.fmi.mjt.spellchecker.exceptions.NegativeSuggestionsCountException;
import bg.sofia.uni.fmi.mjt.spellchecker.formatter.LinesInfo;
import bg.sofia.uni.fmi.mjt.spellchecker.generator.GramsGenerator;
import bg.sofia.uni.fmi.mjt.spellchecker.formatter.WordFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NaiveSpellChecker implements SpellChecker {

    private final String fileProblemMessage = "A problem occurred while reading from a file.";
    private final String nullInputDataMessage = "Input %s is null.";
    private final String negativeSuggestionsMessage = "Suggestions count is negative number.";

    private Map<String, Map<String, Integer>> dictionaryWords;
    private Set<String> stopWords;
    private final GramsGenerator gramsGenerator;
    private final WordFormatter wordFormatter;

    public NaiveSpellChecker(Reader dictionaryReader, Reader stopwordsReader) {
        checkWordsReaderFiles(dictionaryReader, stopwordsReader);

        wordFormatter = new WordFormatter();
        gramsGenerator = new GramsGenerator();
        loadDictionaryWords(dictionaryReader);
        loadStopWords(stopwordsReader);
    }

    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        checkAnalyzeFiles(textReader, output);
        checkParameter(suggestionsCount);

        LinesFormatter linesFormatter = new LinesFormatter();

        try (BufferedReader textBufferedReader = new BufferedReader(textReader);
             BufferedWriter outputBufferedWriter = new BufferedWriter(output)) {
            String textLine;
            while ((textLine = textBufferedReader.readLine()) != null) {
                linesFormatter.incrementLineNumber();

                analyzeSentenceWords(linesFormatter, textLine.split("\\s+"), suggestionsCount);

                outputBufferedWriter.write(textLine + System.lineSeparator());
                outputBufferedWriter.flush();
            }
            outputBufferedWriter.write(linesFormatter.formatOutput());
        } catch (IOException e) {
            throw new IllegalStateException(fileProblemMessage, e);
        }
    }

    @Override
    public Metadata metadata(Reader textReader) {
        if (textReader == null) {
            throw new NullInputParameterException(String.format(nullInputDataMessage, "text reader"));
        }

        LinesInfo linesInfo = new LinesInfo();

        try (BufferedReader textBufferedReader = new BufferedReader(textReader)) {
            String textLine;
            while ((textLine = textBufferedReader.readLine()) != null) {
                metadataSentence(linesInfo, textLine.split("\\s+"));
            }

        } catch (IOException e) {
            throw new IllegalStateException(fileProblemMessage, e);
        }

        return new Metadata(linesInfo.getCharsNumber(), linesInfo.getWordsNumber(), linesInfo.getIssuesNumber());
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        if (word == null) {
            throw new NullInputParameterException(String.format(nullInputDataMessage, "word"));
        }

        checkParameter(n);

        return gramsGenerator.generateSuggestions(dictionaryWords, wordFormatter.formatWord(word), n);
    }

    private void analyzeSentenceWords(LinesFormatter linesFormatter, String[] wordsToAnalyse, int suggestionsCount) {
        for (String word : wordsToAnalyse) {
            String formatted = wordFormatter.formatWord(word);

            if (!formatted.isEmpty() && !stopWords.contains(formatted)) {
                linesFormatter.incrementWordsNumber();
                if (!dictionaryWords.containsKey(formatted)) {
                    linesFormatter.incrementIssuesNumber();

                    linesFormatter.addLineInfoElements(word, gramsGenerator
                            .generateSuggestions(dictionaryWords, formatted, suggestionsCount));
                }
            }
            linesFormatter.incrementCharsNumber(word.length());
        }
    }

    private void metadataSentence(LinesInfo linesInfo, String[] wordsToAnalyse){
        for (String word : wordsToAnalyse) {
            String formatted = wordFormatter.formatWord(word);

            if (!formatted.isEmpty() && !stopWords.contains(formatted)) {
                linesInfo.incrementWordsNumber();
                if (!dictionaryWords.containsKey(formatted)) {
                    linesInfo.incrementIssuesNumber();
                }
            }
            linesInfo.incrementCharsNumber(word.length());
        }
    }

    private void loadDictionaryWords(Reader dictionaryReader) {
        try (BufferedReader dictionaryBufferedReader = new BufferedReader(dictionaryReader)) {
            dictionaryWords = dictionaryBufferedReader.lines()
                    .map(wordFormatter::formatWord)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toMap(w -> w, gramsGenerator::generateWordTwoGrams));
        } catch (IOException e) {
            throw new IllegalStateException(fileProblemMessage, e);
        }
    }

    private void loadStopWords(Reader stopwordsReader) {
        try (BufferedReader stopwordsBufferedReader = new BufferedReader(stopwordsReader)) {
            stopWords = stopwordsBufferedReader.lines()
                    .map(s -> s.toLowerCase().trim())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IllegalStateException(fileProblemMessage, e);
        }
    }

    private void checkParameter(int suggestionsCount) {
        if (suggestionsCount < 0) {
            throw new NegativeSuggestionsCountException(negativeSuggestionsMessage);
        }
    }

    private void checkWordsReaderFiles(Reader dictionaryReader, Reader stopwordsReader) {
        String messageText = String.format("%s%s",
                (dictionaryReader == null) ? "Dictionary words reader" : "",
                (stopwordsReader == null) ? "Stopwords writer" : "");

        executeMessageText(messageText);
    }

    private void checkAnalyzeFiles(Reader textReader, Writer output) {
        String messageText = String.format("%s%s",
                (textReader == null) ? "Input text reader" : "",
                (output == null) ? "Output result writer" : "");

        executeMessageText(messageText);
    }

    private void executeMessageText(String messageText) {
        if (!messageText.equals("")) {
            throw new NullInputParameterException(String.format("%s is null.", messageText));
        }
    }
}
