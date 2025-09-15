package haimfeng.landrop.event;

public abstract class AppEvent extends Event {
    public String EventName;
    public String EventData;

    public AppEvent(String eventData)
    {
        EventName = getClass().getSimpleName();
        EventData = eventData;
    }
}

