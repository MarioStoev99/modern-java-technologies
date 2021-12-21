package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.util.Map;
import java.util.Set;

public record FunctionalitiesStorage(Set<String> tags, Map<String, Integer> mentions) {
}
