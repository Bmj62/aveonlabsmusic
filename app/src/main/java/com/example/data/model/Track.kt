package com.example.data.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val artworkUri: String? = null,
    val mediaUri: String,
    val durationMs: Long = 0L,
    val isLocal: Boolean = false,
    val category: String = "Trending",
    val isFavorite: Boolean = false
) {
    fun getFormattedDuration(): String {
        if (durationMs <= 0) return "--:--"
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
