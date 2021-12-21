package bg.sofia.uni.fmi.mjt.spotify;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.*;


public class SpotifyExplorerTest {

    private static SpotifyExplorer spotifyExplorer;

    @BeforeClass
    public static void setUpClass() throws IOException {
        String dataSet = "id,artists,name,year,popularity,duration_ms,tempo,loudness,valence,acousticness,danceability,energy,liveness,speechiness,explicit\n" +
                "4BJqT0PrAfrxzMOxytFOIz,['Sergei Rachmaninoff'; 'James Levine'; 'Berliner Philharmoniker'],Piano Concerto No. 3 in D Minor Op. 30: III. Finale. Alla breve,1920,4,831667,80.954,-20.096,0.0594,0.982,0.279,0.211,0.665,0.0366,0\n" +
                "4CjnVzvSu86vqI5gY8SjPx,['Félix Mayol'],Elle Vendait Des P'tits Gateaux,1983,0,151573,129.153,-14.07,0.969,0.987,0.716,0.301,0.277,0.105,0\n" +
                "4iy1fsAHBgR2STJjPDhjY2,['Fortugé'],Mes Parents Sont Venus Me Chercher,1986,0,183840,97.337,-14.764,0.62,0.995,0.554,0.204,0.153,0.0598,0\n" +
                "1t2zq9TXj7mlSX8fcKwCbm,['Louis Armstrong'],Blue Again,1989,4,187707,110.346,-10.794,0.537,0.978,0.598,0.31,0.0766,0.0714,0\n" +
                "7BGXSpysMMs2zBS0uVh2mj,['Louis Armstrong & His Hot Seven'],Squeeze Me,1989,4,202493,91.574,-13.213,0.548,0.971,0.733,0.179,0.124,0.0564,0\n" +
                "0fYbhMj96zG9fpUK10e8kB,['Harold Melvin & The Blue Notes'],Don't Leave Me This Way,1995,49,366573,120.197,-9.961,0.544,0.403,0.472,0.679,0.109,0.0623,0\n" +
                "4GR1oh1QEZ5LAaj2OQzvuY,['Grupo Galé'],Ya No Te Puedo Amar,1997,60,306707,87.2,-6.586,0.918,0.678,0.72,0.686,0.14,0.033,0\n" +
                "2gXI6TBLhmCNy09NioMHdI,['Nas'],Nas Is Coming,1996,44,341000,90.947,-6.42,0.601,0.166,0.663,0.684,0.367,0.146,1";

        Reader reader = new StringReader(dataSet);
        spotifyExplorer = new SpotifyExplorer(reader);
    }

    @Test
    public void testGroupSpotifyTracksByYear1983() {
        Map<Integer, Set<SpotifyTrack>> actual = spotifyExplorer.groupSpotifyTracksByYear();

        String spotifyTrack = "4CjnVzvSu86vqI5gY8SjPx,['Félix Mayol'],Elle Vendait Des P'tits Gateaux,1983,0,151573,129.153,-14.07,0.969,0.987,0.716,0.301,0.277,0.105,0";
        Set<SpotifyTrack> expetedSpotifyTracks = Set.of(SpotifyTrack.of(spotifyTrack));

        Map<Integer, Set<SpotifyTrack>> expected = new HashMap<>();
        expected.put(1983, expetedSpotifyTracks);

        assertTrue(expected.get(1983).containsAll(actual.get(1983)));
    }

