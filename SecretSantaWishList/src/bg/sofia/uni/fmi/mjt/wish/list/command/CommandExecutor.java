package bg.sofia.uni.fmi.mjt.wish.list.command;

import bg.sofia.uni.fmi.mjt.wish.list.storage.Storage;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class CommandExecutor {

    private static final String successRegister = "[ Username %s successfully registered ]";
    private static final String successLoggedOut = "[ Successfully logged out ]";
    private static final String successLoggedIn = "[ User %s successfully logged in ]";
    private static final String successSubmitted = "[ Gift %s for student %s submitted successfully ]";

    private static final String invalidUsername = "[ Username %s is invalid, select a valid one ]";
    private static final String invalidInfo = "[ Invalid username/password combination ]";

    private static final String takenUsername = "[ Username %s is already taken, select another one ]";
    private static final String alreadySubmitted = "[ The same gift for student %s was already submitted ]";

    private static final String notRegistered = "[ Student with username %s is not registered ]";
    private static final String notLogged = "[ You are not logged in ]";
    private static final String noStudents = "[ There are no students present in the wish list ]";

    private static final String getUserWish = "[ %s: %s ]";

    private static final String disconnectUser = "[ Disconnected from server ]";
    private static final String unknown = "[ Unknown command ]";

    private static final String register = "register";
    private static final String login = "login";
    private static final String postWish = "post-wish";
    private static final String getWish = "get-wish";
    private static final String logout = "logout";
    private static final String disconnect = "disconnect";

    private static final int commandIndex = 0;
    private static final int usernameIndex = 1;
    private static final int passwordIndex = 2;
    private static final int wishIndex = 2;

    private static final int complexCommandArgsLength = 3;
    private static final int simpleCommandArgsLength = 1;

    private final Storage storage;
    private final Random generator;

    public CommandExecutor(Storage storage) {
        this.storage = storage;
        this.generator = new Random();
    }

    public String executeCommand(String clientMessage) {
        String[] clientWishArgs = splitCommand(clientMessage);

        String commandType = clientWishArgs[commandIndex];

        if (checkCommand(commandType, clientWishArgs.length)) {
            return unknown;
        }

        return switch (commandType) {
            case register -> register(clientWishArgs[usernameIndex], clientWishArgs[passwordIndex]);
            case login -> login(clientWishArgs[usernameIndex], clientWishArgs[passwordIndex]);
            case postWish -> postWish(clientWishArgs[usernameIndex], clientWishArgs[wishIndex]);
            case getWish -> getWish();
            case logout -> logout();
            case disconnect -> disconnect();
            default -> unknown;
        };
    }

    private boolean checkCommand(String commandType, int argumentsCount) {
        return checkComplexCommand(commandType, argumentsCount) || checkSimpleCommand(commandType, argumentsCount);
    }

    private boolean checkComplexCommand(String commandType, int argumentsCount) {
        return (commandType.equals(register) || commandType.equals(login) || commandType.equals(postWish))
                && argumentsCount != complexCommandArgsLength;
    }

    private boolean checkSimpleCommand(String commandType, int argumentsCount) {
        return (commandType.equals(getWish) || commandType.equals(logout) || commandType.equals(disconnectUser))
                && argumentsCount != simpleCommandArgsLength;
    }

    private String register(String username, String password) {
        if (!checkUsername(username)) {
            return String.format(invalidUsername, username);
        }

        return storage.addUser(username, password) ? String.format(successRegister, username)
                : String.format(takenUsername, username);
    }

    private String login(String username, String password) {
        if (storage.isLogged(username, password)) {
            return String.format(successLoggedIn, username);
        }
        return invalidInfo;
    }

    private String postWish(String username, String present) {
        if (storage.isUserLogged()) {
            return notLogged;
        }

        if (!storage.containsUser(username)) {
            return String.format(notRegistered, username);
        }

        return storage.addWish(username, present) ? String.format(successSubmitted, present, username)
                : String.format(alreadySubmitted, username);
    }

    private String getWish() {
        if (storage.isUserLogged()) {
            return notLogged;
        }

        if (storage.hasUsers()) {
            return noStudents;
        }

        return getRandomStudentInfo();
    }

    private String logout() {
        if (storage.isUserLogged()) {
            return notLogged;
        }
        storage.logoutCurrentUser();
        return successLoggedOut;
    }

    private String disconnect() {
        storage.logoutCurrentUser();
        return disconnectUser;
    }

    private String[] splitCommand(String clientMessage) {
        return clientMessage.split("\\s+", 3);
    }

    private boolean checkUsername(String username) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9-._]");
        return !pattern.matcher(username).find();
    }

    private String getRandomStudentInfo() {
        List<String> filteredUsers = storage.generateUserList();
        if (filteredUsers.size() == 0) {
            return noStudents;
        }

        String selectedUser = filteredUsers.get(generator.nextInt(filteredUsers.size()));
        return String.format(getUserWish, selectedUser, storage.getUserWishList(selectedUser));
    }

}

