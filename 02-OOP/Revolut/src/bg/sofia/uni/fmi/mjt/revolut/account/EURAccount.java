package bg.sofia.uni.fmi.mjt.revolut.account;

public class EURAccount extends Account {

    public EURAccount(String iban) {
        super(iban);
    }

    public EURAccount(String iban, double amount) {
        super(iban, amount);
    }

    @Override
    public String getCurrency() {
        return EUR;
    }

}
