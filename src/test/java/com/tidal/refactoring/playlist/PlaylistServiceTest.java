package com.tidal.refactoring.playlist;

import com.tidal.refactoring.playlist.data.Playlist;
import com.tidal.refactoring.playlist.data.PlaylistTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.interfaces.PlaylistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {

    @InjectMocks
    private PlaylistService playlistService;

    @Mock
    private PlaylistRepository playlistRepository;

    @Test
    public void testAddTracks() {
        List<Track> trackList = new ArrayList<Track>();

        Track track = new Track();
        track.setArtistId(4);
        track.setTitle("A brand new track");
        track.setId(76868);

        trackList.add(track);

        Mockito.when(playlistRepository.getPlaylistByUUID(anyString())).thenReturn(Optional.of(new Playlist()));

        List<PlaylistTrack> playlistTracks = playlistService.addTracks(UUID.randomUUID().toString(), trackList, 5);

        assertTrue(playlistTracks.size() > 0);
    }
}
