package bg.sofia.uni.fmi.mjt.wish.list.command;

import bg.sofia.uni.fmi.mjt.wish.list.storage.Storage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandExecutorTest {

    private static final String unknownExpected = "[ Unknown command ]";
    private static final String notLoggedExpected = "[ You are not logged in ]";
    private static final String noStudentsExpected = "[ There are no students present in the wish list ]";

    private final String registerCommand = "register Kevin78 123";
    private final String loginCommand = "login Kevin78 123";
    private final String postWishCommand = "post-wish Kevin78 bike";
    private final String getWishCommand = "get-wish";

    private Storage storage;
    private CommandExecutor commandExecutor;

    @Before
    public void setUp() {
        storage = mock(Storage.class);
        commandExecutor = new CommandExecutor(storage);
    }

    @Test
    public void testExecuteCommandComplexCommandWrongParametersCount() {
        final String command = "register Kevin";
        String actual = commandExecutor.executeCommand(command);

        assertEquals("Unknown command message expected.", unknownExpected, actual);
    }

    @Test
    public void testExecuteCommandSimpleCommandWrongParametersCount() {
        final String command = "logout Kevin";
        String actual = commandExecutor.executeCommand(command);

        assertEquals("Unknown command message expected.", unknownExpected, actual);
    }

    @Test
    public void testExecuteCommandUnknownCommand() {
        final String command = "get-with";
        String actual = commandExecutor.executeCommand(command);

        assertEquals("Unknown command message expected.", unknownExpected, actual);
    }

    @Test
    public void testRegisterInvalidUsername() {
        final String expected = "[ Username Kev&n) is invalid, select a valid one ]";
        final String command = "register Kev&n) 123";
        String actual = commandExecutor.executeCommand(command);

        assertEquals("Invalid command message expected.", expected, actual);
    }

    @Test
    public void testRegisterNewValidUserSuccess() {
        when(storage.addUser("Kevin78", "123")).thenReturn(true);
        final String expected = "[ Username Kevin78 successfully registered ]";
        String actual = commandExecutor.executeCommand(registerCommand);

        assertEquals("Success command message expected.", expected, actual);
    }

    @Test
    public void testRegisterNewValidUserTakenUsername() {
        when(storage.addUser("Kevin78", "123")).thenReturn(false);
        final String expected = "[ Username Kevin78 is already taken, select another one ]";
        String actual = commandExecutor.executeCommand(registerCommand);

        assertEquals("Already taken username message expected.", expected, actual);
    }

    @Test
    public void testLoginInvalidCombination() {
        when(storage.isLogged("Kevin12", "123")).thenReturn(false);
        final String expected = "[ Invalid username/password combination ]";
        String actual = commandExecutor.executeCommand(loginCommand);

        assertEquals("Invalid combination message expected.", expected, actual);
    }

    @Test
    public void testLoginSuccess() {
        when(storage.isLogged("Kevin78", "123")).thenReturn(true);
        final String expected = "[ User Kevin78 successfully logged in ]";
        String actual = commandExecutor.executeCommand(loginCommand);

        assertEquals("Success message expected.", expected, actual);
    }

    @Test
    public void testPostWishNotLoggedUser() {
        when(storage.isUserLogged()).thenReturn(true);
        String actual = commandExecutor.executeCommand(postWishCommand);

        assertEquals("Not logged message expected.", notLoggedExpected, actual);
    }

    @Test
    public void testPostWishNotRegistered() {
        final String username = "Kevin78";
        final String expected = "[ Student with username Kevin78 is not registered ]";
        when(storage.containsUser(username)).thenReturn(false);
        String actual = commandExecutor.executeCommand(postWishCommand);

        assertEquals("Not registered message expected.", expected, actual);
    }

    @Test
    public void testPostWishSuccess() {
        final String username = "Kevin78";
        final String wish = "bike";
        final String expected = "[ Gift bike for student Kevin78 submitted successfully ]";
        when(storage.containsUser(username)).thenReturn(true);
        when(storage.addWish(username, wish)).thenReturn(true);
        String actual = commandExecutor.executeCommand(postWishCommand);

        assertEquals("Successfully submitted message expected.", expected, actual);
    }

    @Test
    public void testPostWishAlreadySubmitted() {
        final String username = "Kevin78";
        final String wish = "bike";
        final String expected = "[ The same gift for student Kevin78 was already submitted ]";
        when(storage.containsUser(username)).thenReturn(true);
        when(storage.addWish(username, wish)).thenReturn(false);
        String actual = commandExecutor.executeCommand(postWishCommand);

        assertEquals("Already submitted message expected.", expected, actual);
    }

    @Test
    public void testGetWishNotLoggedUser() {
        when(storage.isUserLogged()).thenReturn(true);
        String actual = commandExecutor.executeCommand(getWishCommand);

        assertEquals("Not logged message expected.", notLoggedExpected, actual);
    }

    @Test
    public void testGetWishNoStudents() {
        when(storage.hasUsers()).thenReturn(true);
        String actual = commandExecutor.executeCommand(getWishCommand);

        assertEquals("No students message expected.", noStudentsExpected, actual);
    }

    @Test
    public void testGetWishEmptyFilteredUsers() {
        when(storage.generateUserList()).thenReturn(new ArrayList<>());
        String actual = commandExecutor.executeCommand(getWishCommand);

        assertEquals("No students message expected.", noStudentsExpected, actual);
    }

    @Test
    public void testGetWishSuccess() {
        final String username = "Kevin";
        final String expected = "[ Kevin: [ball, bike] ]";
        when(storage.generateUserList()).thenReturn(List.of(username));
        when(storage.getUserWishList(username)).thenReturn("[ball, bike]");

        String actual = commandExecutor.executeCommand(getWishCommand);

        assertEquals("User wishes message expected.", expected, actual);
    }

    @Test
    public void testLogoutNotLogged() {
        final String command = "logout";
        when(storage.isUserLogged()).thenReturn(true);
        String actual = commandExecutor.executeCommand(command);

        assertEquals("Not logged message expected.", notLoggedExpected, actual);
    }

    @Test
    public void testNotLogged() {
        final String command = "logout";
        final String expected = "[ Successfully logged out ]";
        String actual = commandExecutor.executeCommand(command);

        assertEquals("Success message expected.", expected, actual);
    }

    @Test
    public void testDisconnect() {
        final String expected = "[ Disconnected from server ]";
        final String command = "disconnect";
        String actual = commandExecutor.executeCommand(command);

        assertEquals("Disconnect message expected.", expected, actual);
    }
}
