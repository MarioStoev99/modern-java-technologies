package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

abstract class AbstractCard implements Card {

    private static final int MAX_ATTEMPTS_TO_ENTER_THE_PIN = 3;

    private final String number;
    private final int pin;
    private final LocalDate expirationDate;

    private boolean isBlocked;
    private int invalidAttempts;

    protected AbstractCard(String number, int pin, LocalDate expirationDate) {
        this.number = number;
        this.pin = pin;
        this.expirationDate = expirationDate;
    }

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean checkPin(int pin) {
        if (this.pin == pin) {
            invalidAttempts = 0;
            return true;
        }

        if (++invalidAttempts == MAX_ATTEMPTS_TO_ENTER_THE_PIN) {
            block();
        }
        return false;
    }

    @Override
    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public void block() {
        isBlocked = true;
    }

    @Override
    public String getNumber() {
        return number;
    }
}
