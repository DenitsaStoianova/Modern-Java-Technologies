package bg.sofia.uni.fmi.mjt.spellchecker.formatter;

public class LinesInfo {

    private int charsNumber = 0;
    private int wordsNumber = 0;
    private int issuesNumber = 0;

    public void incrementCharsNumber(int wordCharsCount) {
        this.charsNumber += wordCharsCount;
    }

    public void incrementWordsNumber() {
        this.wordsNumber++;
    }

    public void incrementIssuesNumber() {
        this.issuesNumber++;
    }

    public int getCharsNumber() {
        return charsNumber;
    }

    public int getWordsNumber() {
        return wordsNumber;
    }

    public int getIssuesNumber() {
        return issuesNumber;
    }
}
