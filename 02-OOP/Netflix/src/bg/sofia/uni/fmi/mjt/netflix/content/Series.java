package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public class Series extends AbstractVideoContent {
    private final Episode[] episodes;

    public Series(String name, Genre genre, PgRating rating, Episode[] episodes) {
        super(name, genre, rating);
        this.episodes = episodes;
    }

    @Override
    public int getDuration() {
        int overallDuration = 0;
        for (Episode episode : episodes) {
            overallDuration += episode.duration();
        }
        return overallDuration;
    }
}
