package bg.sofia.uni.fmi.mjt.wish.list.storage;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserStorageTest {

    private Storage storage;

    private final String testUser = "Kevin";
    private final String testPassword = "123K";

    @Before
    public void setUp() {
        storage = new UserStorage();
        storage.addUser(testUser, testPassword);
    }

    @Test
    public void testAddUserStorageNotContainsUser() {
        final String username = "Paul";
        final String password = "456";

        assertTrue("Expected true when new user is added.", storage.addUser(username, password));
    }

    @Test
    public void testAddUserStorageContainsUser() {
        assertFalse("Expected false when new user is added.", storage.addUser(testUser, testPassword));
    }

    @Test
    public void testIsLoggedWithNotLoggedUser() {
        final String username = "Paul";
        final String password = "456";

        assertFalse("Expected false when new user is not logged.", storage.isLogged(username, password));
    }

    @Test
    public void testIsLoggedWithLoggedUser() {
        assertTrue("Expected true when user is logged.", storage.isLogged(testUser, testPassword));
    }

    @Test
    public void testIsLoggedNotCorrectPassword() {
        final String password = "6535";
        assertFalse("Expected false when password is not correct.", storage.isLogged(testUser, password));
    }

    @Test
    public void addPresentNotContains() {
        final String present = "ball";
        assertTrue("Expected true when added present is not available in user wishList.",
                storage.addWish(testUser, present));
    }

    @Test
    public void addPresentContains() {
        final String present = "ball";
        storage.addWish(testUser, present);

        assertFalse("Expected false when added present is available in user wishList.",
                storage.addWish(testUser, present));
    }

    @Test
    public void testGenerateUserListEmpty() {
        final String username = "Paul";
        final String password = "456";
        storage.addUser(username, password);
        storage.addUser(testUser, testPassword);
        assertTrue("Expected user with nonzero presents at his wish list and not currently logged user.",
                storage.generateUserList().isEmpty());
    }

    @Test
    public void testGenerateUserListOneUser() {
        final String username1 = "Paul";
        final String password1 = "456";
        final String username2 = "Paul";
        final String password2 = "456";
        final String present = "ball";
        storage.addUser(testUser, testPassword);
        storage.addUser(username1, password1);
        storage.addUser(username2, password2);
        storage.addWish(username1, present);
        final List<String> expected = List.of(username1);
        List<String> actual = storage.generateUserList();

        assertEquals("Expected user with nonzero presents at his wish list and not currently logged user.",
                expected, actual);
    }

    @Test
    public void testGetUserWishList() {
        final String expected = "[ball, bike]";

        final String present1 = "ball";
        final String present2 = "bike";
        storage.addWish(testUser, present1);
        storage.addWish(testUser, present2);
        String actual = storage.getUserWishList(testUser);

        assertEquals("Not correct wish list.", expected, actual);
    }


}
