package bg.sofia.uni.fmi.mjt.spellchecker.exceptions;

public class NegativeSuggestionsCountException extends RuntimeException {
    public NegativeSuggestionsCountException(String message) {
        super(message);
    }
}
