package com.tidal.refactoring.playlist.data;

import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlaylistTrack implements Comparable<PlaylistTrack> {

  private Integer id;
  private Playlist playlist;
  private int index;
  private LocalDate dateAdded;
  private int trackId;

  private Track track;

  @Override
  public int compareTo(PlaylistTrack o) {
    return this.getIndex() - o.getIndex();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PlaylistTrack that = (PlaylistTrack) o;

    if (index != that.index) return false;
    if (trackId != that.trackId) return false;
    if (dateAdded != null ? !dateAdded.equals(that.dateAdded) : that.dateAdded != null)
      return false;
    return !(id != null ? !id.equals(that.id) : that.id != null);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + index;
    result = 31 * result + (dateAdded != null ? dateAdded.hashCode() : 0);
    result = 31 * result + trackId;
    return result;
  }

  public String toString() {
    return "PlaylistTrack id[" + getId() + "], trackId[" + getTrackId() + "]";
  }
}
