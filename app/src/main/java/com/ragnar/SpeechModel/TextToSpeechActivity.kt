package com.ragnar.SpeechModel

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

class TextToSpeechActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var editText: EditText
    private lateinit var speakButton: Button
    private lateinit var languageSpinner: Spinner
    private lateinit var voiceSpinner: Spinner
    private lateinit var speedSeekBar: SeekBar
    private lateinit var speedValueTextView: TextView
    private lateinit var pitchSeekBar: SeekBar       // NEW: Pitch SeekBar
    private lateinit var pitchValueTextView: TextView  // NEW: Pitch value TextView

    private var isTtsInitialized = false
    private var availableVoices: List<Voice> = ArrayList()
    private var selectedLocale: Locale = Locale.US // Default to US English

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_to_speech)

        setupActionBar()
        initializeViews()

        // Initialize TextToSpeech engine
        textToSpeech = TextToSpeech(this, this)

        speakButton.setOnClickListener {
            speakText()
        }
    }

    private fun setupActionBar() {
        supportActionBar?.title = "Text to Speech"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeViews() {
        editText = findViewById(R.id.editTextInput)
        speakButton = findViewById(R.id.btnSpeak)
        languageSpinner = findViewById(R.id.spinnerLanguage)
        voiceSpinner = findViewById(R.id.spinnerVoice)
        speedSeekBar = findViewById(R.id.seekBarSpeed)
        speedValueTextView = findViewById(R.id.textViewSpeedValue)
        pitchSeekBar = findViewById(R.id.seekBarPitch)           // NEW: Initialize pitch SeekBar
        pitchValueTextView = findViewById(R.id.textViewPitchValue) // NEW: Initialize pitch TextView
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true

            // Get all available voices from the TTS engine
            availableVoices = textToSpeech.voices?.toList() ?: emptyList()

            setupLanguageSpinner()
            setupVoiceSpinnerListener()
            setupSpeedSeekBar()
            setupPitchSeekBar() // NEW: Call setup for pitch seekbar

            // Enable the speak button and set the default language, which will trigger
            // the onItemSelected listener to populate the voice spinner.
            speakButton.isEnabled = true
            languageSpinner.setSelection(0, false) // Select English by default
            updateLanguageAndVoices(0) // Manually trigger for initial setup

        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
            Log.e("TTS", "Initialization failed with status: $status")
        }
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("English", "Hindi")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateLanguageAndVoices(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateLanguageAndVoices(position: Int) {
        selectedLocale = when (position) {
            0 -> Locale.US
            1 -> Locale("hi", "IN")
            else -> Locale.US
        }
        textToSpeech.language = selectedLocale
        updateVoiceSpinnerForLanguage(selectedLocale)
    }

    private fun updateVoiceSpinnerForLanguage(locale: Locale) {
        // Filter voices that support the selected language and locale
        val voicesForLanguage = availableVoices.filter { it.locale == locale }

        val voiceNames = voicesForLanguage.map { it.name }

        val voiceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, voiceNames)
        voiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        voiceSpinner.adapter = voiceAdapter

        // If there are voices, set the first one as default
        if (voicesForLanguage.isNotEmpty()) {
            textToSpeech.voice = voicesForLanguage[0]
            voiceSpinner.visibility = View.VISIBLE
        } else {
            // Hide spinner if no voices are available for the selected language
            voiceSpinner.visibility = View.GONE
            Toast.makeText(this, "No voices found for selected language", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupVoiceSpinnerListener() {
        voiceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val voicesForLanguage = availableVoices.filter { it.locale == selectedLocale }
                if (position < voicesForLanguage.size) {
                    textToSpeech.voice = voicesForLanguage[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSpeedSeekBar() {
        // We map the SeekBar's progress (0-20) to a speed range of 0.5x to 2.5x
        speedSeekBar.max = 20
        // Set default speed to 1.0x. Progress = ((1.0f - 0.5f) / 2.0f) * 20 = 5
        speedSeekBar.progress = 5
        speedValueTextView.text = "1.0x" // Set initial text

        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Calculate speed: min_speed + (progress / max_progress) * speed_range
                val speed = 0.5f + (progress / speedSeekBar.max.toFloat()) * 2.0f
                speedValueTextView.text = String.format("%.1fx", speed)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // NEW: Function to set up the pitch SeekBar
    private fun setupPitchSeekBar() {
        // Map progress (0-20) to a pitch range of 0.5x to 2.5x
        // Higher values create a "chipmunk" or child-like effect
        pitchSeekBar.max = 20
        // Set default pitch to 1.5x for a child-like voice.
        // Solved for progress: 1.5 = 0.5 + (p/20)*2.0 -> 1.0 = p/10 -> p=10
        pitchSeekBar.progress = 10
        pitchValueTextView.text = "1.5x" // Set initial text

        pitchSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val pitch = 0.5f + (progress / pitchSeekBar.max.toFloat()) * 2.0f
                pitchValueTextView.text = String.format("%.1fx", pitch)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    private fun speakText() {
        val text = editText.text.toString().trim()

        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            return
        }

        if (isTtsInitialized) {
            // MODIFIED: Set both speech rate and pitch before speaking

            // Set speech rate based on the current speed seekbar progress
            val speedProgress = speedSeekBar.progress
            val speed = 0.5f + (speedProgress / speedSeekBar.max.toFloat()) * 2.0f
            textToSpeech.setSpeechRate(speed)

            // NEW: Set pitch based on the current pitch seekbar progress
            val pitchProgress = pitchSeekBar.progress
            val pitch = 0.5f + (pitchProgress / pitchSeekBar.max.toFloat()) * 2.0f
            textToSpeech.setPitch(pitch)

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        } else {
            Toast.makeText(this, "Text-to-Speech is not initialized yet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}