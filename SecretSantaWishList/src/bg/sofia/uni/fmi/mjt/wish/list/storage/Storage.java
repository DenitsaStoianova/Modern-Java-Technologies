package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.util.List;

public interface Storage {

    boolean addUser(String username, String password);

    boolean isLogged(String username, String password);

    boolean containsUser(String username);

    boolean addWish(String username, String present);

    List<String> generateUserList();

    boolean hasUsers();

    String getUserWishList(String username);

    boolean isUserLogged();

    void logoutCurrentUser();
}
