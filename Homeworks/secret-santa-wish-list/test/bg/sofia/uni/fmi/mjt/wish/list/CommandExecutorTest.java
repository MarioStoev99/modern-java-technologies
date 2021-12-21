package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.command.Command;
import bg.sofia.uni.fmi.mjt.wish.list.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.wish.list.command.CommandType;
import bg.sofia.uni.fmi.mjt.wish.list.command.Response;
import bg.sofia.uni.fmi.mjt.wish.list.storage.UserData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandExecutorTest {

    private static final String INVALID_ARGUMENTS_COUNT = "[ Invalid arguments count. Check the template and try again! ]";

    private CommandExecutor commandExecutor;

    @Mock
    private Map<String, UserData> userData;

    @Before
    public void setUp() {
        commandExecutor = new CommandExecutor(userData);
    }

    @Test
    public void testExecuteRegisterInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.REGISTER, "Mario", "Mario", 1));
        Response expectedResponse = new Response(CommandType.REGISTER, INVALID_ARGUMENTS_COUNT);

        assertEquals(expectedResponse, actualResponse);
        verify(userData, never()).put("Mario", new UserData(""));
    }

    @Test
    public void testExecuteRegisterInvalidUsername() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.REGISTER, "M@rio 123456", "Mario", 2));
        Response expectedResponse = new Response(CommandType.REGISTER, "[ Username M@rio is invalid, select a valid one ]");

        assertEquals(expectedResponse, actualResponse);
        verify(userData, never()).put("Mario", new UserData(""));
    }

    @Test
    public void testExecuteRegisterUserAlreadyExist() {
        when(userData.containsKey("Mario")).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.REGISTER, "Mario 123456", "Mario", 2));
        Response expectedResponse = new Response(CommandType.REGISTER, "[ Username Mario is already taken, select another one ]");
        assertEquals(expectedResponse, actualResponse);

        verify(userData, never()).put("Mario", new UserData("123456"));
    }

    @Test
    public void testExecuteRegisterSuccess() {
        when(userData.containsKey("Mario")).thenReturn(false);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.REGISTER, "Mario 123456", "Mario", 2));
        Response expectedResponse = new Response(CommandType.REGISTER, "[ Username Mario successfully registered ]");

        assertEquals(expectedResponse, actualResponse);
        verify(userData, atLeastOnce()).put("Mario", new UserData("123456"));
    }

    @Test
    public void testExecuteLoginInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario", "Mario", 1));
        Response expectedResponse = new Response(CommandType.LOGIN, INVALID_ARGUMENTS_COUNT);

        assertEquals(expectedResponse, actualResponse);
        verify(userData, never()).get("Mario");
    }

    @Test
    public void testExecuteLoginUserIsNotRegistered() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario 123456", "Mario", 2));
        Response expectedResponse = new Response(CommandType.LOGIN, "[ Student with nickname Mario is not registered ]");

        assertEquals(expectedResponse, actualResponse);
        verify(userData, never()).get("Mario");
    }

    @Test
    public void testExecuteLoginUserIsAlreadyJoined() {
        UserData userDataMock = mock(UserData.class);
        when(userData.containsKey("Mario")).thenReturn(true);
        when(userData.get("Mario")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario 123456", "Mario", 2));
        Response expectedResponse = new Response(CommandType.LOGIN, "[ User Mario has already logged in ]");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, never()).login();
    }

    @Test
    public void testExecuteLoginInvalidPassword() {
        UserData userDataMock = mock(UserData.class);
        when(userData.containsKey("Mario")).thenReturn(true);
        when(userData.get("Mario")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(false);
        when(userDataMock.verifyUserPassword(anyString())).thenReturn(false);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario 654321", "Mario", 2));
        Response expectedResponse = new Response(CommandType.LOGIN, "[ Invalid password ]");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, never()).login();
    }

    @Test
    public void testExecuteLoginUserSuccess() {
        UserData userDataMock = mock(UserData.class);
        when(userData.containsKey("Mario")).thenReturn(true);
        when(userData.get("Mario")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(false);
        when(userDataMock.verifyUserPassword(anyString())).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGIN, "Mario 123456", "Mario", 2));
        Response expectedResponse = new Response(CommandType.LOGIN, "[ User Mario successfully logged in ]");

        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock, atLeastOnce()).login();
    }

    @Test
    public void testExecutePostWishInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.POST_WISH, "", "Mario", 0));
        Response expectedResponse = new Response(CommandType.POST_WISH, INVALID_ARGUMENTS_COUNT);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testExecutePostWishUserIsNotRegistered() {
        Set<String> presentsMock = mock(Set.class);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.POST_WISH, "Mario kolelo", "Mario", 2));
        Response expectedResponse = new Response(CommandType.POST_WISH, "[ Student with nickname Mario is not registered ]");

        assertEquals(expectedResponse, actualResponse);
        verify(presentsMock, never()).add(anyString());
    }

    @Test
    public void testExecutePostWishUserIsNotLogin() {
        UserData userDataMock = mock(UserData.class);
        Set<String> presentsMock = mock(Set.class);
        when(userData.containsKey("Mario")).thenReturn(true);
        when(userData.get("Mario")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(false);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.POST_WISH, "Mario kolelo", "Mario", 2));
        Response expectedResponse = new Response(CommandType.POST_WISH, "[ You are not logged in ]");

        assertEquals(expectedResponse, actualResponse);
        verify(presentsMock, never()).add(anyString());
    }

    @Test
    public void testExecutePostWishGiftHasAlreadyBeenSubmitted() {
        UserData userDataMock = mock(UserData.class);
        Set<String> presentsMock = mock(Set.class);
        when(userData.containsKey("Mario")).thenReturn(true);
        when(userData.get("Mario")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getPresents()).thenReturn(presentsMock);
        when(presentsMock.contains(anyString())).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.POST_WISH, "Mario kolelo", "Mario", 2));
        Response expectedResponse = new Response(CommandType.POST_WISH, "[ The same gift for student Mario was already submitted ]");

        assertEquals(expectedResponse, actualResponse);
        verify(presentsMock, never()).add(anyString());
    }

    @Test
    public void testExecutePostWishForTheFirstTimeSuccess() {
        UserData userDataMock = mock(UserData.class);
        Set<String> presentsMock = mock(Set.class);
        when(userData.containsKey("Mario")).thenReturn(true);
        when(userData.get("Mario")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getPresents()).thenReturn(presentsMock);
        when(presentsMock.contains(anyString())).thenReturn(false);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.POST_WISH, "Mario kolelo", "Mario", 2));
        Response expectedResponse = new Response(CommandType.POST_WISH, "[ Gift kolelo for student Mario submitted successfully ]");

        assertEquals(expectedResponse, actualResponse);
        verify(presentsMock, atLeastOnce()).add(anyString());
    }

    @Test
    public void testExecutePostWishForTheSecondTimeForTheSameUserSuccess() {
        UserData userDataMock = mock(UserData.class);
        Set<String> presentsMock = mock(Set.class);
        when(userData.containsKey("Mario")).thenReturn(true);
        when(userData.get("Mario")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getPresents()).thenReturn(presentsMock);
        when(presentsMock.contains(anyString())).thenReturn(false);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.POST_WISH, "Mario Smartphone", "Mario", 2));
        Response expectedResponse = new Response(CommandType.POST_WISH, "[ Gift Smartphone for student Mario submitted successfully ]");

        assertEquals(expectedResponse, actualResponse);
        verify(presentsMock, atLeastOnce()).add(anyString());
    }

    @Test
    public void testExecuteGetWishInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.GET_WISH, "Mario kolelo", "Nikolay", 2));
        Response expectedResponse = new Response(CommandType.GET_WISH, INVALID_ARGUMENTS_COUNT);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteGetWishUserIsNotLogin() {
        UserData userDataMock = mock(UserData.class);
        Set<String> presentsMock = mock(Set.class);
        when(userData.containsKey("Nikolay")).thenReturn(true);
        when(userData.get("Nikolay")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(false);
        when(userDataMock.getPresents()).thenReturn(presentsMock);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.GET_WISH, "", "Nikolay", 0));
        Response expectedResponse = new Response(CommandType.GET_WISH, "[ You are not logged in ]");

        assertEquals(expectedResponse, actualResponse);
        verify(presentsMock, never()).clear();
    }

    @Test
    public void testExecuteGetWishNoPresentsInTheWishList() {
        UserData userDataMock = mock(UserData.class);
        Set<String> presentsMock = mock(Set.class);
        when(userData.containsKey("Nikolay")).thenReturn(true);
        when(userData.get("Nikolay")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getPresents()).thenReturn(presentsMock);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.GET_WISH, "", "Nikolay", 0));
        Response expectedResponse = new Response(CommandType.GET_WISH, "[ There are no presents in the wish list! ]");

        assertEquals(expectedResponse, actualResponse);
        verify(presentsMock, never()).clear();
    }

    @Test
    public void testExecuteGetWishSuccess() {
        UserData userDataMock = mock(UserData.class);
        Set<String> presentsMock = mock(Set.class);
        Map.Entry<String, UserData> entryMock = mock(Map.Entry.class);
        when(userData.containsKey("Nikolay")).thenReturn(true);
        when(userData.get("Nikolay")).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);
        when(userDataMock.getPresents()).thenReturn(presentsMock);
        when(userData.entrySet()).thenReturn((Set<Map.Entry<String, UserData>>) entryMock);
        when(entryMock.getValue()).thenReturn(userDataMock);
        when(userDataMock.getPresents()).thenReturn(presentsMock);
        when(presentsMock.isEmpty()).thenReturn(false);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.GET_WISH, "", "Nikolay", 0));
        Response expectedResponse = new Response(CommandType.GET_WISH, "[ Mario: [kolelo] ]");

        assertEquals(expectedResponse, actualResponse);
        verify(presentsMock, atLeastOnce()).clear();
    }

    @Test
    public void testExecuteDisconnectInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.DISCONNECT, "Mario", "Mario", 1));
        Response expectedResponse = new Response(CommandType.DISCONNECT, INVALID_ARGUMENTS_COUNT);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteDisconnectSuccess() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.DISCONNECT, "", "Mario", 0));
        Response expectedResponse = new Response(CommandType.DISCONNECT, "[ Disconnected from server ]");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteLogoutInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGOUT, "Mario", "Mario", 1));
        Response expectedResponse = new Response(CommandType.LOGOUT, INVALID_ARGUMENTS_COUNT);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteLogoutWhenUserNotLogin() {
        UserData userDataMock = mock(UserData.class);
        when(userData.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(false);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGOUT, "", "Mario", 0));
        Response expectedResponse = new Response(CommandType.LOGOUT, "[ You are not logged in ]");
        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock,never()).logout();
    }

    @Test
    public void testExecuteLogoutSuccess() {
        UserData userDataMock = mock(UserData.class);
        when(userData.get(anyString())).thenReturn(userDataMock);
        when(userDataMock.isLoggedIn()).thenReturn(true);

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LOGOUT, "", "Mario", 0));
        Response expectedResponse = new Response(CommandType.LOGOUT, "[ Successfully logged out ]");
        assertEquals(expectedResponse, actualResponse);
        verify(userDataMock,atLeastOnce()).logout();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNull() {
        commandExecutor.execute(null);
    }

    @Test
    public void testExecuteUnknownCommand() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.UNKNOWN, "sdsd sdsddsdsd", "Mario", 2));
        Response expectedResponse = new Response(CommandType.UNKNOWN, "Unknown command!");
        assertEquals(expectedResponse, actualResponse);
    }
}
