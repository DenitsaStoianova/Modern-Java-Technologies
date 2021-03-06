package bg.sofia.uni.fmi.mjt.socialmedia.user;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class User {
    private String username;
    private int mentions;
    private List<ActivityElement> activityLog;
    private Set<Content> contents;

    public User(String username) {
        this.username = username;
        this.activityLog = new ArrayList<>();
        this.contents = new TreeSet<>(compareByDate);
    }

    public void addContent(Content content) {
        contents.add(content);
    }

    public Set<Content> getContents() {
        return contents;
    }

    public void doActivity(ActivityType activityType, String id, LocalDateTime publishedOn) {
        String activityText = String.format("%s with id %s", activityType.getDescription(), id);
        ActivityElement activityElement = new ActivityElement(activityText, publishedOn);
        activityLog.add(activityElement);
    }

    public void doActivity(String id, LocalDateTime publishedOn, String text) {
        String activityText = String.format("Commented \"%s\" on a content with id %s", text, id);
        ActivityElement activityElement = new ActivityElement(activityText, publishedOn);
        activityLog.add(activityElement);
    }

    public int getMentions() {
        return mentions;
    }

    public void mention() {
        mentions++;
    }

    public List<String> getActivityLog() {
        List<String> activities = new ArrayList<>();
        if (activityLog.size() > 0) {
            Collections.sort(activityLog);
            for (ActivityElement activityElement : activityLog) {
                activities.add(activityElement.getFormattedText());
            }
        }
        return activities;
    }

    private Comparator<Content> compareByDate = new Comparator<Content>() {
        @Override
        public int compare(Content o1, Content o2) {
            int result = o2.getPublishedDate().compareTo(o1.getPublishedDate());
            if (result == 0) {
                return o1.getId().compareTo(o2.getId());
            }
            return result;
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
