package bg.sofia.uni.fmi.mjt.revolut.account;

public class BGNAccount extends Account{
    public BGNAccount(String iban) {
        super(iban);
    }

    public BGNAccount(String iban, double amount) {
        super(iban, amount);
    }

    @Override
    public String getCurrency() {
        return BGN;
    }

}
