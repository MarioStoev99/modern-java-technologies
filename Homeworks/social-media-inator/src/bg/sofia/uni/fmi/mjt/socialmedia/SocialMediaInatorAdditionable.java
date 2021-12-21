package bg.sofia.uni.fmi.mjt.socialmedia;

public interface SocialMediaInatorAdditionable extends SocialMediaInator {

    int getNumberOfRegisteredUsers();

    int getNumberOfLikesForFixedContent(String username, String id);

    int getNumberOfCommentsForFixedContent(String username, String id);

}
