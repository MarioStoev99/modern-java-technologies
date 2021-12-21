package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.exception.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.exception.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Netflix implements StreamingService {

    private final Account[] accounts;
    private final Streamable[] streamableContent;
    private int totalWatchedTime;
    private Streamable mostViewedStream;

    public Netflix(Account[] accounts, Streamable[] streamableContent) {
        this.accounts = accounts;
        this.streamableContent = streamableContent;
    }

    @Override
    public void watch(Account user, String videoContentName) throws ContentUnavailableException {
        checkIsNotNull(user, "User");
        checkIsNotNull(videoContentName, "Video content name");
        checkUserRegistered(user);

        Streamable stream = getStream(videoContentName);
        if (stream == null) {
            throw new ContentNotFoundException("This stream is not registered in the system");
        }
        checkAgeRestriction(user, stream);

        totalWatchedTime += stream.getDuration();

        setMostViewedStream(stream);
    }

    @Override
    public Streamable findByName(String videoContentName) {
        checkIsNotNull(videoContentName, "Video content name");

        return getStream(videoContentName);
    }

    @Override
    public Streamable mostViewed() {
        return mostViewedStream;
    }

    @Override
    public int totalWatchedTimeByUsers() {
        return totalWatchedTime;
    }

    private void checkUserRegistered(Account user) {
        for (Account account : accounts) {
            if (account.username().equals(user.username())) {
                return;
            }
        }
        throw new UserNotFoundException("This user doesn't exist in the system");
    }

    private void checkAgeRestriction(Account user, Streamable stream) throws ContentUnavailableException {
        int age = (int) ChronoUnit.YEARS.between(user.localDateTime(), LocalDateTime.now());
        PgRating rating = stream.getRating();

        if (age > 17) {
            return;
        }

        String message = "This stream is not recommended to people who are under %d";
        if (rating == PgRating.NC17 && age < 18) {
            throw new ContentUnavailableException(String.format(message, 18));
        }
        if (rating == PgRating.PG13 && age < 14) {
            throw new ContentUnavailableException(String.format(message, 14));
        }
    }

    private void setMostViewedStream(Streamable stream) {
        stream.incrementViews();

        if (mostViewedStream == null) {
            mostViewedStream = stream;
            return;
        }

        mostViewedStream = mostViewedStream.getViews() < stream.getViews() ? stream : mostViewedStream;
    }

    private void checkIsNotNull(Object key, String keyDescription) {
        if (key == null || key instanceof String && key.toString().isEmpty()) {
            throw new IllegalArgumentException(keyDescription + " cannot be null or empty!");
        }
    }

    private Streamable getStream(String videoContentName) {
        for (Streamable stream : streamableContent) {
            if (videoContentName.equals(stream.getTitle())) {
                return stream;
            }
        }
        return null;
    }

}
