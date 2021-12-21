package bg.sofia.uni.fmi.mjt.password.vault.rest;

import bg.sofia.uni.fmi.mjt.password.vault.exception.PasswordApiClientException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurePasswordCheckerTest {

    private SecurePasswordChecker securePasswordChecker;

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse httpResponseMock;

    @Before
    public void setUp() {
        securePasswordChecker = new SecurePasswordChecker(httpClientMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSecureNull() throws IOException, InterruptedException, PasswordApiClientException {
        securePasswordChecker.isSecure(null);
        verify(httpClientMock, never()).send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSecureEmpty() throws IOException, InterruptedException, PasswordApiClientException {
        securePasswordChecker.isSecure("");
        verify(httpClientMock, never()).send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
    }

    @Test
    public void testIsSecureHttpStatusOk() throws IOException, InterruptedException, PasswordApiClientException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        boolean actualPasswordCheckerResponse = securePasswordChecker.isSecure("password");
        assertEquals(false, actualPasswordCheckerResponse);
        verify(httpClientMock, atLeastOnce()).send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
    }

    @Test
    public void testIsSecureHttpStatusNotFound() throws IOException, InterruptedException, PasswordApiClientException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        boolean actualPasswordCheckerResponse = securePasswordChecker.isSecure("1221@!@!@!@!");
        assertEquals(true, actualPasswordCheckerResponse);
        verify(httpClientMock, atLeastOnce()).send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
    }

    @Test(expected = PasswordApiClientException.class)
    public void testIsSecureFailedWithHttpInternalServerError() throws IOException, InterruptedException, PasswordApiClientException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        securePasswordChecker.isSecure("password");
        verify(httpClientMock, atLeastOnce()).send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
    }
}
