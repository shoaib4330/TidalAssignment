package com.tidal.refactoring.playlist.interfaces;

import com.tidal.refactoring.playlist.data.PlaylistTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import com.tidal.refactoring.playlist.exception.ValidationException;

import java.util.List;

public interface PlaylistService {

    /**
     * Add the tracks to the playlist sent as list
     *
     * @param uuid identifies the playlist
     * @param tracksToAdd Tracks to add to the playlist
     * @param insertionIndex Index to insert tracks at
     * @return The newly tracks added tracks to the playlist
     * @throws PlaylistException
     * @throws ValidationException
     */
    List<PlaylistTrack> addTracks(String uuid, List<Track> tracksToAdd, int insertionIndex);

    /**
     * Remove the tracks from the playlist located at the sent indexes
     *
     * @param uuid identifies the playlist
     * @param indexes indexes of the tracks in the playlist that need to be removed
     * @return the tracks in the playlist after the removal
     * @throws PlaylistException
     * @throws ValidationException
     */
    List<PlaylistTrack> removeTracks(String uuid, List<Integer> indexes);
}
