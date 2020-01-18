package socialstudent.eurecom.fr.socialstudent;

import java.util.Objects;

public class Event {

    private String name;
    private String description;
    private double lat;
    private double lng;
    private String place;
    private String time;
    private String date;
    private String key;
    private long type;
    private String organizer ;
    private boolean isPrivate ;

    public Event(String name, String description, double lat, double lng, String place, String time, String date, String key, long type, String organizer, boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.place = place;
        this.time = time;
        this.date = date;
        this.key = key;
        this.type = type;
        this.organizer = organizer;
        this.isPrivate = isPrivate;
    }

    public Event() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Double.compare(event.getLat(), getLat()) == 0 &&
                Double.compare(event.getLng(), getLng()) == 0 &&
                Objects.equals(getName(), event.getName()) &&
                Objects.equals(getDescription(), event.getDescription()) &&
                Objects.equals(getTime(), event.getTime()) &&
                Objects.equals(getDate(), event.getDate());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getDescription(), getLat(), getLng(), getTime(), getDate());
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
}
