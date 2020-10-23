package com.tidal.refactoring.playlist;

import com.tidal.refactoring.playlist.data.Playlist;
import com.tidal.refactoring.playlist.data.PlaylistTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import com.tidal.refactoring.playlist.exception.ValidationException;
import com.tidal.refactoring.playlist.interfaces.PlaylistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceImplTest {

    private static final String PLAYLIST_UUID = "ea23cd43ekdo30cm54jxvf93";
    private static final String PLAYLIST_NAME = "The Test Playlist Name";
    private static final LocalDate PLAYLIST_DATE = LocalDate.now();

    private static final LocalDate CURRENT_DATE = LocalDate.now();

    private static final Integer TO_INDEX = 1;
    private static final Integer TRACK_ID = 1;
    private static final String TRACK_TITLE = "Track 1";
    private static final Integer ARTIST_ID = 1;

    @InjectMocks private PlaylistServiceImpl playlistServiceImpl;

    @Mock private PlaylistRepository playlistRepository;

    /* Verifying non-empty validation constraint, UUID has to be non-empty
     * - There is no point testing for all (null, empty, white-spaces) scenarios, since commons' `isBlank()`
     * implicitly does that*/
    @Test
    public void addTracksShouldThrowValidationExceptionWhenUUIDIsEmpty() {
        assertThrows(
                ValidationException.class,
                () -> playlistServiceImpl.addTracks("", Collections.emptyList(), 0));

        Mockito.verify(playlistRepository, Mockito.never()).getPlaylistByUUID(anyString());
    }

    @Test
    public void addTracksShouldThrowValidationExceptionWhenTracksToAddAreEmpty() {
        assertThrows(
                ValidationException.class,
                () -> playlistServiceImpl.addTracks(PLAYLIST_UUID, Collections.emptyList(), 0));

        Mockito.verify(playlistRepository, Mockito.never()).getPlaylistByUUID(anyString());
    }

    @Test
    public void addTracksShouldThrowPlaylistExceptionWhenPlaylistByUUIDNotFound() {
        List<Track> trackList =
                Collections.singletonList(
                        Track.builder()
                                .id(TRACK_ID)
                                .title(TRACK_TITLE)
                                .duration(60.00f)
                                .artistId(ARTIST_ID)
                                .build());

        Mockito.when(playlistRepository.getPlaylistByUUID(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                PlaylistException.class,
                () -> playlistServiceImpl.addTracks(PLAYLIST_UUID, trackList, TO_INDEX));
    }

    @Test
    public void addTracksShouldThrowExceptionWhenPlaylistExceeds500Tracks() {
        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .playlistTracks(new HashSet<>())
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(500)
                        .deleted(false)
                        .duration(3000.00f)
                        .build();

        List<Track> trackList =
                Collections.singletonList(
                        Track.builder()
                                .id(TRACK_ID)
                                .title(TRACK_TITLE)
                                .duration(60.00f)
                                .artistId(ARTIST_ID)
                                .build());

        Mockito.when(playlistRepository.getPlaylistByUUID(PLAYLIST_UUID))
                .thenReturn(Optional.of(playlist));

        assertThrows(
                PlaylistException.class,
                () -> playlistServiceImpl.addTracks(PLAYLIST_UUID, trackList, TO_INDEX));

        Mockito.verify(playlistRepository, Mockito.times(1)).getPlaylistByUUID(PLAYLIST_UUID);
    }

    @Test
    public void addTracksAddsTrackToThePlaylist() {
        Track track = Track.builder().id(76868).title("A brand new track").artistId(4).build();
        List<Track> trackList = Collections.singletonList(track);

        Mockito.when(playlistRepository.getPlaylistByUUID(anyString()))
                .thenReturn(Optional.of(Playlist.builder().build()));

        List<PlaylistTrack> playlistTracks =
                playlistServiceImpl.addTracks(UUID.randomUUID().toString(), trackList, 5);

        assertTrue(playlistTracks.size() > 0);
    }

    /*
    Verify: When Index is -1, track is added at the end of playlist
     */
    @Test
    public void addTracksPutsTrackAtPlaylistEndWhenIndexIsMinusOne() {
        Integer MINUS_ONE_TO_INDEX = -1;
        Integer PLAYLIST_TRACK_ID_ONE = 10;
        Integer PLAYLIST_TRACK_ID_TWO = 11;
        Integer TRACK_ID_ONE = 1;
        Integer TRACK_ID_TWO = 2;

        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(2)
                        .deleted(false)
                        .duration(120.00f)
                        .build();

        Set<PlaylistTrack> playlistTracks =
                new HashSet<>(
                        Arrays.asList(
                                PlaylistTrack.builder()
                                        .id(PLAYLIST_TRACK_ID_ONE)
                                        .trackId(TRACK_ID_ONE)
                                        .dateAdded(CURRENT_DATE)
                                        .track(Track.builder().id(TRACK_ID_ONE).build())
                                        .playlist(playlist)
                                        .build(),
                                PlaylistTrack.builder()
                                        .id(PLAYLIST_TRACK_ID_TWO)
                                        .trackId(TRACK_ID_TWO)
                                        .dateAdded(CURRENT_DATE)
                                        .track(Track.builder().id(TRACK_ID_TWO).build())
                                        .playlist(playlist)
                                        .build()));

        playlist.setPlaylistTracks(playlistTracks);

        List<Track> addList =
                Collections.singletonList(
                        Track.builder()
                                .id(TRACK_ID)
                                .title(TRACK_TITLE)
                                .duration(60.00f)
                                .artistId(ARTIST_ID)
                                .build());

        /* Configure mocks */
        Mockito.when(playlistRepository.getPlaylistByUUID(PLAYLIST_UUID))
                .thenReturn(Optional.of(playlist));

        /* Actual calls */
        List<PlaylistTrack> addedTracks =
                playlistServiceImpl.addTracks(PLAYLIST_UUID, addList, MINUS_ONE_TO_INDEX);

        /* Assertions */
        assertTrue(playlist.getNrOfTracks() - 1 == addedTracks.get(0).getIndex());
        Mockito.verify(playlistRepository, Mockito.times(1)).getPlaylistByUUID(PLAYLIST_UUID);
    }

    /*
    Verify: When Index is greater than playlist size , track is added at the end of playlist
     */
    @Test
    public void addTracksPutsTrackAtPlaylistEndWhenIndexGreaterThanPlaylistSize() {
        Integer HIGH_TO_INDEX = 99;
        Integer PLAYLIST_TRACK_ID_ONE = 10;
        Integer PLAYLIST_TRACK_ID_TWO = 11;
        Integer TRACK_ID_ONE = 1;
        Integer TRACK_ID_TWO = 2;

        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(2)
                        .deleted(false)
                        .duration(120.00f)
                        .build();

        Set<PlaylistTrack> playlistTracks =
                new HashSet<>(
                        Arrays.asList(
                                PlaylistTrack.builder()
                                        .id(PLAYLIST_TRACK_ID_ONE)
                                        .trackId(TRACK_ID_ONE)
                                        .dateAdded(CURRENT_DATE)
                                        .track(Track.builder().id(TRACK_ID_ONE).build())
                                        .playlist(playlist)
                                        .build(),
                                PlaylistTrack.builder()
                                        .id(PLAYLIST_TRACK_ID_TWO)
                                        .trackId(TRACK_ID_TWO)
                                        .dateAdded(CURRENT_DATE)
                                        .track(Track.builder().id(TRACK_ID_TWO).build())
                                        .playlist(playlist)
                                        .build()));

        playlist.setPlaylistTracks(playlistTracks);

        List<Track> addList =
                Collections.singletonList(
                        Track.builder()
                                .id(TRACK_ID)
                                .title(TRACK_TITLE)
                                .duration(60.00f)
                                .artistId(ARTIST_ID)
                                .build());

        /* Configure mocks */
        Mockito.when(playlistRepository.getPlaylistByUUID(PLAYLIST_UUID))
                .thenReturn(Optional.of(playlist));

        /* Actual calls */
        List<PlaylistTrack> addedTracks =
                playlistServiceImpl.addTracks(PLAYLIST_UUID, addList, HIGH_TO_INDEX);

        /* Assertions */
        assertTrue(playlist.getNrOfTracks() - 1 == addedTracks.get(0).getIndex());
        Mockito.verify(playlistRepository, Mockito.times(1)).getPlaylistByUUID(PLAYLIST_UUID);
    }

    /*
    Verify: When Index is out of bounds , throws playlist exception
     */
    @Test
    public void addTracksThrowsPlaylistExceptionWhenIndexIsOutOfBounds() {
        Integer OUT_OF_BOUNDS_INDEX = -12;

        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .playlistTrack(PlaylistTrack.builder().build())
                        .playlistTrack(PlaylistTrack.builder().build())
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(2)
                        .deleted(false)
                        .duration(120.00f)
                        .build();

        List<Track> addList =
                Collections.singletonList(
                        Track.builder()
                                .id(TRACK_ID)
                                .title(TRACK_TITLE)
                                .duration(60.00f)
                                .artistId(ARTIST_ID)
                                .build());

        /* Configure mocks */
        Mockito.when(playlistRepository.getPlaylistByUUID(PLAYLIST_UUID))
                .thenReturn(Optional.of(playlist));

        /* Assertions */
        assertThrows(
                PlaylistException.class,
                /* Actual Call */
                () -> playlistServiceImpl.addTracks(PLAYLIST_UUID, addList, OUT_OF_BOUNDS_INDEX));

        Mockito.verify(playlistRepository, Mockito.times(1)).getPlaylistByUUID(PLAYLIST_UUID);
    }

    /*
    Verify: When track added to playlist, playlist duration is updated respectively
     */
    @Test
    public void addTracksUpdatesPlaylistDurationWhenNewTrackAdded() {
        Integer ADD_TRACK_TO_INDEX = 1;
        Integer TRACK_ID_ONE = 20;
        Integer TRACK_ID_TWO = 21;
        Float TRACK_DURATION_ONE = 60.00f;
        Float TRACK_DURATION_TWO = 120.00f;

        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(1)
                        .deleted(false)
                        .duration(TRACK_DURATION_ONE)
                        .build();

        Set<PlaylistTrack> playlistTracks =
                new HashSet<>(
                        Arrays.asList(
                                PlaylistTrack.builder()
                                        .id(10)
                                        .trackId(TRACK_ID_ONE)
                                        .dateAdded(CURRENT_DATE)
                                        .track(
                                                Track.builder()
                                                        .id(TRACK_ID_ONE)
                                                        .duration(TRACK_DURATION_ONE)
                                                        .build())
                                        .playlist(playlist)
                                        .build()));

        playlist.setPlaylistTracks(playlistTracks);

        List<Track> addList =
                Collections.singletonList(
                        Track.builder()
                                .id(TRACK_ID_TWO)
                                .duration(TRACK_DURATION_TWO)
                                .title(TRACK_TITLE)
                                .artistId(ARTIST_ID)
                                .build());

        /* Configure mocks */
        Mockito.when(playlistRepository.getPlaylistByUUID(PLAYLIST_UUID))
                .thenReturn(Optional.of(playlist));

        /* Actual calls */
        playlistServiceImpl.addTracks(PLAYLIST_UUID, addList, ADD_TRACK_TO_INDEX);

        /* Assertions */
        assertTrue(playlist.getDuration().equals(TRACK_DURATION_ONE + TRACK_DURATION_TWO));
        Mockito.verify(playlistRepository, Mockito.times(1)).getPlaylistByUUID(PLAYLIST_UUID);
    }

    /*
    Verify: When index is somewhere in middle of playlist tracks list , track is added at that middle index
     */
    @Test
    public void addTracksAddsTrackAtSpecifiedIndexWhenToIndexIsAnInBetweenIndex() {
        Integer ADD_TO_INDEX = 1;
        Integer TRACK_ID_ONE = 1;
        Integer TRACK_ID_TWO = 2;

        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(2)
                        .deleted(false)
                        .duration(120.00f)
                        .build();

        Set<PlaylistTrack> playlistTracks =
                new HashSet<>(
                        Arrays.asList(
                                PlaylistTrack.builder()
                                        .index(0)
                                        .id(10)
                                        .trackId(TRACK_ID_ONE)
                                        .dateAdded(CURRENT_DATE)
                                        .track(
                                                Track.builder()
                                                        .id(TRACK_ID_ONE)
                                                        .duration(60.00f)
                                                        .build())
                                        .playlist(playlist)
                                        .build(),
                                PlaylistTrack.builder()
                                        .index(1)
                                        .id(11)
                                        .trackId(TRACK_ID_TWO)
                                        .dateAdded(CURRENT_DATE)
                                        .track(
                                                Track.builder()
                                                        .id(TRACK_ID_TWO)
                                                        .duration(110.00f)
                                                        .build())
                                        .playlist(playlist)
                                        .build()));

        playlist.setPlaylistTracks(playlistTracks);

        List<Track> addList =
                Collections.singletonList(
                        Track.builder()
                                .id(TRACK_ID)
                                .title(TRACK_TITLE)
                                .duration(60.00f)
                                .artistId(ARTIST_ID)
                                .build());

        /* Configure mocks */
        Mockito.when(playlistRepository.getPlaylistByUUID(PLAYLIST_UUID))
                .thenReturn(Optional.of(playlist));

        /* Actual calls */
        List<PlaylistTrack> addedTracks =
                playlistServiceImpl.addTracks(PLAYLIST_UUID, addList, ADD_TO_INDEX);

        /* Assertions */
        assertTrue(ADD_TO_INDEX == addedTracks.get(0).getIndex());
        Mockito.verify(playlistRepository, Mockito.times(1)).getPlaylistByUUID(PLAYLIST_UUID);
    }

    /* ---------------------------- RemoveTracks Unit Tests --------------------------------*/
    /* Verifying non-empty validation constraint, UUID has to be non-empty
     * - There is no point testing for all (null, empty, white-spaces) scenarios, since commons' `isBlank()`
     * implicitly does that*/
    @Test
    public void removeTracksShouldThrowValidationExceptionWhenUUIDIsEmpty() {
        assertThrows(
                ValidationException.class,
                () -> playlistServiceImpl.removeTracks("", Collections.singletonList(1)));

        Mockito.verify(playlistRepository, Mockito.never()).getPlaylistByUUID(anyString());
    }

    @Test
    public void removeTracksShouldThrowValidationExceptionWhenIndexesAreEmpty() {
        assertThrows(
                ValidationException.class,
                () -> playlistServiceImpl.removeTracks(PLAYLIST_UUID, Collections.emptyList()));

        Mockito.verify(playlistRepository, Mockito.never()).getPlaylistByUUID(anyString());
    }

    @Test
    public void removeTracksShouldThrowPlaylistExceptionWhenPlaylistByUUIDNotFound() {
        Mockito.when(playlistRepository.getPlaylistByUUID(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                PlaylistException.class,
                () ->
                        playlistServiceImpl.removeTracks(
                                PLAYLIST_UUID, Collections.singletonList(1)));
    }

    @Test
    public void removeTracksShouldThrowPlaylistExceptionWhenIndexIsOutOfBounds() {
        Mockito.when(playlistRepository.getPlaylistByUUID(anyString()))
                .thenReturn(Optional.of(Playlist.builder().build()));

        assertThrows(
                PlaylistException.class,
                () ->
                        playlistServiceImpl.removeTracks(
                                PLAYLIST_UUID, Collections.singletonList(-1)));
    }

    @Test
    public void removeTracksRemovesTrackFromSpecifiedPlaylistIndexWhenIndexIsAnInBetweenIndex() {
        List<Integer> REMOVAL_INDEX = Collections.singletonList(1);
        int TRACK_ID_ONE = 1;
        int EXPECTED_TRACK_TO_REMOVE = 2;
        int TRACK_ID_THREE = 3;

        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(3)
                        .build();

        Set<PlaylistTrack> playlistTracks =
                new HashSet<>(
                        Arrays.asList(
                                PlaylistTrack.builder()
                                        .index(0)
                                        .id(1)
                                        .trackId(TRACK_ID_ONE)
                                        .dateAdded(CURRENT_DATE)
                                        .track(Track.builder().id(TRACK_ID_ONE).build())
                                        .playlist(playlist)
                                        .build(),
                                PlaylistTrack.builder()
                                        .index(1)
                                        .id(2)
                                        .trackId(EXPECTED_TRACK_TO_REMOVE)
                                        .dateAdded(CURRENT_DATE)
                                        .track(Track.builder().id(EXPECTED_TRACK_TO_REMOVE).build())
                                        .playlist(playlist)
                                        .build(),
                                PlaylistTrack.builder()
                                        .index(2)
                                        .id(3)
                                        .trackId(TRACK_ID_THREE)
                                        .dateAdded(CURRENT_DATE)
                                        .track(Track.builder().id(TRACK_ID_THREE).build())
                                        .playlist(playlist)
                                        .build()));

        playlist.setPlaylistTracks(playlistTracks);

        /* Configure mocks */
        Mockito.when(playlistRepository.getPlaylistByUUID(anyString()))
                .thenReturn(Optional.of(playlist));

        /* Action */
        List<PlaylistTrack> result = playlistServiceImpl.removeTracks(PLAYLIST_UUID, REMOVAL_INDEX);

        /* Assertions */
        assertTrue(result.size() == 2);
        assertFalse(
                result.stream()
                        .anyMatch(
                                playlistTrack ->
                                        playlistTrack.getId().equals(EXPECTED_TRACK_TO_REMOVE)));
    }

    @Test
    public void removeTracksUpdatesPlaylistDurationWhenTrackIsRemoved() {
        Integer TRACK_REMOVE_INDEX = 1;
        Integer TRACK_ID_ONE = 1;
        Integer TRACK_ID_TWO = 2;
        Float TRACK_DURATION_ONE = 60.00f;
        Float TRACK_DURATION_TWO = 120.00f;

        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(1)
                        .deleted(false)
                        .duration(TRACK_DURATION_ONE)
                        .build();

        Set<PlaylistTrack> playlistTracks =
                new HashSet<>(
                        Arrays.asList(
                                PlaylistTrack.builder()
                                        .id(1)
                                        .trackId(TRACK_ID_ONE)
                                        .track(
                                                Track.builder()
                                                        .id(TRACK_ID_ONE)
                                                        .duration(TRACK_DURATION_ONE)
                                                        .build())
                                        .playlist(playlist)
                                        .build(),
                                PlaylistTrack.builder()
                                        .id(2)
                                        .trackId(TRACK_ID_TWO)
                                        .track(
                                                Track.builder()
                                                        .id(TRACK_ID_TWO)
                                                        .duration(TRACK_DURATION_TWO)
                                                        .build())
                                        .playlist(playlist)
                                        .build()));

        playlist.setPlaylistTracks(playlistTracks);

        /* Configure mocks */
        Mockito.when(playlistRepository.getPlaylistByUUID(PLAYLIST_UUID))
                .thenReturn(Optional.of(playlist));

        /* Actual calls */
        playlistServiceImpl.removeTracks(
                PLAYLIST_UUID, Collections.singletonList(TRACK_REMOVE_INDEX));

        /* Assertions */
        Float expectedDuration = TRACK_DURATION_ONE;
        Float actualDuration = playlist.getDuration();

        assertTrue(expectedDuration.equals(actualDuration));
        Mockito.verify(playlistRepository, Mockito.times(1)).getPlaylistByUUID(PLAYLIST_UUID);
    }

    @Test
    public void removeTracksReIndexesTracksWhenATrackIsRemoved() {
        int TRACK_ID_ONE = 1;
        int TRACK_ID_TWO = 2;
        int TRACK_ID_THREE = 3;

        int REMOVAL_INDEX = 2;

        Playlist playlist =
                Playlist.builder()
                        .id(1)
                        .uuid(PLAYLIST_UUID)
                        .playListName(PLAYLIST_NAME)
                        .registeredDate(PLAYLIST_DATE)
                        .lastUpdated(PLAYLIST_DATE)
                        .nrOfTracks(3)
                        .build();

        Set<PlaylistTrack> playlistTracks =
                new HashSet<>(
                        Arrays.asList(
                                PlaylistTrack.builder()
                                        .index(0)
                                        .id(1)
                                        .trackId(TRACK_ID_ONE)
                                        .track(Track.builder().id(TRACK_ID_ONE).build())
                                        .playlist(playlist)
                                        .build(),
                                PlaylistTrack.builder()
                                        .index(1)
                                        .id(2)
                                        .trackId(TRACK_ID_TWO)
                                        .track(Track.builder().id(TRACK_ID_TWO).build())
                                        .playlist(playlist)
                                        .build(),
                                PlaylistTrack.builder()
                                        .index(2)
                                        .id(3)
                                        .trackId(TRACK_ID_THREE)
                                        .track(Track.builder().id(TRACK_ID_THREE).build())
                                        .playlist(playlist)
                                        .build()));

        playlist.setPlaylistTracks(playlistTracks);

        /* Configure mocks */
        Mockito.when(playlistRepository.getPlaylistByUUID(anyString()))
                .thenReturn(Optional.of(playlist));

        /* Action */
        List<PlaylistTrack> result =
                playlistServiceImpl.removeTracks(
                        PLAYLIST_UUID, Collections.singletonList(REMOVAL_INDEX));

        /* Assertions */
        int expectedReassignedIndex = 1;
        int actualReassignedIndex = result.get(1).getIndex();
        assertTrue(expectedReassignedIndex == actualReassignedIndex);
    }
}
