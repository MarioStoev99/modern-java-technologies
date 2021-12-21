package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.util.HashSet;
import java.util.Set;

public class UserData {

    private String password;
    private boolean isLoggedIn;
    private Set<String> presents;

    public UserData(String password) {
        this.password = password;
        this.presents = new HashSet<>();
    }

    public boolean verifyUserPassword(String password) {
        return this.password.equals(password);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void login() {
        setLoggedIn(true);
    }

    public void logout() {
        setLoggedIn(false);
    }

    private void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public Set<String> getPresents() {
        return presents;
    }

}
