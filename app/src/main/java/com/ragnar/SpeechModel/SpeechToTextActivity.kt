package com.ragnar.SpeechModel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class SpeechToTextActivity : AppCompatActivity() {

    private lateinit var micButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var speechRecognizer: SpeechRecognizer

    // MODIFIED: Add a handler for the cooldown
    private val handler = Handler(Looper.getMainLooper())

    private var isListening = false
    private val RECORD_AUDIO_PERMISSION_REQUEST = 101
    private val TAG = "SpeechToTextActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_to_text)

        micButton = findViewById(R.id.btnMicrophone)
        resultTextView = findViewById(R.id.textViewResult)
        statusTextView = findViewById(R.id.textViewStatus)

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Speech recognition not available on this device.")
            micButton.isEnabled = false // MODIFIED: Disable button if not available
            finish()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(speechRecognitionListener)

        micButton.setOnClickListener {
            if (checkAudioPermission()) {
                toggleSpeechRecognition()
            } else {
                requestAudioPermission()
            }
        }

        supportActionBar?.title = "Speech to Text"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_REQUEST
        )
    }

    private fun toggleSpeechRecognition() {
        if (isListening) {
            stopListening()
        } else {
            startListening()
        }
    }

    private fun startListening() {
        resultTextView.text = ""
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        speechRecognizer.startListening(intent)
        isListening = true
        micButton.isEnabled = true // Ensure button is enabled
        micButton.text = "⏹"
        micButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        statusTextView.text = "Listening... Speak now"
        Log.e(TAG, "Speech recognition started.")
    }

    private fun stopListening() {
        speechRecognizer.stopListening()
    }

    private fun resetUiAfterRecognition(isError: Boolean) {
        if (isListening) { // Prevent this from being called multiple times
            isListening = false
            micButton.isEnabled = false // Disable button during cooldown
            handler.postDelayed({
                micButton.isEnabled = true // Re-enable after 1 second
            }, 1000)
        }
        micButton.text = "🎤"
        micButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary))
        if (!isError) {
            statusTextView.text = "Tap microphone to start"
        }
    }

    private val speechRecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            statusTextView.text = "Ready for speech..."
            Log.e(TAG, "onReadyForSpeech: Ready")
        }

        override fun onBeginningOfSpeech() {
            statusTextView.text = "Speech detected..."
            Log.e(TAG, "onBeginningOfSpeech: Speech started")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {
            Log.e(TAG, "onBufferReceived: Audio buffer received")
        }

        override fun onEndOfSpeech() {
            statusTextView.text = "Processing speech..."
            Log.e(TAG, "onEndOfSpeech: Speech ended")
            resetUiAfterRecognition(isError = false)
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error (Check Google App)"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech match found"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            statusTextView.text = "Error: $errorMessage"
            Log.e(TAG, "onError: ($error) $errorMessage")
            resetUiAfterRecognition(isError = true)
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val recognizedText = matches[0]
                resultTextView.text = recognizedText
                statusTextView.text = "Speech recognized successfully!"
                Log.e(TAG, "onResults: Final result - '$recognizedText'")
            } else {
                Log.e(TAG, "onResults: No results found.")
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val partialText = matches[0]
                resultTextView.text = partialText
                Log.e(TAG, "onPartialResults: Partial result - '$partialText'")
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Log.e(TAG, "onEvent: Event type - $eventType")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleSpeechRecognition()
            } else {
                Toast.makeText(this, "Audio permission is required for speech recognition", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Audio permission denied by user.")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
            Log.e(TAG, "Speech recognizer destroyed.")
        }
    }
}