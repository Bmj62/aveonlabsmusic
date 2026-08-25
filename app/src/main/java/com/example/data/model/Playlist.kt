package com.example.data.model

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverArtUrl: String,
    val tracks: List<Track> = emptyList(),
    val gradientColorHex: String = "#1DB954"
)
