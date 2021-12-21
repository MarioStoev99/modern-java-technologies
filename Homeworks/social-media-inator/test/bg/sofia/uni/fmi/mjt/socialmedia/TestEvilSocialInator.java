package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.FunctionalitiesStorage;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Post;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestEvilSocialInator {

    private static final LocalDateTime date = LocalDateTime.of(2021, 10, 25, 1, 1, 1);
    private static final LocalDateTime date1 = LocalDateTime.of(2021, 10, 23, 1, 1, 1);
    private static final LocalDateTime date2 = LocalDateTime.of(2021, 10, 27, 1, 1, 1);
    private static final String description = "#programming #unittests #datastructures @Petyr @Kristiyan @Simona";
    private static final String description1 = "#oxoboxo #123 #456 @Kristiyan @Simona";
    private static final String description2 = "#programming #unittests #datastructures @Simona";

    private EvilSocialInator evilSocialInator;

    @Before
    public void setUp() {
        evilSocialInator = new EvilSocialInator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNull() {
        evilSocialInator.register(null);
    }

    @Test
    public void testRegisterUserSuccess() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        assertEquals(4, evilSocialInator.getNumberOfRegisteredUsers());
    }

    @Test(expected = UsernameAlreadyExistsException.class)
    public void testRegisterUserWhoHaveAlreadyBeenRegistered() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Mario");
    }

    @Test
    public void testPublishPostSuccess() {
        evilSocialInator.register("Mario");
        String actualId = evilSocialInator.publishPost("Mario", date, description);
        assertTrue("Mario-0".equals(actualId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPublishPostUsernameNull() {
        evilSocialInator.publishPost(null, date, description);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPublishPostDescriptionNull() {
        evilSocialInator.publishPost("Mario", null, description);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPublishPostPublishedOnNull() {
        evilSocialInator.publishPost("Mario", date, null);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testPublishPostUserIsNotRegisteredInThePlatform() {
        evilSocialInator.publishPost("Violeta", date, description);
    }

    @Test
    public void testPublishStorySuccess() {
        evilSocialInator.register("Mario");
        String id1 = evilSocialInator.publishStory("Mario", date1, description1);
        assertTrue("Mario-0".equals(id1));
    }

    @Test
    public void testLikePostOrStory() {
        evilSocialInator.register("Mario");
        String id = evilSocialInator.publishPost("Mario", date, description);
        evilSocialInator.like("Mario", id);

        assertEquals(1, evilSocialInator.getNumberOfLikesForFixedContent("Mario", "Mario-0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLikeUsernameNull() {
        evilSocialInator.like(null, "Mario-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLikeIdNull() {
        evilSocialInator.like("Mario", null);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLikeUsernameIsNotRegistered() {
        evilSocialInator.like("Violeta", "Mario-1");
    }

    @Test(expected = ContentNotFoundException.class)
    public void testLikeContentIsNotRegistered() {
        evilSocialInator.register("Mario");
        evilSocialInator.like("Mario", "Mario-112122");
    }

    @Test
    public void testCommentPostOrStorySuccess() {
        evilSocialInator.register("Mario");
        String id = evilSocialInator.publishPost("Mario", date, description);

        evilSocialInator.comment("Mario", "First comment", id);
        assertEquals(1, evilSocialInator.getNumberOfCommentsForFixedContent("Mario", "Mario-0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommentTextIsNull() {
        evilSocialInator.comment("Mario", null, "Mario-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNMostPopularContentWithNegativeNumber() {
        evilSocialInator.getNMostPopularContent(-1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetNMostPopularContentThrowExceptionWhenTryToModifyTheResult() {
        Collection<Content> actual = evilSocialInator.getNMostPopularContent(1);
        actual.clear();
    }

    @Test
    public void testGetNMostPopularContentSuccess() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        String id = evilSocialInator.publishPost("Mario", date, description);
        String id1 = evilSocialInator.publishPost("Mario", date1, description1);
        String id2 = evilSocialInator.publishPost("Petyr", date2, description2);
        String id3 = evilSocialInator.publishStory("Kristiyan", date, description1);

        evilSocialInator.like("Mario", id1);
        evilSocialInator.like("Kristiyan", id1);
        evilSocialInator.like("Simona", id2);
        evilSocialInator.like("Mario", id3);
        evilSocialInator.like("Simona", id1);
        evilSocialInator.like("Petyr", id2);
        evilSocialInator.comment("Mario", "First comment", id);

        Collection<Content> actual = evilSocialInator.getNMostPopularContent(1);

        Set<String> tags = Set.of("#oxoboxo", "#123", "#456");
        Map<String, Integer> mentions = Map.of("@Kristiyan", 1, "@Simona", 1);
        Post post = new Post("Mario", date1, new FunctionalitiesStorage(tags, mentions));
        post.incrementLikes();
        post.incrementLikes();
        post.incrementLikes();
        Collection<Content> expected = Set.of(post);

        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void testGetNMostRecentContentSuccess() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        evilSocialInator.publishPost("Mario", date, description);
        evilSocialInator.publishPost("Mario", date1, description1);
        evilSocialInator.publishPost("Petyr", date2, description2);
        evilSocialInator.publishStory("Kristiyan", date, description1);

        Collection<Content> actual = evilSocialInator.getNMostRecentContent("Mario", 1);

        Set<String> tags = Set.of("#programming", "#unittests", "#datastructures");
        Map<String, Integer> mentions = Map.of("@Petyr", 1, "@Kristiyan", 1, "@Simona", 1);
        Post post = new Post("Mario", date, new FunctionalitiesStorage(tags, mentions));

        Collection<Content> expected = Set.of(post);

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNMostRecentContentWithNegativeN() {
        evilSocialInator.getNMostRecentContent("Mario", -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNMostRecentContentNull() {
        evilSocialInator.getNMostRecentContent(null, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetNMostRecentContentTryToModifyTheResult() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        evilSocialInator.publishPost("Mario", date, description);
        evilSocialInator.publishPost("Mario", date1, description1);
        evilSocialInator.publishPost("Petyr", date2, description2);
        evilSocialInator.publishStory("Kristiyan", date, description1);
        Collection<Content> actual = evilSocialInator.getNMostRecentContent("Mario", 1);
        actual.clear();
    }

    @Test
    public void testGetNMostRecentNisGreaterThanTheContentsInTheSystem() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        evilSocialInator.publishPost("Mario", date, description);
        evilSocialInator.publishPost("Mario", date1, description1);

        Collection<Content> actual = evilSocialInator.getNMostRecentContent("Mario", 10);
        Set<String> tags1 = Set.of("#oxoboxo", "#123", "#456");
        Set<String> tags = Set.of("#programming", "#unittests", "#datastructures");
        Map<String, Integer> mentions = Map.of("@Petyr", 1, "@Kristiyan", 1, "@Simona", 1);
        Post post = new Post("Mario", date, new FunctionalitiesStorage(tags, mentions));
        Post post1 = new Post("Petyr", date1, new FunctionalitiesStorage(tags1, Map.of("@Simona", 1, "@Kristiyan", 1)));
        Collection<Content> expected = List.of(post, post1);

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void testGetMostPopularUserSuccess() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        evilSocialInator.publishPost("Mario", date, description);
        evilSocialInator.publishPost("Mario", date1, description1);
        evilSocialInator.publishPost("Petyr", date2, description2);
        evilSocialInator.publishStory("Kristiyan", date, description1);

        assertEquals("@Simona", evilSocialInator.getMostPopularUser());
    }

    @Test
    public void testGetMostPopularUserWithoutContentsInTheSystem() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");


        assertEquals(null, evilSocialInator.getMostPopularUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindContentByTagWithoutSpecialCharacterInTheBeginning() {
        evilSocialInator.findContentByTag("Simona");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindContentByTagWithNullAsArgument() {
        evilSocialInator.findContentByTag(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFindContentByTagTryToModifyTheResult() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        evilSocialInator.publishPost("Mario", date, description);
        evilSocialInator.publishPost("Mario", date1, description1);
        evilSocialInator.publishPost("Petyr", date2, description2);
        evilSocialInator.publishStory("Kristiyan", date, description1);
        Collection<Content> actual = evilSocialInator.findContentByTag("#unittests");
        actual.clear();
    }

    @Test
    public void testFindContentByTagSuccess() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        evilSocialInator.publishPost("Mario", date, description);
        evilSocialInator.publishPost("Mario", date1, description1);
        evilSocialInator.publishPost("Petyr", date2, description2);
        evilSocialInator.publishStory("Kristiyan", date, description1);

        Set<String> tags = Set.of("#programming", "#unittests", "#datastructures");
        Map<String, Integer> mentions = Map.of("@Petyr", 1, "@Kristiyan", 1, "@Simona", 1);
        Post post = new Post("Mario", date, new FunctionalitiesStorage(tags, mentions));
        Post post1 = new Post("Petyr", date2, new FunctionalitiesStorage(tags, Map.of("@Simona", 1)));

        Collection<Content> expected = Set.of(post, post1);

        Collection<Content> actual = evilSocialInator.findContentByTag("#unittests");

        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void testFindContentByTagWhenTheProvidedTagDoesNotExist() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        evilSocialInator.publishPost("Mario", date, description);
        evilSocialInator.publishPost("Mario", date1, description1);
        evilSocialInator.publishPost("Petyr", date2, description2);
        evilSocialInator.publishStory("Kristiyan", date, description1);

        Collection<Content> expected = new ArrayList<>();

        Collection<Content> actual = evilSocialInator.findContentByTag("#todayissaturday");

        assertTrue(expected.containsAll(actual));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivityLogPassNullAsArgument() {
        evilSocialInator.getActivityLog(null);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testGetActivityLogWithUserWhoDoesNotExistInThePlatform() {
        evilSocialInator.getActivityLog("Ivailo");
    }

    @Test
    public void testGetActivityLogSuccess() {
        evilSocialInator.register("Mario");
        evilSocialInator.register("Petyr");
        evilSocialInator.register("Kristiyan");
        evilSocialInator.register("Simona");

        String id = evilSocialInator.publishPost("Mario", date1, description);
        String id1 = evilSocialInator.publishPost("Mario", date, description1);
        String id2 = evilSocialInator.publishPost("Petyr", date2, description2);
        String id3 = evilSocialInator.publishStory("Kristiyan", date, description1);

        evilSocialInator.like("Mario", id1);
        evilSocialInator.like("Kristiyan", id1);
        evilSocialInator.like("Simona", id2);
        evilSocialInator.like("Mario", id3);
        evilSocialInator.like("Simona", id1);
        evilSocialInator.like("Petyr", id2);
        evilSocialInator.comment("Mario", "First comment", id);

        List<String> expected = new ArrayList<>();
        expected.add("Commented First comment on a content with id " + id);
        expected.add("Liked a content with id " + id3);
        expected.add("Liked a content with id " + id1);
        expected.add("Created a post with id Mario-1");
        expected.add("Created a post with id Mario-0");

        List<String> actualActivities = evilSocialInator.getActivityLog("Mario");
        List<String> actualActivitiesWithoutDate = new ArrayList<>();
        for (String activity : actualActivities) {
            String[] parts = activity.split(" ", 2);
            actualActivitiesWithoutDate.add(parts[1]);
        }

        assertArrayEquals(expected.toArray(), actualActivitiesWithoutDate.toArray());
    }
}
