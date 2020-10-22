package com.tidal.refactoring.playlist;

import com.tidal.refactoring.playlist.data.Playlist;
import com.tidal.refactoring.playlist.data.PlaylistTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import com.tidal.refactoring.playlist.exception.ValidationException;
import com.tidal.refactoring.playlist.interfaces.PlaylistRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class PlaylistService {
    private static final Integer PLAYLIST_MAX_SIZE = 500;

    private PlaylistRepository playlistRepository;

    private BusinessUtils businessUtils;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
        this.businessUtils = new BusinessUtils();
    }

    /** Add tracks to the index */
    public List<PlaylistTrack> addTracks(String uuid, List<Track> tracksToAdd, int toIndex)
            throws PlaylistException {

        if (StringUtils.isEmpty(uuid))
            throw new ValidationException("uuid must not be null or empty");
        if (CollectionUtils.isEmpty(tracksToAdd))
            throw new ValidationException("tracksToAdd must not be null or empty");

        Playlist playList =
                playlistRepository
                        .getPlaylistByUUID(uuid)
                        .orElseThrow(() -> new PlaylistException("Playlist not found"));

        /* We do not allow more than MAX_TRACKS in playlists */
        if (playList.getNrOfTracks() + tracksToAdd.size() > PLAYLIST_MAX_SIZE) {
            throw new PlaylistException(
                    "Playlist cannot have more than " + PLAYLIST_MAX_SIZE + " tracks");
        }

        /* The index is out of bounds, put it in the end of the list */
        int size = playList.getNrOfTracks();
        if (toIndex > size || toIndex == -1) {
            toIndex = size;
        }

        if (!validateIndexes(toIndex, playList.getNrOfTracks())) {
            throw new PlaylistException("toIndex is out of bounds");
        }

        List<PlaylistTrack> original =
                new ArrayList<>(SetUtils.emptyIfNull(playList.getPlaylistTracks()));
        Collections.sort(original);

        List<PlaylistTrack> addedTracks = new ArrayList<>(tracksToAdd.size());
        for (Track track : tracksToAdd) {
            PlaylistTrack playlistTrack =
                    PlaylistTrack.builder()
                            .track(track)
                            .playlist(playList)
                            .dateAdded(businessUtils.currentDate())
                            .trackId(track.getId())
                            .build();

            playList.setDuration(addTrackDurationToPlaylist(playList, track));
            original.add(toIndex, playlistTrack);
            addedTracks.add(playlistTrack);
            toIndex++;
        }

        Integer indexInPlaylist = 0;
        for (PlaylistTrack track : original) {
            track.setIndex(indexInPlaylist++);
        }

        /* Setting new HashSet instead of set#clear(), clear() is slower */
        playList.setPlaylistTracks(new HashSet<>(original));
        playList.setNrOfTracks(original.size());
        return addedTracks;
    }

    /**
     * Remove the tracks from the playlist located at the sent indexes
     *
     * @param uuid identifies the playlist
     * @param indexes indexes of the tracks in the playlist that need to be removed
     * @return the tracks in the playlist after the removal
     * @throws PlaylistException
     */
    public List<PlaylistTrack> removeTracks(String uuid, List<Integer> indexes)
            throws PlaylistException {
        // TODO
        return Collections.EMPTY_LIST;
    }

    private boolean validateIndexes(int toIndex, int length) {
        return toIndex >= 0 && toIndex <= length;
    }

    private float addTrackDurationToPlaylist(Playlist playList, Track track) {
        return (track != null ? track.getDuration() : 0)
                + (playList != null && playList.getDuration() != null ? playList.getDuration() : 0);
    }
}
