package bg.sofia.uni.fmi.mjt.password.vault.command;

import bg.sofia.uni.fmi.mjt.password.vault.algorithm.Sha256;
import bg.sofia.uni.fmi.mjt.password.vault.exception.InvalidDecryptionException;
import bg.sofia.uni.fmi.mjt.password.vault.exception.InvalidEncryptionException;
import bg.sofia.uni.fmi.mjt.password.vault.exception.PasswordApiClientException;
import bg.sofia.uni.fmi.mjt.password.vault.rest.SecurePasswordChecker;
import bg.sofia.uni.fmi.mjt.password.vault.user.data.UserData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CommandExecutorTest {

    private static final String INVALID_ARGS_COUNT_MESSAGE = "Invalid arguments!";

    private CommandExecutor commandExecutor;

    @Mock
    private SecurePasswordChecker securePasswordChecker;

    @Mock
    private Map<String, UserData> users;

    @Mock
    private UserData userDataMock;

    @Before
    public void setUp() {
        commandExecutor = new CommandExecutor(securePasswordChecker, users);
    }

    @Test
    public void testExecuteRegisterInvalidArguments() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.REGISTER, "Mario", ""));
        Response expectedResponse = new Response(CommandType.REGISTER, INVALID_ARGS_COUNT_MESSAGE);

        executeRegisterNeverCallPut(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteRegisterPasswordsAreNotEquals() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.REGISTER, "Mario 123456 654321", "Mario"));
        Response expectedResponse = new Response(CommandType.REGISTER, "Both passwords are not equals!");

        executeRegisterNeverCallPut(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteRegisterUserAlreadyExist() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.containsKey(anyString())).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.REGISTER, "Mario 123456 123456", "Mario"));
        Response expectedResponse = new Response(CommandType.REGISTER, "Username Mario is already taken, select another one!");

        assertEquals(expectedResponse, actualResponse);
        verify(users, never()).put(any(), any());
    }

    private void executeRegisterNeverCallPut(Response expectedResponse, Response actualResponse) {
        assertEquals(expectedResponse, actualResponse);
        verify(users, never()).put(any(), any());
    }

    @Test
    public void testExecuteRegisterSuccess() throws PasswordApiClientException, IOException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.REGISTER, "oxoboxo 123456 123456", "oxoboxo"));
        Response expectedResponse = new Response(CommandType.REGISTER, "Username oxoboxo successfully registered");

        assertEquals(expectedResponse, actualResponse);
        verify(users, atLeastOnce()).put(any(), any());
        Files.deleteIfExists(Path.of("resource" + File.separator + "test" + File.separator + "websites" + File.separator + "oxoboxo.txt"));
    }

    @Test
    public void testExecuteLoginInvalidArguments() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGIN, INVALID_ARGS_COUNT_MESSAGE);

        loginUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteLoginUserIsNotRegistered() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario 123456", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGIN, "User with nickname Mario is not registered!");

        loginUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteLoginUserIsAlreadyLogin() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario 123456", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGIN, "Username Mario has already logged in!");

        loginUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteLoginInvalidPassword() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario 654321", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGIN, "Invalid password!");

        loginUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    private void loginUserDataMockNeverCallResult(Response expectedResponse, Response actualResponse) {
        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, never()).login();
    }

    @Test
    public void testExecuteLoginUserSuccess() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.verifyUserPassword(anyString())).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario 123456", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGIN, "User Mario successfully logged in!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).login();
    }

    @Test
    public void testExecuteLogoutInvalidArguments() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGOUT, "Mario", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGOUT, INVALID_ARGS_COUNT_MESSAGE);

        logoutUserDataMockNeverCallsResult(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteLogoutWhenUserNotRegister() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGOUT, "", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGOUT, "You are not register!");

        logoutUserDataMockNeverCallsResult(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteLogoutWhenUserNotLogin() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.containsKey(anyString())).thenReturn(true);
        when(users.get(anyString())).thenReturn(userDataMock);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGOUT, "", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGOUT, "You are not logged in or the session has already expired!");

        logoutUserDataMockNeverCallsResult(expectedResponse, actualResponse);
    }

    private void logoutUserDataMockNeverCallsResult(Response expectedResponse, Response actualResponse) {
        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, never()).logout();
    }

    @Test
    public void testExecuteLogoutSuccess() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.containsKey(anyString())).thenReturn(true);
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGOUT, "", "Mario"));
        Response expectedResponse = new Response(CommandType.LOGOUT, "Successfully logged out!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).logout();
    }

    @Test
    public void testExecuteDisconnectInvalidArguments() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.DISCONNECT, "Mario", "Mario"));
        Response expectedResponse = new Response(CommandType.DISCONNECT, INVALID_ARGS_COUNT_MESSAGE);

        logoutUserDataMockNeverCallsResult(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteDisconnectSuccess() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.DISCONNECT, "", "Mario"));
        Response expectedResponse = new Response(CommandType.DISCONNECT, "Disconnected from server!");

        logoutUserDataMockNeverCallsResult(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteDisconnectWhenUserisLoggedInSuccess() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.containsKey(anyString())).thenReturn(true);
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.DISCONNECT, "", "Mario"));
        Response expectedResponse = new Response(CommandType.DISCONNECT, "Disconnected from server!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).logout();
    }

    @Test
    public void testGeneratePasswordInvalidArguments() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.GENERATE_PASSWORD, "facebook.com", "Mario"));
        Response expectedResponse = new Response(CommandType.GENERATE_PASSWORD, INVALID_ARGS_COUNT_MESSAGE);

        generateMocksNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testGeneratePasswordUserNotRegistered() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.GENERATE_PASSWORD, "facebook.com Mario", ""));
        Response expectedResponse = new Response(CommandType.GENERATE_PASSWORD, "User with nickname Mario is not registered!");

        generateMocksNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testGeneratePasswordUserNotLogin() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.GENERATE_PASSWORD, "facebook.com Mario", ""));
        Response expectedResponse = new Response(CommandType.GENERATE_PASSWORD, "You are not logged in!");

        generateMocksNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testGeneratePasswordForAnotherUser() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.GENERATE_PASSWORD, "facebook.com Nikolay", "Mario"));
        Response expectedResponse = new Response(CommandType.GENERATE_PASSWORD, "This is not your account!");

        generateMocksNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testGeneratePasswordWebsiteHasAlreadyExist() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn(anyString());

        Response actualResponse = commandExecutor.execute(new Command(CommandType.GENERATE_PASSWORD, "facebook.com Mario", "Mario"));
        Response expectedResponse = new Response(CommandType.GENERATE_PASSWORD, "The provided website is existing and has a password!");

        generateMocksNeverCallResult(expectedResponse, actualResponse);
    }

    private void generateMocksNeverCallResult(Response expectedResponse, Response actualResponse) {
        assertEquals(expectedResponse, actualResponse);
        //verify(passwordGenerator, never()).generate();
        verify(userDataMock, never()).addWebsitePassword(any(), any());
    }

    @Test
    public void testGeneratePasswordSuccess() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn(null);
        when(userDataMock.getUserPassword()).thenReturn("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");

        commandExecutor.execute(new Command(CommandType.GENERATE_PASSWORD, "facebook.com Mario", "Mario"));

        verify(userDataMock, atLeastOnce()).addWebsitePassword(any(), any());
    }


    @Test
    public void testRetrieveCredentialsInvalidArguments() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.RETRIEVE_CREDENTIALS, "facebook.com", "Mario"));
        Response expectedResponse = new Response(CommandType.RETRIEVE_CREDENTIALS, INVALID_ARGS_COUNT_MESSAGE);

        retrieveCredentialsUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testRetrieveCredentialsUserNotRegistered() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.RETRIEVE_CREDENTIALS, "facebook.com Mario", ""));
        Response expectedResponse = new Response(CommandType.RETRIEVE_CREDENTIALS, "User with nickname Mario is not registered!");

        retrieveCredentialsUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testRetrieveCredentialsUserNotLogin() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.RETRIEVE_CREDENTIALS, "facebook.com Mario", ""));
        Response expectedResponse = new Response(CommandType.RETRIEVE_CREDENTIALS, "You are not logged in!");

        retrieveCredentialsUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testRetrieveCredentialsForAnotherUser() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.RETRIEVE_CREDENTIALS, "facebook.com Nikolay", "Mario"));
        Response expectedResponse = new Response(CommandType.RETRIEVE_CREDENTIALS, "This is not your account!");

        retrieveCredentialsUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    private void retrieveCredentialsUserDataMockNeverCallResult(Response expectedResponse, Response actualResponse) {
        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, never()).getWebsitePassword(any());
    }

    @Test
    public void testRetrieveCredentialsWebsiteDoesNotExist() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn(null);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.RETRIEVE_CREDENTIALS, "facebook.com Mario", "Mario"));
        Response expectedResponse = new Response(CommandType.RETRIEVE_CREDENTIALS, "The provided website does not exist!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).getWebsitePassword(any());
    }

    @Test
    public void testRetrieveCredentialsSuccess() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn("KhsvCPvFOt1fR6kaE0WmJAP7y8KHm5eaiuxPWvUEm2k=");
        when(userDataMock.getUserPassword()).thenReturn("6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b");

        Response actualResponse = commandExecutor.execute(new Command(CommandType.RETRIEVE_CREDENTIALS, "facebook.com Mario", "Mario"));
        Response expectedResponse = new Response(CommandType.RETRIEVE_CREDENTIALS, "7t*679^[PfMPi8P99N5-9bbuPz");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).getWebsitePassword(any());
    }

    @Test
    public void testRemovePasswordInvalidArguments() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.REMOVE_PASSWORD, "facebook.com", "Mario"));
        Response expectedResponse = new Response(CommandType.REMOVE_PASSWORD, INVALID_ARGS_COUNT_MESSAGE);

        removePasswordUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testRemovePasswordUserNotRegistered() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.REMOVE_PASSWORD, "facebook.com Mario", ""));
        Response expectedResponse = new Response(CommandType.REMOVE_PASSWORD, "User with nickname Mario is not registered!");

        removePasswordUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testRemovePasswordUserNotLogin() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.REMOVE_PASSWORD, "facebook.com Mario", ""));
        Response expectedResponse = new Response(CommandType.REMOVE_PASSWORD, "You are not logged in!");

        removePasswordUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testRemovePasswordForAnotherUser() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.REMOVE_PASSWORD, "facebook.com Nikolay", "Mario"));
        Response expectedResponse = new Response(CommandType.REMOVE_PASSWORD, "This is not your account!");

        removePasswordUserDataMockNeverCallResult(expectedResponse, actualResponse);
    }

    private void removePasswordUserDataMockNeverCallResult(Response expectedResponse, Response actualResponse) {
        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, never()).getWebsitePassword(any());
        verify(userDataMock, never()).removeWebsitePassword(any());
    }

    @Test
    public void testRemovePasswordWebsiteDoesNotExist() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn(null);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.REMOVE_PASSWORD, "facebook.com Mario", "Mario"));
        Response expectedResponse = new Response(CommandType.REMOVE_PASSWORD, "The provided website does not exist!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).getWebsitePassword(any());
        verify(userDataMock, never()).removeWebsitePassword(any());
    }

    @Test
    public void testRemovePasswordSuccess() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn(anyString());

        Response actualResponse = commandExecutor.execute(new Command(CommandType.REMOVE_PASSWORD, "facebook.com Mario", "Mario"));
        Response expectedResponse = new Response(CommandType.REMOVE_PASSWORD, "Password was successfully removed!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).getWebsitePassword(any());
        verify(userDataMock, atLeastOnce()).removeWebsitePassword(any());
    }

    @Test
    public void testAddPasswordInvalidArguments() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.ADD_PASSWORD, "facebook.com", "Mario"));
        Response expectedResponse = new Response(CommandType.ADD_PASSWORD, INVALID_ARGS_COUNT_MESSAGE);

        addPasswordMocksNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testAddPasswordUserNotRegistered() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.ADD_PASSWORD, "facebook.com Mario P@ssword!", ""));
        Response expectedResponse = new Response(CommandType.ADD_PASSWORD, "User with nickname Mario is not registered!");

        addPasswordMocksNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testAddPasswordUserNotLogin() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.ADD_PASSWORD, "facebook.com Mario P@ssword!", ""));
        Response expectedResponse = new Response(CommandType.ADD_PASSWORD, "You are not logged in!");

        addPasswordMocksNeverCallResult(expectedResponse, actualResponse);
    }

    @Test
    public void testAddPasswordForAnotherUser() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.ADD_PASSWORD, "facebook.com Nikolay P@ssword!", "Mario"));
        Response expectedResponse = new Response(CommandType.ADD_PASSWORD, "This is not your account!");

        addPasswordMocksNeverCallResult(expectedResponse, actualResponse);
    }

    private void addPasswordMocksNeverCallResult(Response expectedResponse, Response actualResponse) throws PasswordApiClientException {
        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, never()).getWebsitePassword(any());
        verify(securePasswordChecker, never()).isSecure(anyString());
        verify(userDataMock, never()).addWebsitePassword(any(), any());
    }

    @Test
    public void testAddPasswordTheProvidedWebsiteHasAlreadyExist() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn(anyString());

        Response actualResponse = commandExecutor.execute(new Command(CommandType.ADD_PASSWORD, "facebook.com Mario password", "Mario"));
        Response expectedResponse = new Response(CommandType.ADD_PASSWORD, "The provided website is existing and has a password!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).getWebsitePassword(any());
        verify(securePasswordChecker, never()).isSecure(anyString());
        verify(userDataMock, never()).addWebsitePassword(any(), any());
    }

    @Test
    public void testAddPasswordTheProvidedPasswordNotCompromised() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn(null);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.ADD_PASSWORD, "facebook.com Mario password", "Mario"));
        Response expectedResponse = new Response(CommandType.ADD_PASSWORD, "The provided password is compromised!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).getWebsitePassword(any());
        verify(securePasswordChecker, atLeastOnce()).isSecure(anyString());
        verify(userDataMock, never()).addWebsitePassword(any(), any());
    }

    @Test
    public void testAddPasswordSuccess() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        when(users.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getWebsitePassword(any())).thenReturn(null);
        when(securePasswordChecker.isSecure("7t*679^[PfMPi8P99N5-9bbuPz")).thenReturn(true);
        when(userDataMock.getUserPassword()).thenReturn("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");

        Response actualResponse = commandExecutor.execute(new Command(CommandType.ADD_PASSWORD, "facebook.com Mario 7t*679^[PfMPi8P99N5-9bbuPz", "Mario"));
        Response expectedResponse = new Response(CommandType.ADD_PASSWORD, "The provided password is being added for the given website!");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).getWebsitePassword(any());
        verify(securePasswordChecker, atLeastOnce()).isSecure(anyString());
        verify(userDataMock, atLeastOnce()).addWebsitePassword(any(), any());
    }

    @Test
    public void testExecuteUnknownCommand() throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.UNKNOWN, "sdsd sdsddsdsd", "Mario"));
        Response expectedResponse = new Response(CommandType.UNKNOWN, "Unknown command!");
        assertEquals(expectedResponse, actualResponse);
    }

}
