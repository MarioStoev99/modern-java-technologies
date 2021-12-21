package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.*;
import bg.sofia.uni.fmi.mjt.socialmedia.content.comparators.PopularityComparator;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class EvilSocialInator implements SocialMediaInatorAdditionable {

    private static final char HASHTAG = '#';

    private final Map<String, Set<Activity>> activityLogs;
    private final Map<String, AbstractContent> postsAndStories;
    private final Map<String, Integer> overallUserMentions;
    private final Map<String, List<AbstractContent>> overallTags;

    public EvilSocialInator() {
        this.activityLogs = new HashMap<>();
        this.postsAndStories = new HashMap<>();
        this.overallUserMentions = new HashMap<>();
        this.overallTags = new HashMap<>();
        AbstractContent.resetAutoIncrementInteger();
    }

    @Override
    public void register(String username) {
        checkIsNotNullOrEmpty(username, "Username");

        if (activityLogs.containsKey(username)) {
            throw new UsernameAlreadyExistsException(String.format("Username with name %s has already existed!", username));
        }

        activityLogs.put(username, new LinkedHashSet<>());
    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description) {
        publishContentChecks(username, publishedOn, description);

        FunctionalitiesStorage tagsAndMentions = FunctionalitiesGenerator.generate(description);
        AbstractContent post = new Post(username, publishedOn, tagsAndMentions);

        return publishContent(post);
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description) {
        publishContentChecks(username, publishedOn, description);

        FunctionalitiesStorage tagsAndMentions = FunctionalitiesGenerator.generate(description);
        AbstractContent story = new Story(username, publishedOn, tagsAndMentions);

        return publishContent(story);
    }

    @Override
    public void like(String username, String id) {
        checkIsNotNullOrEmpty(username, "Username");
        checkIsNotNullOrEmpty(id, "Id");

        AbstractContent content = getUserContent(username, id);
        content.incrementLikes();
        postsAndStories.put(id, content);

        String activity = LocalDateTime.now() + ": Liked a content with id " + id;
        registerActivity(username, LocalDateTime.now(), activity);
    }

    @Override
    public void comment(String username, String text, String id) {
        checkIsNotNullOrEmpty(username, "Username");
        checkIsNotNullOrEmpty(text, "Text");
        checkIsNotNullOrEmpty(id, "Id");

        AbstractContent content = getUserContent(username, id);
        content.incrementComments();
        postsAndStories.put(id, content);

        String activity = LocalDateTime.now() + ": Commented " + text + " on a content with id " + id;
        registerActivity(username, LocalDateTime.now(), activity);
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) {
        checkIfNIsNegative(n);

        List<AbstractContent> unexpiredContent = removeExpiredContent();

        if (n > unexpiredContent.size()) {
            n = unexpiredContent.size();
        }

        Collections.sort(unexpiredContent, new PopularityComparator());

        return Collections.unmodifiableList(unexpiredContent.subList(0, n));
    }

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n) {
        checkIfNIsNegative(n);
        checkIsNotNullOrEmpty(username, "Username");
        checkIfUserIsNotRegistered(username);

        List<AbstractContent> contents = removeExpiredContent();
        contents = getUsernameContents(contents, username);
        Collections.reverse(contents);

        if (n > contents.size()) {
            n = contents.size();
        }
        return Collections.unmodifiableList(contents.subList(0, n));
    }

    @Override
    public String getMostPopularUser() {
        if (activityLogs.isEmpty()) {
            throw new NoUsersException("There are not registered users yet!");
        }
        Map.Entry<Integer, String> mostPopularUser = createSortedUserMentionsOrder().firstEntry();
        return mostPopularUser == null ? null : mostPopularUser.getValue();
    }

    @Override
    public Collection<Content> findContentByTag(String tag) {
        checkIsNotNullOrEmpty(tag, "Tag");

        if (tag.charAt(0) != HASHTAG) {
            throw new IllegalArgumentException("The tag must start with '#'!");
        }

        List<Content> unexpiredContent = removeExpiredContentByTag(tag);
        return Collections.unmodifiableList(unexpiredContent);
    }

    @Override
    public List<String> getActivityLog(String username) {
        checkIsNotNullOrEmpty(username, "Username");
        checkIfUserIsNotRegistered(username);

        List<String> activityLogDescriptions = new ArrayList<>();

        Set<Activity> activities = activityLogs.get(username);
        for (Activity activity : activities) {
            activityLogDescriptions.add(activity.description());
        }
        Collections.reverse(activityLogDescriptions);
        return activityLogDescriptions;
    }

    @Override
    public int getNumberOfRegisteredUsers() {
        return activityLogs.size();
    }

    @Override
    public int getNumberOfLikesForFixedContent(String username, String id) {
        checkIsNotNullOrEmpty(username, "Username");
        checkIsNotNullOrEmpty(id, "Id");

        return postsAndStories.get(id).getNumberOfLikes();
    }

    @Override
    public int getNumberOfCommentsForFixedContent(String username, String id) {
        checkIsNotNullOrEmpty(username, "Username");
        checkIsNotNullOrEmpty(id, "Id");

        return postsAndStories.get(id).getNumberOfComments();
    }

    private void publishContentChecks(String username, LocalDateTime publishedOn, String description) {
        checkIsNotNullOrEmpty(username, "Username");
        checkIsNotNullOrEmpty(publishedOn, "Date");
        checkIsNotNullOrEmpty(description, "Description");
        checkIfUserIsNotRegistered(username);
    }

    private String publishContent(AbstractContent content) {
        addPostMentionsToTheOverallMentions(content);
        addPostTagsToOverallTags(content);

        String activity = content.getDate() + ": Created a " + content.getType() + " with id " + content.getId();
        registerActivity(content.getUsername(), content.getDate(), activity);
        postsAndStories.put(content.getId(), content);

        return content.getId();
    }

    private void registerActivity(String username, LocalDateTime publishedOn, String activity) {
        Set<Activity> activities = activityLogs.get(username);
        activities.add(new Activity(publishedOn, activity));
        activityLogs.put(username, activities);
    }

    private List<AbstractContent> getUsernameContents(List<AbstractContent> contents, String username) {
        List<AbstractContent> usernameContents = new ArrayList<>();

        for (AbstractContent content : contents) {
            if (content.getUsername().equals(username)) {
                usernameContents.add(content);
            }
        }

        return usernameContents;
    }

    private TreeMap<Integer, String> createSortedUserMentionsOrder() {
        TreeMap<Integer, String> sortedUserMentions = new TreeMap<>(Collections.reverseOrder());

        for (Map.Entry<String, Integer> entry : overallUserMentions.entrySet()) {
            sortedUserMentions.put(entry.getValue(), entry.getKey());
        }

        return sortedUserMentions;
    }

    private void addPostMentionsToTheOverallMentions(AbstractContent content) {
        for (Map.Entry<String, Integer> entry : content.getMentions().entrySet()) {
            if (overallUserMentions.containsKey(entry.getKey())) {
                overallUserMentions.put(entry.getKey(), overallUserMentions.get(entry.getKey()) + entry.getValue());
            } else {
                overallUserMentions.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void addPostTagsToOverallTags(AbstractContent content) {
        for (String tag : content.getTags()) {
            if (!overallTags.containsKey(tag)) {
                overallTags.put(tag, new ArrayList<>());
            }
            List<AbstractContent> contents = overallTags.get(tag);
            contents.add(content);
            overallTags.put(tag, contents);
        }
    }

    private AbstractContent getUserContent(String username, String id) {
        checkIfUserIsNotRegistered(username);

        if (!postsAndStories.containsKey(id)) {
            throw new ContentNotFoundException("Provided content is not registered!");
        }

        return postsAndStories.get(id);
    }

    private List<Content> removeExpiredContentByTag(String tagProvidedByUser) {
        List<Content> unexpiredContents = new ArrayList<>();
        if (!overallTags.containsKey(tagProvidedByUser)) {
            return unexpiredContents;
        }

        for (AbstractContent content : overallTags.get(tagProvidedByUser)) {
            if (!content.timeExpired()) {
                unexpiredContents.add(content);
            }
        }
        return unexpiredContents;
    }

    private List<AbstractContent> removeExpiredContent() {
        List<AbstractContent> unexpiredContents = new ArrayList<>();

        for (Map.Entry<String, AbstractContent> content : postsAndStories.entrySet()) {
            if (!content.getValue().timeExpired()) {
                unexpiredContents.add(content.getValue());
            }
        }
        return unexpiredContents;
    }

    private void checkIsNotNullOrEmpty(Object key, String keyDescription) {
        if (key == null || key instanceof String && ((String) key).isEmpty()) {
            throw new IllegalArgumentException(keyDescription + " cannot be null or empty!");
        }
    }

    private void checkIfNIsNegative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Provided value for 'n' cannot be negative");
        }
    }

    private void checkIfUserIsNotRegistered(String username) {
        if (!activityLogs.containsKey(username)) {
            throw new UsernameNotFoundException("Provided username is not registered!");
        }
    }

}
