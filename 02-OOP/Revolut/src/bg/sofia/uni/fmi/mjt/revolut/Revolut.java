package bg.sofia.uni.fmi.mjt.revolut;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;
import bg.sofia.uni.fmi.mjt.revolut.card.CardType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Revolut implements RevolutAPI {

    private static final double EXCHANGE_RATE_EUR_TO_BGN = 1.95583;
    private static final String CURRENTLY_BANNED_DOMAIN = ".biz";
    private static final String BGN = "BGN";
    private static final String EUR = "EUR";
    
    private final Account[] accounts;
    private final Card[] cards;

    public Revolut(Account[] accounts, Card[] cards) {
        this.accounts = accounts;
        this.cards = cards;
    }

    @Override
    public boolean pay(Card card, int pin, double amount, String currency) {
        if (!checkIsNotNullOrEmpty(card) || !checkIsNotNullOrEmpty(currency) || !checkIsNotNullOrEmpty(card.getType())) {
            return false;
        } else if (!checkIsNotNegative(pin) || !checkIsNotNegative(amount)) {
            return false;
        } else if (!CardType.PHYSICAL.equals(card.getType())) {
            return false;
        } else if (pin < 1000 || pin > 9999) {
            return false;
        } else if (BGN.equals(currency) && !EUR.equals(currency)) {
            return false;
        }
        return conductPayment(card, pin, amount, currency);
    }

    @Override
    public boolean payOnline(Card card, int pin, double amount, String currency, String shopURL) {
        if (!checkIsNotNullOrEmpty(card) || !checkIsNotNullOrEmpty(currency) ||
                !checkIsNotNullOrEmpty(shopURL) || !checkIsNotNullOrEmpty(card.getType())) {
            return false;
        } else if (containsCurrentlyBannedDomain(shopURL)) {
            return false;
        } else if (!checkIsNotNegative(pin) || !checkIsNotNegative(amount)) {
            return false;
        } else if (BGN.equals(currency) && !EUR.equals(currency)) {
            return false;
        }
        return conductPayment(card, pin, amount, currency);
    }

    @Override
    public boolean addMoney(Account account, double amount) {
        if (!checkIsNotNullOrEmpty(account)) {
            return false;
        } else if (!checkIsNotNegative(amount)) {
            return false;
        }
        Account userAccount = getAccount(account.getIban());
        if (!checkIsNotNullOrEmpty(userAccount)) {
            return false;
        }

        userAccount.depositMoney(amount, account.getCurrency());
        return true;
    }

    @Override
    public boolean transferMoney(Account from, Account to, double amount) {
        if (!checkIsNotNullOrEmpty(from) || !checkIsNotNullOrEmpty(to)) {
            return false;
        } else if (!checkIsNotNegative(amount)) {
            return false;
        }
        String fromAccountIban = from.getIban();
        String ToAccountIban = to.getIban();

        Account userAccountFrom = getAccount(fromAccountIban);
        if (!checkIsNotNullOrEmpty(userAccountFrom)) {
            return false;
        }
        Account userAccountTo = getAccount(ToAccountIban);
        if (!checkIsNotNullOrEmpty(userAccountTo)) {
            return false;
        }

        if (fromAccountIban.equals(ToAccountIban)) {
            return false;
        }

        return executeTransaction(from, to, amount);
    }

    @Override
    public double getTotalAmount() {
        double amount = 0;
        for (Account account : accounts) {
            if (account.getCurrency().equals("EUR")) {
                double convertMoneyToCurrencyToBGN = EXCHANGE_RATE_EUR_TO_BGN * account.getAmount();
                amount += convertMoneyToCurrencyToBGN;
            } else {
                amount += account.getAmount();
            }
        }
        return amount;
    }

    private boolean conductPayment(Card card, int pin, double amount, String currency) {
        Card revolutCard = getCard(card);
        if (!checkIsNotNullOrEmpty(revolutCard)) {
            return false;
        }
        boolean expiredCard = ChronoUnit.DAYS.between(LocalDate.now(), revolutCard.getExpirationDate()) < 0;
        if (expiredCard || revolutCard.isBlocked() || !revolutCard.checkPin(pin)) {
            return false;
        }

        Account account = getAccount(card.getNumber());
        if (!checkIsNotNullOrEmpty(account)) {
            return false;
        }

        return account.withdrawMoney(amount, currency);
    }

    private Card getCard(Card card) {
        for (Card revolutCard : cards) {
            if (revolutCard.getNumber().equals(card.getNumber()) && revolutCard.getType().equals(card.getType())) {
                return revolutCard;
            }
        }
        return null;
    }

    private Account getAccount(String iban) {
        for (Account account : accounts) {
            if (account.getIban().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    private boolean executeTransaction(Account from, Account to, double amount) {
        if (!from.withdrawMoney(amount, from.getCurrency())) {
            return false;
        }
        to.depositMoney(amount, from.getCurrency());
        return true;
    }

    private boolean containsCurrentlyBannedDomain(String shopURL) {
        int urlCountParts = 3;
        String[] urlParts = shopURL.split(".", urlCountParts);
        if (urlParts.length != urlCountParts) {
            return false;
        }
        String[] thirdPart = urlParts[3].split("/");
        String topLevelDomain = thirdPart[0];
        return CURRENTLY_BANNED_DOMAIN.equals(topLevelDomain);
    }

    private boolean checkIsNotNullOrEmpty(Object key) {
        if (key == null || key instanceof String && key.toString().isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean checkIsNotNegative(Number key) {
        if (key instanceof Integer && key.intValue() <= 0) {
            return false;
        } else if (key instanceof Double && key.doubleValue() <= 0) {
            return false;
        } else {
            return true;
        }
    }

}

