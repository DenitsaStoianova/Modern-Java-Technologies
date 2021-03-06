package bg.sofia.uni.fmi.mjt.spellchecker.formatter;

import java.util.List;

public class LinesFormatter extends LinesInfo {

    private final String metadataOutputText = "= = = Metadata = = =" + System.lineSeparator();
    private final String findingsOutputText = "= = = Findings = = =" + System.lineSeparator();
    private final String noMisspelledWords = "There is no misspelled words at the text." + System.lineSeparator();
    private final String linesFindingsText = "Line #%d, {%s} - Possible suggestions are {%s}";
    private final String metadataFindingsText = "%s%d characters, %d words, %d spelling issue(s) found";

    private int lineNumber = 0;
    private final StringBuilder linesMisspelledWordsFindings;

    public LinesFormatter() {
        this.linesMisspelledWordsFindings = new StringBuilder();
    }

    public void incrementLineNumber() {
        this.lineNumber++;
    }

    public void addLineInfoElements(String word, List<String> suggestions) {
        linesMisspelledWordsFindings.append(String.format(linesFindingsText,
                lineNumber, word, suggestions.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("")))
                .append(System.lineSeparator());
    }

    public String formatOutput() {
        String formattedMetadata = formatMetadata();
        String formattedFindings = formatFindings();

        return String.format("%s%s", formattedMetadata, formattedFindings);
    }

    private String formatMetadata() {
        return String.format(metadataFindingsText + System.lineSeparator(),
                metadataOutputText, super.getCharsNumber(), super.getWordsNumber(), super.getIssuesNumber());
    }

    private String formatFindings() {
        return String.format("%s%s", findingsOutputText,
                linesMisspelledWordsFindings.toString().isEmpty() ? noMisspelledWords
                        : linesMisspelledWordsFindings.toString());
    }
}
