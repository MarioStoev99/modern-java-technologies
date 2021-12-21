package bg.sofia.uni.fmi.mjt.tagger;

import java.util.HashMap;
import java.util.Map;

public class XmlTagLineProcesser implements LineProcesser {

    private final Map<String, String> cities;
    private final Map<String, Integer> cityOccurences;

    public XmlTagLineProcesser(Map<String, String> cities) {
        this.cities = cities;
        this.cityOccurences = new HashMap<>();
    }

    @Override
    public LineProcessingResult processLine(String line) {
        String[] words = line.split(" ");
        int tagsCounter = 0;

        for (String word : words) {
            String updatedWord = removeUnnecessaryCharacters(word);
            String city = transformFirstLetterToUpperTheRestToLowerCase(updatedWord);

            if (cities.containsKey(city)) {
                if (!cityOccurences.containsKey(city)) {
                    line = line.replace(updatedWord, "<city country=\"" + cities.get(city) + "\">" + updatedWord + "</city>");
                    cityOccurences.put(city, 0);
                }
                cityOccurences.put(city, cityOccurences.get(city) + 1);
                ++tagsCounter;
            }
        }
        return new LineProcessingResult(line, tagsCounter, cityOccurences);
    }

    private String removeUnnecessaryCharacters(String word) {
        return word.replace("\'s", "").replaceAll("[^a-zA-Z]", "");
    }

    private String transformFirstLetterToUpperTheRestToLowerCase(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

}
