package bg.sofia.uni.fmi.mjt.socialmedia.user;

public enum ActivityType {
    LIKE("Liked a content"),
    CREATE_POST("Created a post"),
    CREATE_STORY("Created a story");

    private String description;

    ActivityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
