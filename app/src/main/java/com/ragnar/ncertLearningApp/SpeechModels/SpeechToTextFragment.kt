package com.ragnar.ncertLearningApp.SpeechModels

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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ragnar.ncertLearningApp.R
import java.util.Locale

class SpeechToTextFragment : Fragment() {

    private lateinit var micButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var speechRecognizer: SpeechRecognizer

    // Add a handler for the cooldown
    private val handler = Handler(Looper.getMainLooper())

    private var isListening = false
    private val RECORD_AUDIO_PERMISSION_REQUEST = 101
    private val TAG = "SpeechToTextFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speech_to_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        micButton = view.findViewById(R.id.btnMicrophone)
        resultTextView = view.findViewById(R.id.textViewResult)
        statusTextView = view.findViewById(R.id.textViewStatus)

        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Speech recognition not available", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Speech recognition not available on this device.")
            micButton.isEnabled = false // MODIFIED: Disable button if not available
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(speechRecognitionListener)

        micButton.setOnClickListener {
            if (checkAudioPermission()) {
                toggleSpeechRecognition()
            } else {
                requestAudioPermission()
            }
        }
    }

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
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
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
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
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.google.android.material.R.color.design_default_color_primary))
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
                Toast.makeText(requireContext(), "Audio permission is required for speech recognition", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Audio permission denied by user.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
            Log.e(TAG, "Speech recognizer destroyed.")
        }
    }
}