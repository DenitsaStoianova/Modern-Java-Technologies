package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;

public interface Postable {
    void like(String username);

    void comment();

    boolean isNotExpired(); // returns true if content is not expired

    LocalDateTime getPublishedDate();
}
