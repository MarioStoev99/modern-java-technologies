package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractContent implements ContentAdditionable {

    private static final char SEPARATOR = '-';

    private static int autoIncrementedInteger;

    private final String username;
    protected final LocalDateTime date;
    private final FunctionalitiesStorage tagsAndMentions;

    private final String id;
    private int numberOfLikes;
    private int numberOfComments;

    protected AbstractContent(String username, LocalDateTime date, FunctionalitiesStorage tagsAndMentions) {
        this.username = username;
        this.date = date;
        this.tagsAndMentions = tagsAndMentions;
        this.id = username + SEPARATOR + autoIncrementedInteger++;
    }

    public static void resetAutoIncrementInteger() {
        autoIncrementedInteger = 0;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public String getId() {
        return id;
    }

    public void incrementLikes() {
        numberOfLikes++;
    }

    public void incrementComments() {
        numberOfComments++;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Collection<String> getTags() {
        return tagsAndMentions.tags();
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getMentions() {
        return tagsAndMentions.mentions();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractContent that = (AbstractContent) o;
        return numberOfLikes == that.numberOfLikes && numberOfComments == that.numberOfComments && Objects.equals(date, that.date) && Objects.equals(tagsAndMentions, that.tagsAndMentions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, tagsAndMentions, numberOfLikes, numberOfComments);
    }
}
