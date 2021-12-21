package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.Duration;
import java.time.LocalDateTime;

public class Post extends AbstractContent {

    private static final int EXPIRATION_DAYS_LIMIT = 30;
    private static final String POST = "post";

    public Post(String username, LocalDateTime date, FunctionalitiesStorage tagsAndMentions) {
        super(username, date, tagsAndMentions);
    }

    @Override
    public boolean timeExpired() {
        return Duration.between(date, LocalDateTime.now()).toDays() > EXPIRATION_DAYS_LIMIT;
    }

    @Override
    public String getType() {
        return POST;
    }

}
