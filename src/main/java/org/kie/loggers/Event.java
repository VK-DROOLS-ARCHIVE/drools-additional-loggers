package org.kie.loggers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Event {

    private String toString;
    private int type;
    private List<Event> subEvents = new ArrayList<Event>();
    private Event causeEvent;

    public Event(int type) {
        this.type = type;
    }

    public void setString(String toString) {
        this.toString = toString;
    }

    public String toString() {
        return toString;
    }

    public int getType() {
        return type;
    }

    public void addSubEvent(Event subEvent) {
        subEvents.add(subEvent);
    }

    public void addSubEvents(Collection<Event> subEvents) {
        this.subEvents.addAll(subEvents);
    }

    public List<Event> getSubEvents() {
        return subEvents;
    }

    public boolean hasSubEvents() {
        return !subEvents.isEmpty();
    }

    public void setCauseEvent(Event causeEvent) {
        this.causeEvent = causeEvent;
    }

    public Event getCauseEvent() {
        return causeEvent;
    }
}