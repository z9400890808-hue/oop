package roadaccident.model;

public class TimelineEvent {

    private final String eventTime;
    private final String description;

    public TimelineEvent(String eventTime, String description) {
        this.eventTime = eventTime;
        this.description = description;
    }

    @Override
    public String toString() {
        return eventTime + "  |  " + description;
    }
}
