package com.ragnar.SpeechModel.SpeechModels

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.ragnar.SpeechModel.R
import java.util.Locale

/*
This is a fragment that will take some text as input from EditText
and then use the "android.speech.tts.TextToSpeech" to convert it into sound output i.e. speech

there are various parameter that will affect the sound quality and type of sound being generated
which are
a. speed
b. pitch
c. sound type
 */
class TextToSpeechFragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var editText: EditText
    private lateinit var speakButton: Button
    private lateinit var languageSpinner: Spinner
    private lateinit var voiceSpinner: Spinner
    private lateinit var speedSeekBar: SeekBar
    private lateinit var speedValueTextView: TextView
    private lateinit var pitchSeekBar: SeekBar
    private lateinit var pitchValueTextView: TextView

    private var isTtsInitialized = false
    private var availableVoices: List<Voice> = ArrayList()
    private var selectedLocale: Locale = Locale.US


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return   inflater.inflate(R.layout.fragment_text_to_speech, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)

        // Initialize TextToSpeech engine
        textToSpeech = TextToSpeech(requireContext(), this)

        speakButton.setOnClickListener {
            speakText()
        }
    }

    private fun initializeViews(view : View) {
        editText = view.findViewById(R.id.editTextInput)
        speakButton = view.findViewById(R.id.btnSpeak)
        languageSpinner = view.findViewById(R.id.spinnerLanguage)
        voiceSpinner = view.findViewById(R.id.spinnerVoice)
        speedSeekBar = view.findViewById(R.id.seekBarSpeed)
        speedValueTextView = view.findViewById(R.id.textViewSpeedValue)
        pitchSeekBar = view.findViewById(R.id.seekBarPitch)
        pitchValueTextView = view.findViewById(R.id.textViewPitchValue)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true

            // Get all available voices from the TTS engine
            availableVoices = textToSpeech.voices?.toList() ?: emptyList()

            setupLanguageSpinner()
            setupVoiceSpinnerListener()
            setupSpeedSeekBar()
            setupPitchSeekBar()

            // Enable the speak button and set the default language, which will trigger
            // the onItemSelected listener to populate the voice spinner.
            speakButton.isEnabled = true
            languageSpinner.setSelection(0, false) // Select English by default
            updateLanguageAndVoices(0)

        } else {
            Toast.makeText(requireContext(), "TTS initialization failed", Toast.LENGTH_SHORT).show()
            Log.e("TTS", "Initialization failed with status: $status")
        }
    }
    private fun setupLanguageSpinner() {
        val languages = arrayOf("English", "Hindi")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
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

        val voiceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, voiceNames)
        voiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        voiceSpinner.adapter = voiceAdapter

        // If there are voices, set the first one as default
        if (voicesForLanguage.isNotEmpty()) {
            textToSpeech.voice = voicesForLanguage[0]
            voiceSpinner.visibility = View.VISIBLE
        } else {
            // Hide spinner if no voices are available for the selected language
            voiceSpinner.visibility = View.GONE
            Toast.makeText(requireContext(), "No voices found for selected language", Toast.LENGTH_SHORT).show()
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
        // Set default speed to 1.0x
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
        pitchSeekBar.max = 20
        pitchSeekBar.progress = 5
        pitchValueTextView.text = "1.0x"

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
            Toast.makeText(requireContext(), "Please enter some text", Toast.LENGTH_SHORT).show()
            return
        }
        if (isTtsInitialized) {
            // Set speech rate based on the current speed seekbar progress
            val speedProgress = speedSeekBar.progress
            val speed = 0.5f + (speedProgress / speedSeekBar.max.toFloat()) * 2.0f
            textToSpeech.setSpeechRate(speed)

            val pitchProgress = pitchSeekBar.progress
            val pitch = 0.5f + (pitchProgress / pitchSeekBar.max.toFloat()) * 2.0f
            textToSpeech.setPitch(pitch)

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        } else {
            Toast.makeText(requireContext(), "Text-to-Speech is not initialized yet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(::textToSpeech.isInitialized){
            textToSpeech.stop()
            textToSpeech.shutdown()
            Log.d("TTS", "TTS engine shut down.")
        }
    }
}