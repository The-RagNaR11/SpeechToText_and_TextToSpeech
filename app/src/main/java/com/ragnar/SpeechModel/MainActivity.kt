package com.ragnar.SpeechModel

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ragnar.SpeechModel.Simulations.RaceStorySimulationFragment
import com.ragnar.SpeechModel.SpeechModels.SpeechToTextFragment
import com.ragnar.SpeechModel.SpeechModels.TextToSpeechFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnTextToSpeech = findViewById<Button>(R.id.btnTextToSpeech)
        val btnSpeechToText = findViewById<Button>(R.id.btnSpeechToText)
        val simulationButton = findViewById<Button>(R.id.btnSimulation)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, SpeechToTextFragment())
            .commit()

        btnTextToSpeech.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, TextToSpeechFragment())
                .commit()
        }

        btnSpeechToText.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, SpeechToTextFragment())
                .commit()
        }

        simulationButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, RaceStorySimulationFragment())
                .commit()
        }
    }
}