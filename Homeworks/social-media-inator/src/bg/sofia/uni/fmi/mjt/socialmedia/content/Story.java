package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.Duration;
import java.time.LocalDateTime;

public class Story extends AbstractContent {

    private static final int EXPIRATION_HOURS_LIMIT = 24;
    private static final String STORY = "story";

    public Story(String username, LocalDateTime date, FunctionalitiesStorage tagsAndMentions) {
        super(username, date, tagsAndMentions);
    }

    @Override
    public boolean timeExpired() {
        return Duration.between(date, LocalDateTime.now()).toHours() > EXPIRATION_HOURS_LIMIT;
    }

    @Override
    public String getType() {
        return STORY;
    }
}
