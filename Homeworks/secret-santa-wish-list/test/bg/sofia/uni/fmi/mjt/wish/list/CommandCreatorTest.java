package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.command.Command;
import bg.sofia.uni.fmi.mjt.wish.list.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.wish.list.command.CommandType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandCreatorTest {

    @Test
    public void testCreatePostWishCommand() {
        Command actualCommand = CommandCreator.create("post-wish Mario oxoboxo oxoboxo","Mario");
        Command expectedCommand = new Command(CommandType.POST_WISH,"Mario oxoboxo oxoboxo","Mario",3);

        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateDisconnectCommand() {
        Command actualCommand = CommandCreator.create("disconnect","Mario");
        Command expectedCommand = new Command(CommandType.DISCONNECT,"","Mario",0);

        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateLogoutCommand() {
        Command actualCommand = CommandCreator.create("logout","Mario");
        Command expectedCommand = new Command(CommandType.LOGOUT,"","Mario",0);

        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateGetWishCommand() {
        Command actualCommand = CommandCreator.create("get-wish","Mario");
        Command expectedCommand = new Command(CommandType.GET_WISH,"","Mario",0);

        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateRegisterCommand() {
        Command actualCommand = CommandCreator.create("register Mario 123456","Mario");
        Command expectedCommand = new Command(CommandType.REGISTER,"Mario 123456","Mario",2);

        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateLoginCommand() {
        Command actualCommand = CommandCreator.create("login Mario 123456","Mario");
        Command expectedCommand = new Command(CommandType.LOGIN,"Mario 123456","Mario",2);

        assertTrue(expectedCommand.equals(actualCommand));
    }

    @Test
    public void testCreateUnknownCommand() {
        Command actualCommand = CommandCreator.create("save 5262352635","Mario");
        Command expectedCommand = new Command(CommandType.UNKNOWN,"5262352635","Mario",1);

        assertTrue(expectedCommand.equals(actualCommand));
    }


}
