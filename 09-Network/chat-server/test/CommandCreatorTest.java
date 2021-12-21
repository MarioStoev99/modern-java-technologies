import bg.sofia.uni.fmi.mjt.chat.command.Command;
import bg.sofia.uni.fmi.mjt.chat.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.chat.command.CommandType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandCreatorTest {

    private static final String MARIO = "Mario";

    @Test
    public void testCreateSendSuccess() {
        Command actualCommand = CommandCreator.create("send Petur Helloy", MARIO);
        Command expectedCommand = new Command(CommandType.SEND, "Petur Helloy", MARIO, 2);
        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateSendAllSuccess() {
        Command actualCommand = CommandCreator.create("send-all Helloy everyone", MARIO);
        Command expectedCommand = new Command(CommandType.SEND_ALL, "Helloy everyone", MARIO, 1);
        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateNickSuccess() {
        Command actualCommand = CommandCreator.create("nick Mario", MARIO);
        Command expectedCommand = new Command(CommandType.NICK, "", MARIO, 1);

        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateOneWordCommandsSuccess() {
        Command actualCommand = CommandCreator.create("disconnect", MARIO);
        Command expectedCommand = new Command(CommandType.DISCONNECT, "", MARIO, 0);

        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateUnknownCommand() {
        Command actualCommand = CommandCreator.create("oxoboxo Mario", MARIO);
        Command expectedCommand = new Command(CommandType.UNKNOWN, "", MARIO, 0);

        assertTrue(expectedCommand.equals(actualCommand));
    }
}
