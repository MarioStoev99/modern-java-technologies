package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.util.*;

public class FunctionalitiesGenerator {

    public static FunctionalitiesStorage generate(String description) {
        Set<String> tags = new HashSet<>();
        Map<String, Integer> mentions = new HashMap<>();
        String[] tagsAndMentions = description.split(" ");
        for (String mentionOrTag : tagsAndMentions) {
            if (mentionOrTag.charAt(0) == '@') {
                incrementMentions(mentions, mentionOrTag);
            } else {
                tags.add(mentionOrTag);
            }
        }
        return new FunctionalitiesStorage(tags,mentions);
    }

    private static void incrementMentions(Map<String, Integer> mentions, String mention) {
        if (mentions.containsKey(mention)) {
            mentions.put(mention, mentions.get(mention) + 1);
        } else {
            mentions.put(mention, 1);
        }
    }
}
