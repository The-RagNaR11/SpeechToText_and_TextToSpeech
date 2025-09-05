package com.ragnar.SpeechModel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnTextToSpeech = findViewById<Button>(R.id.btnTextToSpeech)
        val btnSpeechToText = findViewById<Button>(R.id.btnSpeechToText)

        btnTextToSpeech.setOnClickListener {
            val intent = Intent(this, TextToSpeechActivity::class.java)
            startActivity(intent)
        }

        btnSpeechToText.setOnClickListener {
            val intent = Intent(this, SpeechToTextActivity::class.java)
            startActivity(intent)
        }
    }
}