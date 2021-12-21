package bg.sofia.uni.fmi.mjt.netflix;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.Episode;
import bg.sofia.uni.fmi.mjt.netflix.content.Movie;
import bg.sofia.uni.fmi.mjt.netflix.content.Series;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.exception.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.exception.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.platform.Netflix;
import bg.sofia.uni.fmi.mjt.netflix.platform.StreamingService;

import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        Streamable dontBreathe = new Movie("Don't Breathe 2", Genre.HORROR, PgRating.PG13, 98);
        Streamable escapeRoom = new Movie("Escape Room", Genre.HORROR, PgRating.PG13, 100);
        Episode[] episodes = new Episode[4];
        Episode e01 = new Episode("The Cold", 45);
        Episode e02 = new Episode("Prelude", 35);
        Episode e03 = new Episode("Card Game", 50);
        Episode e04 = new Episode("Cold war", 40);
        episodes[0] = e01;
        episodes[1] = e02;
        episodes[2] = e03;
        episodes[3] = e04;
        Streamable blackSummer = new Series("Black Summer", Genre.ACTION, PgRating.PG13, episodes);
        Streamable[] streams = new Streamable[3];
        streams[0] = dontBreathe;
        streams[1] = escapeRoom;
        streams[2] = blackSummer;
        Account account = new Account("Mario", LocalDateTime.of(1999, Month.FEBRUARY, 11, 11, 11));
        Account account1 = new Account("Petur", LocalDateTime.of(1999, Month.FEBRUARY, 11, 11, 11));
        Account[] accounts = new Account[2];
        accounts[0] = account;
        accounts[1] = account1;
        StreamingService streamingService = new Netflix(accounts, streams);
        try {
            streamingService.watch(account, "Don't Breathe 2");
            streamingService.watch(account1, "Escape Room");
            streamingService.watch(account1, "Black Summer");
            streamingService.watch(account1, "Don't Breathe 2");
            Streamable stream = streamingService.findByName("Escape Room");
            System.out.println(stream.getTitle());
            System.out.println(streamingService.totalWatchedTimeByUsers());
            System.out.println(streamingService.mostViewed().getTitle());
        } catch (ContentUnavailableException | UserNotFoundException | ContentNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
