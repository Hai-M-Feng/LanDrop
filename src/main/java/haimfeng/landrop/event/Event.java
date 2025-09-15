package haimfeng.landrop.event;

public abstract class Event {
    public String EventName;
    public Event()
    {
    	this.EventName = getClass().getSimpleName();
    }
}
