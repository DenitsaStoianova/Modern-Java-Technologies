package bg.sofia.uni.fmi.mjt.socialmedia.content;

public enum ContentType {
    POST("Post"),
    STORY("Story");

    private String contentName;

    ContentType(String contentName) {
        this.contentName = contentName;
    }

    public String getContentName() {
        return contentName;
    }
}
