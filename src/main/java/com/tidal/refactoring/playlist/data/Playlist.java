package com.tidal.refactoring.playlist.data;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** A very simplified version of TrackPlaylist */
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Playlist {

  private Integer id;
  private String playListName;

  @Singular("playlistTrack")
  private Set<PlaylistTrack> playlistTracks;

  private LocalDate registeredDate;
  private LocalDate lastUpdated;
  private String uuid;
  private int nrOfTracks;
  private boolean deleted;
  private Float duration;

  public Playlist() {
    this.uuid = UUID.randomUUID().toString();
    LocalDate d = LocalDate.now();
    this.registeredDate = d;
    this.lastUpdated = d;
    this.playlistTracks = new HashSet<PlaylistTrack>();
  }
}
