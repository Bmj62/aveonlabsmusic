**Tech Stack:**

Language: Kotlin

UI Framework: Jetpack Compose (Material3 theme)

Architecture: MVVM (Model-View-ViewModel) with Clean Architecture principles

State Management: StateFlow and collectAsStateWithLifecycle

Media Playback: Jetpack Media3 (ExoPlayer & MediaSessionService)

Local Storage: Android ContentResolver / MediaStore API

Backend: Firebase (Authentication, Firestore, Firebase Storage)

Async Operations: Kotlin Coroutines & Flow

Image Loading: Coil for Compose

Navigation: Jetpack Navigation Compose

**Core Features to Implement:**

Firebase Integration & Auth:

Firebase Authentication setup (Email/Password login and signup screen).

Firestore integration to fetch cloud-hosted playlists, trending tracks, and track metadata (title, artist, cover art URL, streaming URL).

Local Device Audio Scanning:

Request runtime permissions for local media reading (READ_MEDIA_AUDIO for Android 13+ / READ_EXTERNAL_STORAGE for older versions).

Query MediaStore.Audio.Media.EXTERNAL_CONTENT_URI to automatically scan, parse, and list local .mp3/.m4a files stored on the user's phone.

Provide a unified repository or tab toggle to switch between "Cloud Tracks (Firebase)" and "Local Device Storage."

Media Playback Engine:

Create a background MediaSessionService using Media3 to support background audio, notification controls, and lock screen media controls.

Seamlessly play both local content:// URIs and Firebase remote HTTPS media URLs.

Full playback controls: play, pause, next, previous, shuffle, repeat, and seek slider position updates.

UI Screens & Components:

Login / Register Screen: Simple Auth UI backed by Firebase Auth.

Home Screen: Tabbed view showing Firebase featured playlists/songs and a dedicated "Local Library" section.

Mini Player (Bottom Bar): Persistent bar displaying track artwork, title, artist, and play/pause button.

Full-Screen Player: Expandable view with large album art, track details, scrubbable progress bar, and full playback controls.

**Output Requirements:**

Provide full, runnable code organized logically by file:

build.gradle.kts (Project & App level with Firebase, Media3, Coil, Navigation dependencies).

AndroidManifest.xml (Permissions & Service declarations).

Auth UI & ViewModel (LoginScreen.kt, AuthViewModel.kt).

MediaStore Scanner (LocalAudioScanner.kt).

Firebase Repository (FirebaseAudioRepository.kt).

Media3 Service (PlaybackService.kt).

Main UI Views (HomeScreen.kt, PlayerScreen.kt, MiniPlayer.kt, PlayerViewModel.kt).

Include clear instructions on setting up the google-services.json file and Firestore collection schema.
