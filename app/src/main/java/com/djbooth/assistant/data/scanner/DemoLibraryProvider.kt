package com.djbooth.assistant.data.scanner

import com.djbooth.assistant.data.model.Track

object DemoLibraryProvider {

    fun getDemoTracks(): List<Track> {
        return listOf(
            Track(
                id = "demo_1",
                title = "Starlight Horizon",
                artist = "Artbat & Tale Of Us",
                bpm = 124.0,
                key = "8A",
                durationSeconds = 225, // 03:45
                energyLevel = 7,
                genre = "Melodic Techno",
                album = "Afterlife Vol 5"
            ),
            Track(
                id = "demo_2",
                title = "Cyber Pulse",
                artist = "Stephan Bodzin",
                bpm = 125.0,
                key = "9A",
                durationSeconds = 240, // 04:00
                energyLevel = 9,
                genre = "Techno",
                album = "Booster"
            ),
            Track(
                id = "demo_3",
                title = "Deep Resonance",
                artist = "Adriatique",
                bpm = 123.0,
                key = "7A",
                durationSeconds = 210, // 03:30
                energyLevel = 6,
                genre = "Melodic House",
                album = "Siamese Rays"
            ),
            Track(
                id = "demo_4",
                title = "Solar Flare",
                artist = "CamelPhat & Cristoph",
                bpm = 124.0,
                key = "8B",
                durationSeconds = 250, // 04:10
                energyLevel = 8,
                genre = "Progressive House",
                album = "Breathe Single"
            ),
            Track(
                id = "demo_5",
                title = "Groove Dimension",
                artist = "Fisher & Chris Lake",
                bpm = 126.0,
                key = "11B",
                durationSeconds = 195, // 03:15
                energyLevel = 10,
                genre = "Tech House",
                album = "Catch & Release"
            ),
            Track(
                id = "demo_6",
                title = "Afro Tribe (Ritual Mix)",
                artist = "Keinemusik & Rampa",
                bpm = 122.0,
                key = "8A",
                durationSeconds = 270, // 04:30
                energyLevel = 5,
                genre = "Afro House",
                album = "Klouds EP"
            ),
            Track(
                id = "demo_7",
                title = "Neon Sunset",
                artist = "Solomun",
                bpm = 124.0,
                key = "9B",
                durationSeconds = 230, // 03:50
                energyLevel = 7,
                genre = "Deep House",
                album = "Customer Is King"
            ),
            Track(
                id = "demo_8",
                title = "Hyperdrive",
                artist = "Charlotte de Witte",
                bpm = 132.0,
                key = "4A",
                durationSeconds = 205, // 03:25
                energyLevel = 10,
                genre = "Peak Techno",
                album = "KNTXT 08"
            ),
            Track(
                id = "demo_9",
                title = "Velvet Sunset",
                artist = "Nora En Pure",
                bpm = 122.0,
                key = "7B",
                durationSeconds = 215, // 03:35
                energyLevel = 4,
                genre = "Deep House",
                album = "Purified Tracks"
            ),
            Track(
                id = "demo_10",
                title = "Cosmic Journey",
                artist = "Boris Brejcha",
                bpm = 125.0,
                key = "10A",
                durationSeconds = 260, // 04:20
                energyLevel = 8,
                genre = "High-Tech Minimal",
                album = "Fckng Serious"
            )
        )
    }
}
