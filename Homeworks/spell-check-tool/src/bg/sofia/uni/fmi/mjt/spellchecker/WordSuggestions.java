package bg.sofia.uni.fmi.mjt.spellchecker;

import java.util.List;

public record WordSuggestions(String word, List<String> possibleSuggestions, int lineNumber) {
}
