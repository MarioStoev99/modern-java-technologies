package bg.sofia.uni.fmi.mjt.tagger;

import java.util.Map;

public record LineProcessingResult(String processedLine, int cityMeetingCount, Map<String, Integer> cityMeetings) {
}
