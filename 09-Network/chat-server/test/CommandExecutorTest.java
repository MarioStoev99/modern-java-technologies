import bg.sofia.uni.fmi.mjt.chat.command.Command;
import bg.sofia.uni.fmi.mjt.chat.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.chat.command.CommandType;
import bg.sofia.uni.fmi.mjt.chat.command.Response;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandExecutorTest {

    private static final String INVALID_ARGS_COUNT_MESSAGE = "Invalid arguments count!";
    private static final String RECIPIENT_NOT_REGISTERED = "The recipient is not registered with nickname!";
    private static final String ORIGIN_NOT_REGISTERED = "The author of the command is not registered with nickname!";

    private static final String MARIO = "Mario";
    private static final String PESHO = "Pesho";


    private CommandExecutor commandExecutor;

    @Before
    public void setUp() {
        commandExecutor = new CommandExecutor();
    }

    @Test
    public void testExecuteNickSuccess() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.NICK, "", MARIO, 1));
        Response expectedResponse = new Response(CommandType.NICK, "User Mario was added successfully!", MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteNickAlreadyExist() {
        commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));
        Response actualResponse = commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));
        Response expectedResponse = new Response(CommandType.NICK, "The provided nickname has been already added!", MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteNickWithInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.NICK, INVALID_ARGS_COUNT_MESSAGE, MARIO,0));
        Response expectedResponse = new Response(CommandType.NICK, INVALID_ARGS_COUNT_MESSAGE, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteSendRecipientNotExist() {
        commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));
        Response actualResponse = commandExecutor.execute(new Command(CommandType.SEND, "Pesho Helloy", MARIO,2));
        Response expectedResponse = new Response(CommandType.SEND, RECIPIENT_NOT_REGISTERED, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteSendWithInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.SEND, "send", MARIO,0));
        Response expectedResponse = new Response(CommandType.SEND, INVALID_ARGS_COUNT_MESSAGE, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteSendSenderNotExist() {
        commandExecutor.execute(new Command(CommandType.NICK, "", PESHO,1));
        Response actualResponse = commandExecutor.execute(new Command(CommandType.SEND, "Pesho Helloy", MARIO,2));
        Response expectedResponse = new Response(CommandType.SEND, ORIGIN_NOT_REGISTERED, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteSendSuccess() {
        commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));
        commandExecutor.execute(new Command(CommandType.NICK, "", PESHO,1));

        Response actualResponse = commandExecutor.execute(new Command(CommandType.SEND, "Pesho Helloy", MARIO, 2));
        String[] message = actualResponse.message().split(" ",2);
        Response actualResponseWithoutDate = new Response(actualResponse.type(),message[1],actualResponse.recipient());
        Response expectedResponse = new Response(CommandType.SEND, "Mario: Helloy", PESHO);

        assertTrue(expectedResponse.equals(actualResponseWithoutDate));
    }

    @Test
    public void testExecuteSendAllSenderIsNotRegistered() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.SEND_ALL, "Pesho Helloy", MARIO,1));
        Response expectedResponse = new Response(CommandType.SEND_ALL, INVALID_ARGS_COUNT_MESSAGE, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteSendAllWithInvalidArguments() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.SEND_ALL, "send-all", MARIO,0));
        Response expectedResponse = new Response(CommandType.SEND_ALL, INVALID_ARGS_COUNT_MESSAGE, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteSendAllSuccess() {
        commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));

        Response actualResponse = commandExecutor.execute(new Command(CommandType.SEND_ALL, "Helloy", MARIO, 1));
        Response expectedResponse = new Response(CommandType.SEND_ALL, "Mario: Helloy", "send-all");
        String[] message = actualResponse.message().split(" ",2);
        Response actualResponseWithoutDate = new Response(actualResponse.type(),message[1],actualResponse.recipient());

        assertTrue(expectedResponse.equals(actualResponseWithoutDate));
    }

    @Test
    public void testExecuteListUsersSenderNotExist() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LIST_USERS, "", MARIO,0));
        Response expectedResponse = new Response(CommandType.LIST_USERS, ORIGIN_NOT_REGISTERED, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteListUsersInvalidArgumentsCount() {
        commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));
        Response actualResponse = commandExecutor.execute(new Command(CommandType.LIST_USERS, INVALID_ARGS_COUNT_MESSAGE, MARIO,1));
        Response expectedResponse = new Response(CommandType.LIST_USERS, INVALID_ARGS_COUNT_MESSAGE, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteListUsersSuccess() {
        commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));
        commandExecutor.execute(new Command(CommandType.NICK, "", PESHO,1));

        Response actualResponse = commandExecutor.execute(new Command(CommandType.LIST_USERS, "", MARIO,0));
        Response expectedResponse = new Response(CommandType.LIST_USERS, "[Pesho, Mario]", MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteDisconnectSenderNotExist() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.DISCONNECT, "", MARIO,0));
        Response expectedResponse = new Response(CommandType.DISCONNECT, ORIGIN_NOT_REGISTERED, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteDisconnectInvalidArgumentsCount() {
        commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));
        Response actualResponse = commandExecutor.execute(new Command(CommandType.DISCONNECT, INVALID_ARGS_COUNT_MESSAGE, MARIO,1));
        Response expectedResponse = new Response(CommandType.DISCONNECT, INVALID_ARGS_COUNT_MESSAGE, MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteDisconnectSuccess() {
        commandExecutor.execute(new Command(CommandType.NICK, "", MARIO,1));

        Response actualResponse = commandExecutor.execute(new Command(CommandType.DISCONNECT, "", MARIO,0));
        Response expectedResponse = new Response(CommandType.DISCONNECT, "Disconnected from server!", MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }

    @Test
    public void testExecuteUnknownCommand() {
        Response actualResponse = commandExecutor.execute(new Command(CommandType.UNKNOWN, "", MARIO,1));
        Response expectedResponse = new Response(CommandType.UNKNOWN, "Unknown command!", MARIO);

        assertTrue(expectedResponse.equals(actualResponse));
    }
}
