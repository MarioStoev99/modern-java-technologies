package bg.sofia.uni.fmi.mjt.spotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class SpotifyExplorer {

    private static final int minuteInSeconds = 60;
    private static final int secondInMilliseconds = 1000;

    private List<SpotifyTrack> tracks;

    public SpotifyExplorer(Reader dataInput) {
        this.tracks = new LinkedList<>();
        this.tracks.add(null);

        try (BufferedReader bufferedReader = new BufferedReader(dataInput)) {
            this.tracks = bufferedReader.lines()
                    .skip(1)
                    .map(SpotifyTrack::of)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("The data cannot be load", e);
        }
    }

    public Collection<SpotifyTrack> getAllSpotifyTracks() {
        return Collections.unmodifiableCollection(tracks);
    }

    public Collection<SpotifyTrack> getExplicitSpotifyTracks() {
        return Collections.unmodifiableCollection(tracks.stream().filter(SpotifyTrack::explicit).collect(Collectors.toList()));
    }

    public Map<Integer, Set<SpotifyTrack>> groupSpotifyTracksByYear() {
        return tracks.stream()
                .collect(Collectors.groupingBy(SpotifyTrack::year, Collectors.toSet()));
    }

    public int getArtistActiveYears(String artist) {
        checkIsNotNull(artist, "Artist");

        List<Integer> spotifyTracksYearsForTheCurrentArtist = tracks.stream()
                .filter(track -> track.artists() != null)
                .filter(track -> track.artists().contains(artist))
                .map(SpotifyTrack::year)
                .collect(Collectors.toList());
        if (spotifyTracksYearsForTheCurrentArtist.size() == 0) {
            return 0;
        }
        return differenceBetweenMaxAndMinActiveYears(spotifyTracksYearsForTheCurrentArtist);

    }

    public List<SpotifyTrack> getTopNHighestValenceTracksFromThe80s(int n) {
        checkIsNotNegative(n, "N");

        return tracks.stream()
                .filter(track -> track.year() >= 1980 && track.year() <= 1989)
                .sorted(Comparator.comparing(SpotifyTrack::valence).reversed())
                .limit(n)
                .collect(Collectors.toUnmodifiableList());
    }

    public SpotifyTrack getMostPopularTrackFromThe90s() {
        return tracks.stream()
                .filter(track -> track.year() >= 1990 && track.year() <= 1999)
                .max(Comparator.comparing(SpotifyTrack::popularity))
                .orElseThrow(NoSuchElementException::new);
    }

    public long getNumberOfLongerTracksBeforeYear(int minutes, int year) {
        checkIsNotNegative(minutes, "Minutes");
        checkIsNotNegative(year, "Year");

        return tracks.stream()
                .filter(track -> track.year() < year)
                .filter(p -> ((p.duration() / secondInMilliseconds) / minuteInSeconds) >= minutes)
                .count();
    }

    public Optional<SpotifyTrack> getTheLoudestTrackInYear(int year) {
        checkIsNotNegative(year, "Year");

        return tracks.stream()
                .filter(track -> track.year() == year)
                .max(Comparator.comparing(SpotifyTrack::loudness));
    }

    private int differenceBetweenMaxAndMinActiveYears(List<Integer> spotifyTracksYearsForTheCurrentArtist) {
        int maxActiveYearForCurrentArtist = spotifyTracksYearsForTheCurrentArtist.stream()
                .mapToInt(year -> year)
                .max()
                .orElseThrow(NoSuchElementException::new);

        int minActiveYearForCurrentArtist = spotifyTracksYearsForTheCurrentArtist.stream()
                .mapToInt(year -> year)
                .min()
                .orElseThrow(NoSuchElementException::new);

        return maxActiveYearForCurrentArtist - minActiveYearForCurrentArtist + 1;
    }

    private void checkIsNotNull(Object key, String keyDescription) {
        if (key == null) {
            throw new IllegalArgumentException(keyDescription + " cannot be null!");
        }
    }

    private void checkIsNotNegative(int key, String keyDescription) {
        if (key < 0) {
            throw new IllegalArgumentException(keyDescription + " cannot be negative!");
        }
    }
}