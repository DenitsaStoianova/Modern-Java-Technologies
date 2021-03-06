package bg.sofia.uni.fmi.mjt.spellchecker.formatter;

public class WordFormatter {

    public String formatWord(String word) {
        String formattedWord = removeNonAlphanumeric(word.toLowerCase().trim());
        return formattedWord.length() > 1 ? formattedWord : "";
    }

    private String removeLeadingNonAlphanumeric(String word) {
        char[] wordArray = word.toCharArray();
        int i;
        for (i = 0; i < wordArray.length; i++) {
            if (Character.isLetterOrDigit(wordArray[i])) {
                break;
            }
        }
        return word.substring(i);
    }

    private String removeTailingNonAlphanumeric(String word) {
        char[] wordArray = word.toCharArray();
        int i;
        for (i = wordArray.length - 1; i >= 0; i--) {
            if (Character.isLetterOrDigit(wordArray[i])) {
                break;
            }
        }
        return word.substring(0, i + 1);
    }

    private String removeNonAlphanumeric(String word) {
        String removedLeading = removeLeadingNonAlphanumeric(word);
        return removeTailingNonAlphanumeric(removedLeading);
    }
}
