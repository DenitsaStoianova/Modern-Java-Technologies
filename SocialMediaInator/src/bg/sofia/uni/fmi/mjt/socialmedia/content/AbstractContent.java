package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Collection;

public abstract class AbstractContent implements Content {
    private int numberOfLikes;
    private int numberOfComments;
    private String id;
    private List<String> tags;
    private Set<String> mentions;
    private LocalDateTime publishedOn;
    private Set<String> likesByUsers;

    public AbstractContent(String id, LocalDateTime publishedOn, List<String> tags, Set<String> mentions) {
        this.id = id;
        this.publishedOn = publishedOn;
        this.tags = tags;
        this.mentions = mentions;
        this.likesByUsers = new HashSet<>();
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    @Override
    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    @Override
    public int getNumberOfComments() {
        return numberOfComments;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<String> getTags() {
        return tags;
    }

    @Override
    public Collection<String> getMentions() {
        return mentions;
    }

    @Override
    public void like(String username) {
        if (!likesByUsers.contains(username)) {
            numberOfLikes++;
            likesByUsers.add(username);
        }
    }

    @Override
    public void comment() {
        numberOfComments++;
    }

    public LocalDateTime getPublishedDate() {
        return publishedOn;
    }

    @Override
    public abstract boolean isNotExpired();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractContent that = (AbstractContent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
