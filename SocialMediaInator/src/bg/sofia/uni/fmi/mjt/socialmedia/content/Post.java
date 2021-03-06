package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Post extends AbstractContent {
    private final int expiredDays = 30;

    public Post(String id, LocalDateTime publishedOn, List<String> tags, Set<String> mentions) {
        super(id, publishedOn, tags, mentions);
    }

    @Override
    public boolean isNotExpired() {
        return super.getPublishedOn().plusDays(expiredDays).isAfter(LocalDateTime.now());
    }
}
