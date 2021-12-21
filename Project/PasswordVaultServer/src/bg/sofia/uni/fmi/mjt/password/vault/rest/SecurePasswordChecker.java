package bg.sofia.uni.fmi.mjt.password.vault.rest;

import bg.sofia.uni.fmi.mjt.password.vault.algorithm.MD5;
import bg.sofia.uni.fmi.mjt.password.vault.algorithm.Sha1;
import bg.sofia.uni.fmi.mjt.password.vault.algorithm.Sha256;
import bg.sofia.uni.fmi.mjt.password.vault.exception.PasswordApiClientException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SecurePasswordChecker {

    private static final String AUTHORIZATION_API_HEADER = "authorization";
    private static final String AUTHORIZATION_API_TYPE_PLUS_VALUE = "basic Nzk5MWQ2MTZiZjZjNGY4Y2IwOWJjZGZhZWNiYmIzYTM6LWY1RkdIX0tAejlOMj9AeEthaDlVZy1qPWIrTVJ5aDU=";
    private static final String PASSWORD_API_URL = "https://api.enzoic.com/passwords?partial_sha1=%s5baa61e4c9&partial_md5=%s5f4dcc3b5a&partial_sha256=%s";
    private static final int FROM = 0;
    private static final int TO = 10;

    private final HttpClient httpClient;

    public SecurePasswordChecker(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public boolean isSecure(String password) throws PasswordApiClientException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty!");
        }

        HttpResponse<String> httpResponse = sendHttpRequest(password);
        int statusCode = httpResponse.statusCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            return false;
        } else if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            return true;
        } else {
            throw new PasswordApiClientException("Information about provided password cannot be given! Status code: " + statusCode);
        }
    }

    private HttpResponse<String> sendHttpRequest(String password) throws PasswordApiClientException {
        String sha1 = Sha1.getSha1(password).substring(FROM, TO);
        String md5 = MD5.getMd5(password).substring(FROM, TO);
        String sha256 = Sha256.getSha256(password).substring(FROM, TO);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format(PASSWORD_API_URL, sha1, md5, sha256)))
                .header(AUTHORIZATION_API_HEADER, AUTHORIZATION_API_TYPE_PLUS_VALUE)
                .build();
        try {
            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new PasswordApiClientException("An error occurred when try to send a request via client!", e);
        }
    }

}
