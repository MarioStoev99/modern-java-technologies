package bg.sofia.uni.fmi.mjt.revolut;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.account.Currency;
import bg.sofia.uni.fmi.mjt.revolut.account.BGNAccount;
import bg.sofia.uni.fmi.mjt.revolut.account.EURAccount;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;
import bg.sofia.uni.fmi.mjt.revolut.card.VirtualOneTimeCard;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Account from = new BGNAccount("1234-5678",150);
        LocalDate date1 = LocalDate.of(2022,11,11);
        Card card1 = new VirtualOneTimeCard("1234-5678",1234,date1);

        Account to = new EURAccount("8765-4321",150);
        LocalDate date2 = LocalDate.of(2022,11,11);
        Card card2 = new VirtualOneTimeCard("8765-4321",1234,date2);

        Account[] accounts = new Account[2];
        Card[] cards = new Card[2];
        accounts[0] = from;
        cards[0] = card1;
        accounts[1] = to;
        cards[1] = card2;

        RevolutAPI revolutAPI = new Revolut(accounts,cards);

        System.out.println(revolutAPI.transferMoney(from,to,150));
        System.out.println(to.getAmount());
        System.out.println(revolutAPI.getTotalAmount());
        System.out.println(revolutAPI.payOnline(card1,1111,150,"BGN","www.olx.bg"));
        System.out.println(revolutAPI.payOnline(card1,1111,150,"BGN","www.olx.bg"));
        //System.out.println(revolutAPI.payOnline(card1,12314,150,"BGN","www.olx.bg"));
        System.out.println(revolutAPI.payOnline(card1,1234,150,"BGN","www.olx.bg"));
    }
}
