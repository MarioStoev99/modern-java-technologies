package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;

public interface ContentAdditionable extends Content {

    void incrementLikes();

    boolean timeExpired();

    void incrementComments();

    LocalDateTime getDate();

    String getType();
}
