package com.tidal.refactoring.playlist.interfaces;

import com.tidal.refactoring.playlist.data.Playlist;

import java.util.Optional;

public interface PlaylistRepository {

    Optional<Playlist> getPlaylistByUUID(String uuid);

}
