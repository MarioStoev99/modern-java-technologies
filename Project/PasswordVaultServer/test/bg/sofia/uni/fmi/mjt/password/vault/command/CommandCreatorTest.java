package bg.sofia.uni.fmi.mjt.password.vault.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandCreatorTest {

    @Test
    public void testCreateCommandWithThreeArguments() {
        Command actualCommand = CommandCreator.create("register Mario 123456 123456","");
        Command expectedCommand = new Command(CommandType.REGISTER, "Mario 123456 123456", "");

        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    public void testCreateCommandWithTwoArguments() {
        Command actualCommand = CommandCreator.create("generate-password facebook.com Mario","Mario");
        Command expectedCommand = new Command(CommandType.GENERATE_PASSWORD, "facebook.com Mario", "Mario");

        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    public void testCreateCommandWithoutArguments() {
        Command actualCommand = CommandCreator.create("disconnect", "Mario");
        Command expectedCommand = new Command(CommandType.DISCONNECT, "", "Mario");

        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    public void testCreateUnknownCommand() {
        Command actualCommand = CommandCreator.create("oxoboxo Mario","Mario");
        Command expectedCommand = new Command(CommandType.UNKNOWN, "Mario", "Mario");

        assertEquals(expectedCommand, actualCommand);
    }
}
