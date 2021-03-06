package bg.sofia.uni.fmi.mjt.socialmedia.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityElement implements Comparable<ActivityElement> {
    private String text;
    private LocalDateTime publishedOn;

    public ActivityElement(String text, LocalDateTime publishedOn) {
        this.text = text;
        this.publishedOn = publishedOn;
    }

    public String getFormattedText() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        String formatted = String.format("%s: %s", publishedOn.format(dateTimeFormatter), text);
        return formatted;
    }

    @Override
    public int compareTo(ActivityElement o) {
        return -publishedOn.compareTo(o.publishedOn);
    }
}
