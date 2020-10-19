package com.tidal.refactoring.playlist.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * A very simplified version of TrackPlaylist
 */
public class Playlist {

    private Integer id;
    private String playListName;
    private Set<PlaylistTrack> playlistTracks = new HashSet<PlaylistTrack>();
    private Date registeredDate;
    private Date lastUpdated;
    private String uuid;
    private int nrOfTracks;
    private boolean deleted;
    private Float duration;

    public Playlist() {
        this.uuid = UUID.randomUUID().toString();
        Date d = new Date();
        this.registeredDate = d;
        this.lastUpdated = d;
        this.playlistTracks = new HashSet<PlaylistTrack>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public Set<PlaylistTrack> getPlaylistTracks() {
        return playlistTracks;
    }

    public void setPlaylistTracks(Set<PlaylistTrack> playlistTracks) {
        this.playlistTracks = playlistTracks;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getNrOfTracks() {
        return nrOfTracks;
    }

    public void setNrOfTracks(int nrOfTracks) {
        this.nrOfTracks = nrOfTracks;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

}
