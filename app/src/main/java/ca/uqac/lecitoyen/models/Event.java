package ca.uqac.lecitoyen.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Event implements Parcelable {

    private String eid;

    private String pid;

    private String title;

    private long eventDate;

    private String location;

    private String eventType;

    private String details;

    private int price;

    private ArrayList<User> administrators;

    private ArrayList<User> attendees;

    private ArrayList<User> interested;

    private boolean isPrivate;

    /**
     *
     *      Constructor
     *
     */

    public Event() { }

    public Event(String eid, String title, long eventDate, String eventType, String details, ArrayList<User> administrators, boolean isPrivate) {
        this.eid = eid;
        this.title = title;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.details = details;
        this.administrators = administrators;
        this.isPrivate = isPrivate;
    }

    protected Event(Parcel in) {
        eid = in.readString();
        pid = in.readString();
        title = in.readString();
        eventDate = in.readLong();
        location = in.readString();
        eventType = in.readString();
        details = in.readString();
        price = in.readInt();
        administrators = in.createTypedArrayList(User.CREATOR);
        attendees = in.createTypedArrayList(User.CREATOR);
        interested = in.createTypedArrayList(User.CREATOR);
        isPrivate = in.readByte() != 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    /**
     *
     *      Getter's & Setter's
     *
     */

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ArrayList<User> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(ArrayList<User> administrators) {
        this.administrators = administrators;
    }

    public ArrayList<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<User> attendees) {
        this.attendees = attendees;
    }

    public ArrayList<User> getInterested() {
        return interested;
    }

    public void setInterested(ArrayList<User> interested) {
        this.interested = interested;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eid);
        parcel.writeString(pid);
        parcel.writeString(title);
        parcel.writeLong(eventDate);
        parcel.writeString(location);
        parcel.writeString(eventType);
        parcel.writeString(details);
        parcel.writeInt(price);
        parcel.writeTypedList(administrators);
        parcel.writeTypedList(attendees);
        parcel.writeTypedList(interested);
        parcel.writeByte((byte) (isPrivate ? 1 : 0));
    }
}
