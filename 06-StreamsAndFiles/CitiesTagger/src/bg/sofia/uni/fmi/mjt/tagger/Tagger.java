package bg.sofia.uni.fmi.mjt.tagger;

import java.io.*;
import java.util.*;

public class Tagger {

    private final Map<String, String> cities;
    private final Map<String, Integer> cityTags;
    private final LineProcesser xmlTagLineProcesser;
    private int overallTagsCount;

    public Tagger(Reader citiesReader) throws IOException {
        cities = new HashMap<>();
        cityTags = new HashMap<>();

        readCities(citiesReader);

        xmlTagLineProcesser = new XmlTagLineProcesser(cities);
    }

    public void tagCities(Reader text, Writer output) throws IOException {
        checkIsNotNull(text, "Text");
        checkIsNotNull(output, "Output");

        cityTags.clear();
        overallTagsCount = 0;

        try (BufferedReader bufferedReader = new BufferedReader(text);
             BufferedWriter bufferedWriter = new BufferedWriter(output)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                LineProcessingResult result = xmlTagLineProcesser.processLine(line);
                overallTagsCount += result.cityMeetingCount();
                modifyCityTags(result.cityMeetings());

                bufferedWriter.write(result.processedLine());
                bufferedWriter.flush();
            }
        }
    }

    public Collection<String> getNMostTaggedCities(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N cannot be negative!");
        }

        if (n > cityTags.size()) {
            n = cityTags.size();
        }

        List<String> sortedCitiesByNumberOfMeetings = new ArrayList<>(cityTags.keySet());
        Collections.sort(sortedCitiesByNumberOfMeetings, (a, b) -> Integer.compare(cityTags.get(b), cityTags.get(a)));

        return sortedCitiesByNumberOfMeetings.subList(0, n);
    }

    public Collection<String> getAllTaggedCities() {
        return cityTags.keySet();
    }

    public long getAllTagsCount() {
        return overallTagsCount;
    }

    private void modifyCityTags(Map<String, Integer> meetingsOfCity) {
        for (Map.Entry<String, Integer> city : meetingsOfCity.entrySet()) {
            if (cityTags.containsKey(city)) {
                cityTags.put(city.getKey(), cityTags.get(city) + city.getValue());
            } else {
                cityTags.put(city.getKey(), city.getValue());
            }
        }
    }

    private void readCities(Reader citiesReader) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(citiesReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(",");
                cities.put(tokens[0], tokens[1]);
            }
        }
    }

    private void checkIsNotNull(Object key, String keyDescription) {
        if (key == null) {
            throw new IllegalArgumentException(keyDescription + " cannot be null!");
        }
    }

}