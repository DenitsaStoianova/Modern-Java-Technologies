package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Story extends AbstractContent {
    private final int expiredHours = 24;

    public Story(String id, LocalDateTime publishedOn, List<String> tags, Set<String> mentions) {
        super(id, publishedOn, tags, mentions);
    }

    @Override
    public boolean isNotExpired() {
        return super.getPublishedOn().plusHours(expiredHours).isAfter(LocalDateTime.now());
    }
}
