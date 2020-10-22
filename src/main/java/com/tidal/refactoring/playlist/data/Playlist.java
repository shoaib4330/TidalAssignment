package com.tidal.refactoring.playlist.data;

import lombok.*;

import java.time.LocalDate;
import java.util.*;

/** A very simplified version of TrackPlaylist */
@Getter
@Setter
public class Playlist {

    private Integer id;
    private String playListName;

    private Set<PlaylistTrack> playlistTracks;

    private LocalDate registeredDate;
    private LocalDate lastUpdated;
    private String uuid;
    private int nrOfTracks;
    private boolean deleted;
    private Float duration;

    @Builder
    public Playlist(
            Integer id,
            String playListName,
            @Singular("playlistTrack") Set<PlaylistTrack> playlistTracks,
            LocalDate registeredDate,
            LocalDate lastUpdated,
            String uuid,
            int nrOfTracks,
            boolean deleted,
            Float duration) {
        this.id = id;
        this.playListName = playListName;
        this.playlistTracks = new HashSet<>(playlistTracks);
        this.registeredDate = registeredDate;
        this.lastUpdated = lastUpdated;
        this.uuid = uuid;
        this.nrOfTracks = nrOfTracks;
        this.deleted = deleted;
        this.duration = duration;
    }
}
