package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public abstract class AbstractVideoContent implements Streamable {

    private final String name;
    private final Genre genre;
    private final PgRating rating;
    private int views;

    protected AbstractVideoContent(String name, Genre genre, PgRating rating) {
        this.name = name;
        this.genre = genre;
        this.rating = rating;
    }

    @Override
    public String getTitle() {
        return name;
    }

    public abstract int getDuration();

    @Override
    public PgRating getRating() {
        return rating;
    }

    @Override
    public int getViews() {
        return views;
    }

    @Override
    public void incrementViews() {
        views++;
    }
}
