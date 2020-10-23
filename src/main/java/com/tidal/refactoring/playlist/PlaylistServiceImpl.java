package com.tidal.refactoring.playlist;

import com.tidal.refactoring.playlist.data.Playlist;
import com.tidal.refactoring.playlist.data.PlaylistTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import com.tidal.refactoring.playlist.exception.ValidationException;
import com.tidal.refactoring.playlist.interfaces.PlaylistRepository;
import com.tidal.refactoring.playlist.interfaces.PlaylistService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PlaylistServiceImpl implements PlaylistService {
    private static final Integer PLAYLIST_MAX_SIZE = 500;

    private PlaylistRepository playlistRepository;

    private BusinessUtils businessUtils;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
        this.businessUtils = new BusinessUtils();
    }

    /** @see PlaylistService#addTracks(String, List, int) */
    public List<PlaylistTrack> addTracks(String uuid, List<Track> tracksToAdd, int insertionIndex)
            throws PlaylistException {

        /* Validations */
        if (StringUtils.isBlank(uuid))
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

        /* The index is higher than size, put it in the end of the list */
        int size = playList.getNrOfTracks();
        if (insertionIndex > size || insertionIndex == -1) {
            insertionIndex = size;
        }

        if (!isValidIndex(insertionIndex, playList.getNrOfTracks())) {
            throw new PlaylistException("insertionIndex " + insertionIndex + "is out of bounds");
        }

        /* Sorted list of tracks */
        List<PlaylistTrack> original =
                new ArrayList<>(SetUtils.emptyIfNull(playList.getPlaylistTracks()));
        Collections.sort(original);

        /* Add tracks to the playlist tracks based on index */
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
            original.add(insertionIndex, playlistTrack);
            addedTracks.add(playlistTrack);
            insertionIndex++;
        }

        /* Reassign indices to the tracks in playlist */
        int indexInPlaylist = 0;
        for (PlaylistTrack track : original) {
            track.setIndex(indexInPlaylist++);
        }

        /* Setting new HashSet instead of set#clear(), clear() is slower */
        playList.setPlaylistTracks(new HashSet<>(original));
        playList.setNrOfTracks(original.size());
        return addedTracks;
    }

    /** @see PlaylistService#removeTracks(String, List) */
    public List<PlaylistTrack> removeTracks(String uuid, List<Integer> indexes)
            throws PlaylistException {

        /* Validations */
        if (StringUtils.isBlank(uuid))
            throw new ValidationException("uuid must not be null or empty");

        if (CollectionUtils.isEmpty(indexes))
            throw new ValidationException("indexes must not be null or empty");

        Playlist playList =
                playlistRepository
                        .getPlaylistByUUID(uuid)
                        .orElseThrow(() -> new PlaylistException("Playlist not found"));

        /* Validate indices */
        indexes.forEach(
                index -> {
                    if (!isValidIndex(index, playList.getNrOfTracks())) {
                        throw new PlaylistException("Index: " + index + "out of bounds");
                    }
                });

        Set<PlaylistTrack> tracksOfPlaylist = SetUtils.emptyIfNull(playList.getPlaylistTracks());

        /* Gather all tracks to be removed. Time complexity O(N * W) */
        Set<PlaylistTrack> tracksToRemove =
                playList.getPlaylistTracks().stream()
                        .filter(playlistTrack -> indexes.contains(playlistTrack.getIndex()))
                        .collect(Collectors.toSet());

        tracksToRemove.forEach(
                removableTrack -> {
                    /* O(1) operations since its HashSet */
                    tracksOfPlaylist.remove(removableTrack);
                    /* Remove track duration from overall playlist duration */
                    playList.setDuration(
                            removeTrackDurationFromPlaylist(playList, removableTrack.getTrack()));
                });

        /* sorting the tracks of playlist. Overall nlog(n) complexity */
        Set<PlaylistTrack> sortedTracks = new TreeSet<>(tracksOfPlaylist);

        /* Re-assign indices: O(n) */
        Integer plIndex = 0;
        for (PlaylistTrack plTrack : sortedTracks) {
            plTrack.setIndex(plIndex++);
        }

        /* In fairly large sets, assigning new set is faster than clearing and adding into existing */
        playList.setPlaylistTracks(new HashSet<>(sortedTracks));
        playList.setNrOfTracks(tracksOfPlaylist.size());

        /* Suggestion: Method return type should be changed from List to Set */
        return new ArrayList<>(sortedTracks);
    }

    private boolean isValidIndex(int toIndex, int length) {
        return toIndex >= 0 && toIndex <= length;
    }

    private float addTrackDurationToPlaylist(Playlist playList, Track track) {
        return (track != null ? track.getDuration() : 0)
                + (playList != null && playList.getDuration() != null ? playList.getDuration() : 0);
    }

    private float removeTrackDurationFromPlaylist(Playlist playList, Track track) {
        return (playList != null && playList.getDuration() != null ? playList.getDuration() : 0)
                - (track != null ? track.getDuration() : 0);
    }
}
