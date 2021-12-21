package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static bg.sofia.uni.fmi.mjt.spellchecker.CosineSimilarityCalculator.calculate;


public class NaiveSpellChecker implements SpellChecker {

    private static final int GRAMS_NUMBER = 2;
    private static final String NON_ALPHANUMERIC_CHARACTERS = "[^A-Za-z0-9]";
    private static final String READING_PROBLEM = "A problem occurred while reading from file!";
    private static final String READ_OR_WRITE_MESSAGE = "A problem occured when read or write!";

    private final Set<String> dictionary;
    private final Set<String> stopWords;

    public NaiveSpellChecker(Reader dictionaryReader, Reader stopWordsReader) {
        checkIsNotNull(dictionaryReader, "Dictionary reader");
        checkIsNotNull(stopWordsReader, "Stopwords reader");

        dictionary = readDictionary(dictionaryReader);
        stopWords = readStopWords(stopWordsReader);
    }

    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        checkIsNotNull(textReader, "Text reader");
        checkIsNotNull(output, "Output");
        checkIsNotNegative(suggestionsCount, "Suggestion count");

        int characters = 0, mistakes = 0, words = 0;
        Set<WordSuggestions> wordSuggestions = new HashSet<>();

        try (var reader = new BufferedReader(textReader); var writer = new BufferedWriter(output)) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {

                Metadata currentLineMetadata = processWordsForSingleLine(line.split(" "));
                characters += currentLineMetadata.characters();
                words += currentLineMetadata.words();
                mistakes += currentLineMetadata.mistakes();

                writer.write(line);
                Set<WordSuggestions> suggestionsForCurrentLine = getMistakesForSingleLine(line, lineNumber++, suggestionsCount);
                addSuggestions(wordSuggestions, suggestionsForCurrentLine);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(READ_OR_WRITE_MESSAGE, e);
        }
        printStatistic(output, new Metadata(characters, words, mistakes), wordSuggestions);
    }

    @Override
    public Metadata metadata(Reader textReader) {
        checkIsNotNull(textReader, "Text");

        int characters = 0, mistakes = 0, wordsNumber = 0;
        List<String> lines = readText(textReader);

        for (String line : lines) {
            Metadata currentLineMetadata = processWordsForSingleLine(line.split(" "));
            characters += currentLineMetadata.characters();
            wordsNumber += currentLineMetadata.words();
            mistakes += currentLineMetadata.mistakes();
        }
        return new Metadata(characters, wordsNumber, mistakes);
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        checkIsNotNull(word, "Word");
        checkIsNotEmpty(word, "Word");
        checkIsNotNegative(n, "N");

        Map<Double, String> closestWordOrder = new TreeMap<>(Comparator.reverseOrder());
        for (String dictionaryWord : dictionary) {
            double cosineSimilarity = calculate(dictionaryWord, word, GRAMS_NUMBER);
            closestWordOrder.put(cosineSimilarity, dictionaryWord);
        }

        if (n > closestWordOrder.size()) {
            n = closestWordOrder.size();
        }

        return closestWordOrder.values().stream().limit(n).collect(Collectors.toList());
    }

    private Set<String> readDictionary(Reader dictionaryReader) {
        Set<String> dictionary;
        try (BufferedReader reader = new BufferedReader(dictionaryReader)) {
            dictionary = reader
                    .lines()
                    .map(word -> word.trim())
                    .map(word -> word.replaceAll(NON_ALPHANUMERIC_CHARACTERS, ""))
                    .filter(word -> !word.contains(" "))
                    .filter(word -> word.length() != 1)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IllegalStateException(READING_PROBLEM, e);
        }
        return dictionary;
    }

    private Set<String> readStopWords(Reader stopWordsReader) {
        Set<String> stopWords;
        try (BufferedReader reader = new BufferedReader(stopWordsReader)) {
            stopWords = reader
                    .lines()
                    .map(word -> word.trim())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IllegalStateException(READING_PROBLEM, e);
        }
        return stopWords;
    }

    private Set<WordSuggestions> getMistakesForSingleLine(String line, int lineNumber, int suggestionsCount) {
        Set<WordSuggestions> wordSuggestions = new HashSet<>();
        String[] words = line.split(" ");
        for (String word : words) {
            String cleanWord = word.replaceAll(NON_ALPHANUMERIC_CHARACTERS, "");

            if (!dictionary.contains(cleanWord) && !stopWords.contains(cleanWord)) {

                List<String> possibleSuggestions = findClosestWords(cleanWord, suggestionsCount);
                wordSuggestions.add(new WordSuggestions(cleanWord, possibleSuggestions, lineNumber));
            }
        }
        return wordSuggestions;
    }

    private void addSuggestions(Set<WordSuggestions> wordSuggestions, Set<WordSuggestions> suggestionsForCurrentLine) {
        for (WordSuggestions word : suggestionsForCurrentLine) {
            if (!wordSuggestions.contains(word)) {
                wordSuggestions.add(word);
            }
        }
    }

    private void printStatistic(Writer output, Metadata metadata, Set<WordSuggestions> wordSuggestions) {
        try (var writer = new BufferedWriter(output)) {
            writer.write("\n===Metadata===\n");
            writer.write(metadata.characters() + " characters, " + metadata.words() + " words, " + metadata.mistakes() + " spelling issue(s) found\n");
            writer.write("===Findings===\n");

            for (WordSuggestions word : wordSuggestions) {
                writer.write("Line #" + word.lineNumber() + ",{" + word.word() + "}- Possible suggestions are {");
                List<String> possibleSuggestions = word.possibleSuggestions();
                String currentWordSuggestions = possibleSuggestions.toString();
                writer.write(currentWordSuggestions.substring(1, currentWordSuggestions.length() - 1));
                writer.write("}\n");
            }
        } catch (IOException e) {
            throw new IllegalStateException(READ_OR_WRITE_MESSAGE, e);
        }
    }

    private Metadata processWordsForSingleLine(String[] words) {
        int characters = 0, mistakes = 0, wordsNumber = 0;
        for (String word : words) {
            String filteredWord = word.replaceAll(NON_ALPHANUMERIC_CHARACTERS, "");
            if (!stopWords.contains(filteredWord)) {

                if (dictionary.contains(filteredWord)) {
                    wordsNumber++;
                } else if (!stopWords.contains(filteredWord)) {
                    mistakes++;
                }
            }
            characters += getCharactersNumber(word);
        }
        return new Metadata(characters, wordsNumber, mistakes);
    }


    private List<String> readText(Reader textReader) {
        try (BufferedReader reader = new BufferedReader(textReader)) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(READING_PROBLEM, e);
        }
    }

    private int getCharactersNumber(String word) {
        return (int) word.chars()
                .mapToObj(c -> (char) c)
                .filter(c -> !Character.isWhitespace(c))
                .count();
    }

    private void checkIsNotNull(Object key, String keyDescription) {
        if (key == null) {
            throw new IllegalArgumentException(keyDescription + " cannot be null!");
        }
    }

    private void checkIsNotEmpty(Object key, String keyDescription) {
        if (key instanceof String && key.toString().isEmpty()) {
            throw new IllegalArgumentException(keyDescription + " cannot be empty!");
        }
    }

    private void checkIsNotNegative(int key, String keyDescription) {
        if (key < 0) {
            throw new IllegalArgumentException(keyDescription + " cannot be negative!");
        }
    }

    public static void main(String[] args) {
//        String word = "Mario.";
//        System.out.println(word.matches("^[a-zA-Z0-9_.-]*$"));
        System.out.println("".isEmpty());
    }

}
