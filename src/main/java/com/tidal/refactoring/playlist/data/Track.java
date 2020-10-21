package com.tidal.refactoring.playlist.data;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Track {
  private int id;
  private String title;
  private float duration;
  private int artistId;
}
