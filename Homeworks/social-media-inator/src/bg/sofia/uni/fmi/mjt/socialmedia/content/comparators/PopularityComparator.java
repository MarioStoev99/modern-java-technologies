package bg.sofia.uni.fmi.mjt.socialmedia.content.comparators;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;

import java.util.Comparator;

public class PopularityComparator implements Comparator<Content> {

    @Override
    public int compare(Content firstContent, Content secondContent) {
        int firstContentLikesAndComments = firstContent.getNumberOfComments() + firstContent.getNumberOfLikes();
        int secondContentLikesAndComments = secondContent.getNumberOfComments() + secondContent.getNumberOfLikes();
        return Integer.compare(secondContentLikesAndComments,firstContentLikesAndComments);
    }
}
