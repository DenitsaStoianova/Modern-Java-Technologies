package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.util.Set;

public record UserInfo(String username, String password, Set<String> wishList) {
}
