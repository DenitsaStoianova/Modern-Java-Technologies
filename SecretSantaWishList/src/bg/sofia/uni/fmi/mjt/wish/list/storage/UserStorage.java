package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserStorage implements Storage {

    private final Map<String, UserInfo> users;
    private String currentlyLoggedUser = "";

    public UserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public boolean addUser(String username, String password) {
        if (!users.containsKey(username)) {
            users.put(username, new UserInfo(username, password, new HashSet<>()));
            return true;
        }
        return false;
    }

    @Override
    public boolean isLogged(String username, String password) {
        if (users.containsKey(username) && users.get(username).password().equals(password)) {
            currentlyLoggedUser = username;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsUser(String username) {
        return users.containsKey(username);
    }

    @Override
    public boolean addWish(String username, String present) {
        if (!users.get(username).wishList().contains(present)) {
            users.get(username).wishList().add(present);
            return true;
        }
        return false;
    }

    @Override
    public List<String> generateUserList() {
        return users.entrySet()
                .stream()
                .filter(u -> u.getValue().wishList().size() > 0 && !u.getKey().equals(currentlyLoggedUser))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean hasUsers() {
        return users.isEmpty();
    }

    @Override
    public String getUserWishList(String username) {
        String userWishList = users.get(username).wishList().toString();
        users.get(username).wishList().clear();
        return userWishList;
    }

    @Override
    public boolean isUserLogged() {
        return currentlyLoggedUser.isEmpty();
    }

    @Override
    public void logoutCurrentUser() {
        currentlyLoggedUser = "";
    }
}