    @Test
    public void testGroupSpotifyTracksByYear1989() {
        Map<Integer, Set<SpotifyTrack>> actual = spotifyExplorer.groupSpotifyTracksByYear();

        Map<Integer, Set<SpotifyTrack>> expected = new HashMap<>();

        String spotifyTrack1 = "7BGXSpysMMs2zBS0uVh2mj,['Louis Armstrong & His Hot Seven'],Squeeze Me,1989,4,202493,91.574,-13.213,0.548,0.971,0.733,0.179,0.124,0.0564,0";
        String spotifyTrack2 = "1t2zq9TXj7mlSX8fcKwCbm,['Louis Armstrong'],Blue Again,1989,4,187707,110.346,-10.794,0.537,0.978,0.598,0.31,0.0766,0.0714,0";
        Set<SpotifyTrack> expetedSpotifyTracks = Set.of(SpotifyTrack.of(spotifyTrack1), SpotifyTrack.of(spotifyTrack2));

        expected.put(1989, expetedSpotifyTracks);

        assertTrue(expected.get(1989).containsAll(actual.get(1989)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArtistActiveYearsThrowIllegalArgumentException() {
        spotifyExplorer.getArtistActiveYears(null);
    }

    @Test
    public void testGetArtistActiveYears() {
        assertEquals(1, spotifyExplorer.getArtistActiveYears("Louis Armstrong"));
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80s() {
        List<SpotifyTrack> actual = spotifyExplorer.getTopNHighestValenceTracksFromThe80s(2);

        String value = "4CjnVzvSu86vqI5gY8SjPx,['Félix Mayol'],Elle Vendait Des P'tits Gateaux,1983,0,151573,129.153,-14.07,0.969,0.987,0.716,0.301,0.277,0.105,0";
        String value1 = "4iy1fsAHBgR2STJjPDhjY2,['Fortugé'],Mes Parents Sont Venus Me Chercher,1986,0,183840,97.337,-14.764,0.62,0.995,0.554,0.204,0.153,0.0598,0";

        List<SpotifyTrack> expected = List.of(SpotifyTrack.of(value), SpotifyTrack.of(value1));

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTopNHighestValenceTracksFromThe80sPassNegativeNumberAsArgument() {
        spotifyExplorer.getTopNHighestValenceTracksFromThe80s(-1);
    }

    @Test
    public void testGetMostPopularTrackFromThe90s() {
        SpotifyTrack actual = spotifyExplorer.getMostPopularTrackFromThe90s();

        String spotifyTrack = "4GR1oh1QEZ5LAaj2OQzvuY,['Grupo Galé'],Ya No Te Puedo Amar,1997,60,306707,87.2,-6.586,0.918,0.678,0.72,0.686,0.14,0.033,0";
        SpotifyTrack expected = SpotifyTrack.of(spotifyTrack);

        assertEquals(expected, actual);
    }

    @Test(expected = NoSuchElementException.class)
    public void testgetMostPopularTrackFromThe90sNoSuchElement() {
        SpotifyExplorer spotifyExplorerForTheCurrentTest = new SpotifyExplorer(new StringReader(""));
        spotifyExplorerForTheCurrentTest.getMostPopularTrackFromThe90s();
    }

    @Test
    public void testGetNumberOfLongerTracksBeforeYear() {
        long actual = spotifyExplorer.getNumberOfLongerTracksBeforeYear(3, 2021);
        long expected = 7;
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOfLongerTracksBeforeYearInvalidMinutesAsArgument() {
        long actual = spotifyExplorer.getNumberOfLongerTracksBeforeYear(-1, 2021);
        long expected = 6;
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOfLongerTracksBeforeYearInvalidYearsAsArgument() {
        long actual = spotifyExplorer.getNumberOfLongerTracksBeforeYear(3, -1);
        long expected = 6;
        assertEquals(expected, actual);
    }

    @Test
    public void testGetTheLoudestTrackInYear() {
        Optional<SpotifyTrack> optionalSpotifyTrack = spotifyExplorer.getTheLoudestTrackInYear(1989);
        SpotifyTrack actual = optionalSpotifyTrack.get();

        String spotifyTrackExpected = "1t2zq9TXj7mlSX8fcKwCbm,['Louis Armstrong'],Blue Again,1989,4,187707,110.346,-10.794,0.537,0.978,0.598,0.31,0.0766,0.0714,0";
        SpotifyTrack expected = SpotifyTrack.of(spotifyTrackExpected);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTheLoudestTrackInYearPassInvalidYearAsArgument() {
        spotifyExplorer.getTheLoudestTrackInYear(-1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetAllSpotifyTracks() {
        Collection<SpotifyTrack> spotifyTracksCollection = spotifyExplorer.getAllSpotifyTracks();
        spotifyTracksCollection.add(SpotifyTrack.of("2gXI6TBLhmCNy09NioMHdI,['Nas'],Nas Is Coming,1996,44,341000,90.947,-6.42,0.601,0.166,0.663,0.684,0.367,0.146,1"));
    }


    @Test
    public void testGetExplicitSpotifyTracks() {
        Collection<SpotifyTrack> actual = spotifyExplorer.getExplicitSpotifyTracks();

        Collection<SpotifyTrack> expected = List.of(SpotifyTrack.of("2gXI6TBLhmCNy09NioMHdI,['Nas'],Nas Is Coming,1996,44,341000,90.947,-6.42,0.601,0.166,0.663,0.684,0.367,0.146,1"));

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetExplicitSpotifyTracksTryToModifyCollection() {
        Collection<SpotifyTrack> spotifyTracksCollection = spotifyExplorer.getExplicitSpotifyTracks();
        spotifyTracksCollection.clear();
    }

}
