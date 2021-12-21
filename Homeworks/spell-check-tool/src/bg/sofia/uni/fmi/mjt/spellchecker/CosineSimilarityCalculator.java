package bg.sofia.uni.fmi.mjt.spellchecker;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class CosineSimilarityCalculator {

    public static double calculate(String firstWord, String secondWord, int gramNumber) {
        Map<String, Integer> firstWordGrams = getNGramWord(gramNumber, firstWord);
        Map<String, Integer> secondWordGrams = getNGramWord(gramNumber, secondWord);

        double numerator = multiplyEqualGrams(firstWordGrams, secondWordGrams);
        double denominator = getVectorLength(firstWordGrams, gramNumber) * getVectorLength(secondWordGrams, gramNumber);
        return numerator / denominator;
    }

    private static Map<String, Integer> getNGramWord(int gramNumber, String word) {
        Map<String, Integer> grams = new HashMap<>();
        for (int i = 0; i < word.length() - gramNumber + 1; ++i) {
            String subString = word.substring(i, i + gramNumber);
            if (grams.containsKey(subString)) {
                grams.put(subString, grams.get(subString) + 1);
            } else {
                grams.put(subString, 1);
            }
        }
        return grams;
    }

    private static double multiplyEqualGrams(Map<String, Integer> firstWordGrams, Map<String, Integer> secondWordGrams) {
        double multiplyResult = 0;

        for (Map.Entry<String, Integer> entry : firstWordGrams.entrySet()) {
            if (secondWordGrams.containsKey(entry.getKey())) {
                multiplyResult += entry.getValue() * secondWordGrams.get(entry.getKey());
            }
        }
        return multiplyResult;
    }

    private static double getVectorLength(Map<String, Integer> grams, int gramNumber) {
        int vectorLength = 0;

        for (Map.Entry<String, Integer> entry : grams.entrySet()) {
            vectorLength += pow(entry.getValue(), gramNumber);
        }
        return sqrt(vectorLength);
    }

}
