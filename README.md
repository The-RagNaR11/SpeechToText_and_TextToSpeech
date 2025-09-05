# Speech Model Android App

A complete Android application that demonstrates Text-to-Speech (TTS) and Speech-to-Text (STT) functionality using Android's native APIs.

## Features

### 🔊 Text-to-Speech Activity
- Enter text in an input field
- Convert text to speech using Android's built-in TTS engine
- Clean and intuitive user interface
- Real-time speech synthesis

### 🎤 Speech-to-Text Activity
- Voice recognition using Android's SpeechRecognizer
- Real-time speech-to-text conversion
- Visual feedback during recording
- Permission handling for microphone access

## Technical Specifications

- **Language**: Kotlin
- **Layouts**: XML
- **Speech Recognition**: Android's built-in SpeechRecognizer API
- **Text-to-Speech**: Android's built-in TextToSpeech API
- **Build System**: Gradle (KTS format)
- **Package Name**: `com.ragnar.SpeechModel`
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35

## Internet Requirement
- **Speech-to-Text (STT):** `Yes (Primarily)`	Relies on powerful Google servers for high-accuracy transcription. Offline mode is a less accurate fallback.
- **Text-to-Speech (TTS):** `No (Primarily)`	Uses engines and voice data installed on the device. Internet is only for downloading/updating voices.
## Project Structure

```
app/
├── src/main/
│   ├── java/com/ragnar/SpeechModel/
│   │   ├── MainActivity.kt
│   │   ├── TextToSpeechActivity.kt
│   │   └── SpeechToTextActivity.kt
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── activity_text_to_speech.xml
│   │   │   └── activity_speech_to_text.xml
│   │   ├── values/
│   │   │   └── strings.xml
│   └── AndroidManifest.xml
├── build.gradle.kts
└── README.md
```

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK with minimum API level 24
- Device or emulator with microphone support (for speech-to-text)

### Installation Steps

1. **Clone or Download** the project files
2. **Open Android Studio** and select "Open an existing project"
3. **Navigate** to the project directory and open it
4. **Sync** the project with Gradle files
5. **Build** the project (Build → Make Project)
6. **Run** the application on your device or emulator

### File Setup

Create the following directory structure in your Android Studio project:

#### 1. Kotlin Files
Place in `app/src/main/java/com/ragnar/SpeechModel/`:
- `MainActivity.kt`
- `TextToSpeechActivity.kt`
- `SpeechToTextActivity.kt`

#### 2. Layout Files
Place in `app/src/main/res/layout/`:
- `activity_main.xml`
- `activity_text_to_speech.xml`
- `activity_speech_to_text.xml`

#### 3. Drawable Resources
Create `app/src/main/res/drawable/` directory and add:
- `rounded_button.xml`
- `rounded_circle_button.xml`
- `rounded_edittext.xml`
- `rounded_textview.xml`

#### 4. Values
Place in `app/src/main/res/values/`:
- `strings.xml`

#### 5. Root Files
- `build.gradle.kts` (Module: app)
- `AndroidManifest.xml`

## Permissions

The app requires the following permissions:

- **RECORD_AUDIO**: Required for speech recognition functionality
- **INTERNET**: Required for enhanced speech recognition (optional)

## Usage

### Text-to-Speech
1. Launch the app
2. Tap "Text to Speech" button
3. Enter text in the input field
4. Tap "🔊 Speak Text" to hear the text spoken aloud

### Speech-to-Text
1. From the main screen, tap "Speech to Text"
2. Grant microphone permission when prompted
3. Tap the microphone button to start listening
4. Speak clearly into the device microphone
5. The recognized text will appear in the result area
6. Tap the microphone button again to stop listening

## Key Features

### 🎨 UI Design
- Clean, modern material design
- Rounded corners and elevation for visual appeal
- Intuitive color coding (blue for TTS, red for STT)
- Responsive layout that works on different screen sizes

### 🔧 Technical Implementation
- **Proper lifecycle management** for TTS and speech recognizer
- **Runtime permission handling** for microphone access
- **Error handling** with user-friendly messages
- **Resource cleanup** to prevent memory leaks
- **Navigation support** with back button functionality

### 🛡️ Error Handling
- Network connectivity issues
- Microphone permission denied
- Speech recognition service unavailable
- TTS initialization failures
- No speech input detection

## Dependencies

```kotlin
// Core Android dependencies
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
```

## Architecture

The app follows a simple activity-based architecture:

- **MainActivity**: Entry point with navigation to TTS and STT features
- **TextToSpeechActivity**: Handles text input and speech synthesis
- **SpeechToTextActivity**: Manages voice recording and text recognition

## Supported Languages

- **Text-to-Speech**: Uses system default language (typically English US)
- **Speech-to-Text**: Uses device default language with automatic detection

## Testing

The app has been designed to work on:
- **Minimum Android Version**: 7.0 (API 24)
- **Target Android Version**: 14 (API 34)
- **Tested Devices**: Emulators and physical devices with microphone support

## Troubleshooting

### Common Issues

1. **Speech Recognition Not Working**
   - Ensure microphone permission is granted
   - Check device has internet connection
   - Verify microphone hardware is functional

2. **Text-to-Speech Not Working**
   - Check if TTS engine is installed on device
   - Verify device volume is not muted
   - Ensure TTS data is downloaded for selected language

3. **App Crashes on Launch**
   - Verify all dependencies are properly synced
   - Check minimum SDK version compatibility
   - Ensure all required files are in correct directories

## Future Enhancements

Potential improvements for future versions:
- Multi-language support selection
- Voice recording playback
- Text file import/export
- Custom TTS voice selection
- Offline speech recognition
- Speech rate and pitch controls

## License

This project is created for educational and demonstration purposes. Feel free to modify and use as needed.

## Contributing

1. Fork the project
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

**Note**: Make sure to test the app on a physical device for the best speech recognition experience, as emulators may have limited microphone support.