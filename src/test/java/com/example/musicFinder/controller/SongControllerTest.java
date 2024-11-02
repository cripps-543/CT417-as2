package com.example.musicFinder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.musicFinder.MusicFinderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class SongControllerTest {

    @InjectMocks
    private MusicFinderController musicFinderController;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testFetchLyrics_ValidSong() {
        // Arrange
        String artist = "Adele";
        String song = "Hello";
        String expectedLyricsSnippet = "Hello, it's me<br>I was wondering"; // Use actual expected lyrics

        String jsonResponse = "{\"lyrics\":\"Hello, it's me\\nI was wondering...\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);

        // Act
        ObjectNode response = musicFinderController.getSongDetails(artist, song);

        // Assert
        assertEquals(artist, response.get("artist").asText());
        assertEquals(song, response.get("song").asText());
        assertTrue(response.get("lyrics").asText().contains(expectedLyricsSnippet));
        assertEquals("https://www.youtube.com/results?search_query=Adele+Hello", response.get("youtubeSearch").asText());
    }

    @Test
    public void testFetchLyrics_InvalidSong() {
        // Arrange
        String artist = "Unknown Artist";
        String song = "Unknown Song";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new RuntimeException("Lyrics not found"));

        // Act
        ObjectNode response = musicFinderController.getSongDetails(artist, song);

        // Assert
        assertEquals(artist, response.get("artist").asText());
        assertEquals(song, response.get("song").asText());
        assertEquals("{\"error\":\"Lyrics not found\"}", response.get("lyrics").asText());
    }

    @Test
    public void testFetchLyrics_NullArtist() {
        // Arrange
        String artist = null;
        String song = "Hello";

        // Act
        ObjectNode response = musicFinderController.getSongDetails(artist, song);

        // Assert
        assertEquals("Artist name cannot be null or empty", response.get("error").asText());
    }

    @Test
    public void testFetchLyrics_NullSong() {
        // Arrange
        String artist = "Adele";
        String song = null;

        // Act
        ObjectNode response = musicFinderController.getSongDetails(artist, song);

        // Assert
        assertEquals("Song name cannot be null or empty", response.get("error").asText());
    }

    @Test
    public void testFetchLyrics_EmptyArtist() {
        // Arrange
        String artist = "";
        String song = "Hello";

        // Act
        ObjectNode response = musicFinderController.getSongDetails(artist, song);

        // Assert
        assertEquals("Artist name cannot be null or empty", response.get("error").asText());
    }

    @Test
    public void testFetchLyrics_EmptySong() {
        // Arrange
        String artist = "Adele";
        String song = "";

        // Act
        ObjectNode response = musicFinderController.getSongDetails(artist, song);

        // Assert
        assertEquals("Song name cannot be null or empty", response.get("error").asText());
    }

}
