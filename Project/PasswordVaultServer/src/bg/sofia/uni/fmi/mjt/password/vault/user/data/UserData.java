package bg.sofia.uni.fmi.mjt.password.vault.user.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.password.vault.command.CommandExecutor.PROPERTY_KEY;

public class UserData {

    private static final String SERVER_PASSWORDS_DIRECTORY =  System.getProperty(PROPERTY_KEY) + "serverPasswords.txt";
    private static final String WEBSITE_PASSWORDS_DIRECTORY = System.getProperty(PROPERTY_KEY) + "websites" +  File.separator;

    private static final String FILE_OPENING_PROBLEM = "A problem occurred when try to open filename %s.txt";
    private static final String FILE_EXTENSION = ".txt";
    private static final int TIME_IN_MILLISECONDS = 60000;

    private final String username;

    public UserData(String username) {
        this.username = username;
    }

    private long lastEventInMillis;
    private boolean isLoggedIn;

    public void login() {
        isLoggedIn = true;
        lastEventInMillis = System.currentTimeMillis();
    }

    public void logout() {
        isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return System.currentTimeMillis() - lastEventInMillis < TIME_IN_MILLISECONDS && isLoggedIn;
    }

    public boolean verifyUserPassword(String password) {
        try (var reader = new BufferedReader(new FileReader(SERVER_PASSWORDS_DIRECTORY))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(" ");
                if (credentials[0].equals(username) && credentials[1].equals(password)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FILE_OPENING_PROBLEM, WEBSITE_PASSWORDS_DIRECTORY), e);
        }
    }

    public String getUserPassword() {
        try (var reader = new BufferedReader(new FileReader(SERVER_PASSWORDS_DIRECTORY))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(" ");
                if (credentials[0].equals(username)) {
                    return credentials[1];
                }
            }
            return null;
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FILE_OPENING_PROBLEM, WEBSITE_PASSWORDS_DIRECTORY), e);
        }
    }

    public void addCredentials(String password) {
        try (var writer = new BufferedWriter(new FileWriter(SERVER_PASSWORDS_DIRECTORY, true))) {
            writer.write(username + " " + password + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FILE_OPENING_PROBLEM, WEBSITE_PASSWORDS_DIRECTORY), e);
        }

        try {
            Files.createFile(Path.of(WEBSITE_PASSWORDS_DIRECTORY + username + FILE_EXTENSION));
            lastEventInMillis = System.currentTimeMillis();
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FILE_OPENING_PROBLEM, username), e);
        }
    }

    public void removeWebsitePassword(String website) {
        List<WebsiteCredentials> websiteCredentials = readWebsites();
        try (var writer = new BufferedWriter(new FileWriter(WEBSITE_PASSWORDS_DIRECTORY + username + FILE_EXTENSION))) {
            for (WebsiteCredentials singleWebsite : websiteCredentials) {
                if (!singleWebsite.website().equals(website)) {
                    writer.write(singleWebsite.website() + " " + singleWebsite.password() + "\n");
                    writer.flush();
                }
            }
            lastEventInMillis = System.currentTimeMillis();
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FILE_OPENING_PROBLEM, username), e);
        }
    }

    public String getWebsitePassword(String website) {
        try (var reader = new BufferedReader(new FileReader(WEBSITE_PASSWORDS_DIRECTORY + username + FILE_EXTENSION))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(" ");
                if (credentials[0].equals(website)) {
                    return credentials[1];
                }
            }
            lastEventInMillis = System.currentTimeMillis();
            return null;
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FILE_OPENING_PROBLEM, username), e);
        }
    }

    public void addWebsitePassword(String password, String website) {
        try (var writer = new BufferedWriter(new FileWriter(WEBSITE_PASSWORDS_DIRECTORY + username + FILE_EXTENSION, true))) {
            writer.write(website + " " + password + "\n");
            writer.flush();
            lastEventInMillis = System.currentTimeMillis();
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FILE_OPENING_PROBLEM, username), e);
        }
    }

    private List<WebsiteCredentials> readWebsites() {
        List<WebsiteCredentials> websiteCredentials = new ArrayList<>();
        try (var reader = new BufferedReader(new FileReader(WEBSITE_PASSWORDS_DIRECTORY + username + FILE_EXTENSION))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(" ");
                websiteCredentials.add(new WebsiteCredentials(credentials[0], credentials[1]));
            }
            return websiteCredentials;
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FILE_OPENING_PROBLEM, username), e);
        }
    }

}
