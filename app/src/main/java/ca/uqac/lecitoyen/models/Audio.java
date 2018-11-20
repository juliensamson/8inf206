package ca.uqac.lecitoyen.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Audio implements Parcelable {

    private String aid;

    private String pid;

    private String creatorid;

    private String collaboratorid;

    private String title;

    private String genre;

    private String description;

    private String lyrics;

    private long releaseDate;

    private long playtime;

    private String externeLink;

    public Audio() {

    }

    protected Audio(Parcel in) {
        aid = in.readString();
        pid = in.readString();
        creatorid = in.readString();
        collaboratorid = in.readString();
        title = in.readString();
        genre = in.readString();
        description = in.readString();
        lyrics = in.readString();
        releaseDate = in.readLong();
        playtime = in.readLong();
        externeLink = in.readString();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
    }

    public String getCollaboratorid() {
        return collaboratorid;
    }

    public void setCollaboratorid(String collaboratorid) {
        this.collaboratorid = collaboratorid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public String getExterneLink() {
        return externeLink;
    }

    public void setExterneLink(String externeLink) {
        this.externeLink = externeLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(aid);
        parcel.writeString(pid);
        parcel.writeString(creatorid);
        parcel.writeString(collaboratorid);
        parcel.writeString(title);
        parcel.writeString(genre);
        parcel.writeString(description);
        parcel.writeString(lyrics);
        parcel.writeLong(releaseDate);
        parcel.writeLong(playtime);
        parcel.writeString(externeLink);
    }
}
