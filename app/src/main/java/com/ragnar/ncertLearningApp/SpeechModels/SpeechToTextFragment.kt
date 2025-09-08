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
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ragnar.ncertLearningApp.R

/**
 * Fragment for converting speech to text with multiple language support.
 * Allows users to pick a language from a dropdown (Spinner) and
 * recognize speech in that language, displaying the result in the correct script.
 */
class SpeechToTextFragment : Fragment() {

    private lateinit var micButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var languageSpinner: Spinner
    private lateinit var speechRecognizer: SpeechRecognizer

    private val handler = Handler(Looper.getMainLooper())
    private var isListening = false
    private val RECORD_AUDIO_PERMISSION_REQUEST = 101
    private val TAG = "SpeechToTextFragment"

    // Default language is English
    private var selectedLanguageCode = "en-IN"

    // Supported languages (display name → code)
    private val languages = mapOf(
        "English" to "en-IN",
        "हिंदी (Hindi)" to "hi-IN",
        "ಕನ್ನಡ (Kannada)" to "kn-IN",
        "தமிழ் (Tamil)" to "ta-IN",
        "తెలుగు (Telugu)" to "te-IN"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_speech_to_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        micButton = view.findViewById(R.id.btnMicrophone)
        resultTextView = view.findViewById(R.id.textViewResult)
        statusTextView = view.findViewById(R.id.textViewStatus)
        languageSpinner = view.findViewById(R.id.spinnerLanguage)

        // Setup Spinner adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Default selection: English
        val defaultIndex = languages.keys.toList().indexOf("English")
        languageSpinner.setSelection(defaultIndex)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View?, position: Int, id: Long) {
                val selectedLang = parent.getItemAtPosition(position).toString()
                selectedLanguageCode = languages[selectedLang] ?: "en-IN"
                statusTextView.text = "Selected: $selectedLang"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Speech recognition not available", Toast.LENGTH_SHORT).show()
            micButton.isEnabled = false
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
        if (isListening) stopListening() else startListening()
    }

    private fun startListening() {
        resultTextView.text = ""
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguageCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        speechRecognizer.startListening(intent)
        isListening = true
        micButton.text = "⏹"
        micButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
        statusTextView.text = "Listening... Speak now"
    }

    private fun stopListening() {
        speechRecognizer.stopListening()
    }

    private fun resetUiAfterRecognition() {
        if (isListening) {
            isListening = false
            micButton.isEnabled = false
            handler.postDelayed({ micButton.isEnabled = true }, 1000)
        }
        micButton.text = "🎤"
        micButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.google.android.material.R.color.design_default_color_primary))
        statusTextView.text = "Tap microphone to start"
    }

    private val speechRecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            statusTextView.text = "Ready for speech..."
        }

        override fun onBeginningOfSpeech() {
            statusTextView.text = "Speech detected..."
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            statusTextView.text = "Processing speech..."
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client error"
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
            resetUiAfterRecognition()
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                resultTextView.text = matches[0]
                statusTextView.text = "Speech recognized successfully!"
            }
            resetUiAfterRecognition()
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                resultTextView.text = matches[0]
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleSpeechRecognition()
            } else {
                Toast.makeText(requireContext(), "Audio permission required", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
    }
}
