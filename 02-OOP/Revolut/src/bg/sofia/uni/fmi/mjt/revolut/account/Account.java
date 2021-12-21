package bg.sofia.uni.fmi.mjt.revolut.account;

public abstract class Account {

    private static final double EXCHANGE_RATE_EUR_TO_BGN = 1.95583;
    protected static final String BGN = "BGN";
    protected static final String EUR = "EUR";

    private final String iban;
    private double amount;

    protected Account(String IBAN) {
        this(IBAN, 0);
    }

    protected Account(String iban, double amount) {
        this.iban = iban;
        this.amount = amount;
    }

    public abstract String getCurrency();

    public double getAmount() {
        return amount;
    }

    public String getIban() {
        return iban;
    }

    public boolean withdrawMoney(double amount,String currency) {
        double convertedMoney = 0;
        if (this.getCurrency().equals(currency)) {
            convertedMoney = amount;
        } else if (this.getCurrency().equals(EUR)) {
            convertedMoney = amount / EXCHANGE_RATE_EUR_TO_BGN;
        } else {
            convertedMoney += amount * EXCHANGE_RATE_EUR_TO_BGN;
        }

        if(this.amount < convertedMoney) {
            return false;
        }

        this.amount -= convertedMoney;
        return true;
    }

    public void depositMoney(double amount,String currency) {
        if (this.getCurrency().equals(currency)) {
            this.amount += amount;
        } else if (this.getCurrency().equals(EUR)) {
           this.amount += amount / EXCHANGE_RATE_EUR_TO_BGN;
        } else {
            this.amount += amount * EXCHANGE_RATE_EUR_TO_BGN;
        }
    }

}