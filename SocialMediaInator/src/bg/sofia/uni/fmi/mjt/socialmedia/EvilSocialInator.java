package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.ContentType;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Post;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Story;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.user.ActivityType;
import bg.sofia.uni.fmi.mjt.socialmedia.user.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class EvilSocialInator implements SocialMediaInator {

    private Map<String, User> usersInfo;
    private Map<String, Content> contents;
    private int contentsCount;

    public EvilSocialInator() {
        usersInfo = new HashMap<>();
        contents = new HashMap<>();
    }

    @Override
    public void register(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null.");
        }

        if (usersInfo.containsKey(username)) {
            String message = String.format("User with username %s already exists in the platform.", username);
            throw new UsernameAlreadyExistsException(message);
        }

        usersInfo.put(username, new User(username));
    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description) {
        String id = publishContent(ContentType.POST, username, publishedOn, description);
        usersInfo.get(username).doActivity(ActivityType.CREATE_POST, id, publishedOn);
        return id;
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description) {
        String id = publishContent(ContentType.STORY, username, publishedOn, description);
        usersInfo.get(username).doActivity(ActivityType.CREATE_STORY, id, publishedOn);
        return id;
    }

    @Override
    public void like(String username, String id) {
        checkParameters(username, id);
        checkUsername(username);
        checkContent(id);
        contents.get(id).like(username);
        usersInfo.get(username).doActivity(ActivityType.LIKE, id, LocalDateTime.now());
    }

    @Override
    public void comment(String username, String text, String id) {
        checkParameters(username, text, id);
        checkUsername(username);
        checkContent(id);
        contents.get(id).comment();
        usersInfo.get(username).doActivity(id, LocalDateTime.now(), text);
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) {
        checkNumberOfContents(n);

        List<Content> contentList = new ArrayList<>(contents.values());
        contentList.sort(popularityComparator);

        return createNewCollection(n, contentList);
    }

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null.");
        }
        checkNumberOfContents(n);
        checkUsername(username);

        Set<Content> userContents = usersInfo.get(username).getContents();
        return createNewCollection(n, userContents);
    }

    @Override
    public String getMostPopularUser() {
        if (usersInfo.isEmpty()) {
            throw new NoUsersException("There are currently no users in the platform.");
        }

        String mostPopular = usersInfo.keySet().iterator().next();
        int mostMentionedCount = 0;
        for (Map.Entry<String, User> entry : usersInfo.entrySet()) {
            if (entry.getValue().getMentions() > mostMentionedCount) {
                mostMentionedCount = entry.getValue().getMentions();
                mostPopular = entry.getKey();
            }
        }
        return mostPopular;
    }

    @Override
    public Collection<Content> findContentByTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag is null.");
        }

        Collection<Content> resContents = new ArrayList<>();
        for (Content content : contents.values()) {
            if (content.isNotExpired() && content.getTags().contains(tag)) {
                resContents.add(content);
            }
        }
        return Collections.unmodifiableCollection(resContents);
    }

    @Override
    public List<String> getActivityLog(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null.");
        }
        checkUsername(username);
        return usersInfo.get(username).getActivityLog();
    }

    private Collection<Content> createNewCollection(int n, Collection<Content> contents) {
        Collection<Content> resultCollection = new ArrayList<>();

        if (contents.size() < n) {
            for (Content content : contents) {
                if (content.isNotExpired()) {
                    resultCollection.add(content);
                }
            }
            return Collections.unmodifiableCollection(resultCollection);
        }

        int contentCount = 0;
        for (Content content : contents) {
            if (contentCount >= n) {
                break;
            }

            if (content.isNotExpired()) {
                contentCount++;
                resultCollection.add(content);
            }
        }

        return Collections.unmodifiableCollection(resultCollection);
    }

    private final Comparator<Content> popularityComparator = new Comparator<Content>() {
        @Override
        public int compare(Content o1, Content o2) {
            return (o2.getNumberOfLikes() + o2.getNumberOfComments())
                    - (o1.getNumberOfLikes() + o1.getNumberOfComments());
        }
    };

    private void checkNumberOfContents(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of contents to get cannot be negative.");
        }
    }

    private void checkParameters(String username, String id) {
        String messageText = String.format("%s%s",
                (username == null) ? "Username " : "",
                (id == null) ? "Id " : "");

        if (!messageText.equals("")) {
            throw new IllegalArgumentException(String.format("%sis null.", messageText));
        }
    }

    private void checkParameters(String username, String text, String id) {
        String messageText = String.format("%s%s%s",
                (username == null) ? "Username " : "",
                (text == null) ? "Text " : "",
                (id == null) ? "Id " : "");

        if (!messageText.equals("")) {
            throw new IllegalArgumentException(String.format("%sis null.", messageText));
        }
    }

    private void checkParameters(String username, LocalDateTime publishedOn, String description) {
        String messageText = String.format("%s%s%s",
                (username == null) ? "Username " : "",
                (publishedOn == null) ? "PublishDate " : "",
                (description == null) ? "Description " : "");

        if (!messageText.equals("")) {
            throw new IllegalArgumentException(String.format("%sis null.", messageText));
        }
    }

    private void checkUsername(String username) {
        if (!usersInfo.containsKey(username)) {
            String message = String.format("User with username %s is not found in the platform.", username);
            throw new UsernameNotFoundException(message);
        }
    }

    private void checkContent(String id) {
        if (!contents.containsKey(id)) {
            String message = String.format("Content with id %s is not fount at the platform.", id);
            throw new ContentNotFoundException(message);
        }
    }

    private String publishContent(ContentType contentType, String username,
                                  LocalDateTime publishedOn, String description) {
        checkParameters(username, publishedOn, description);

        checkUsername(username);

        String id = String.format("%s-%d", username, contentsCount++);
        Content content = createContent(contentType, id, publishedOn, description);

        if (content.getMentions().contains("@" + username)) {
            usersInfo.get(username).mention();
        }

        contents.put(id, content);
        usersInfo.get(username).addContent(content);

        return id;
    }

    public Content createContent(ContentType contentType, String id,
                                 LocalDateTime publishedOn, String description) {
        List<String> tags = new ArrayList<>();
        Set<String> mentions = new HashSet<>();

        String[] descriptionItems = description.split("\\s+");
        for (String descriptionItem : descriptionItems) {
            if (descriptionItem.startsWith("#")) {
                tags.add(descriptionItem);
            }
            if (descriptionItem.startsWith("@")) {
                mentions.add(descriptionItem);
            }
        }

        if (contentType == ContentType.POST) {
            return new Post(id, publishedOn, tags, mentions);
        }
        return new Story(id, publishedOn, tags, mentions);
    }
}
