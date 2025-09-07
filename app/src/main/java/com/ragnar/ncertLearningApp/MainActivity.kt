package com.ragnar.ncertLearningApp

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ragnar.ncertLearningApp.Simulations.LoadSimulationFragment
import com.ragnar.ncertLearningApp.SpeechModels.SpeechToTextFragment
import com.ragnar.ncertLearningApp.SpeechModels.TextToSpeechFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavbar: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Setting the status bar to black
        val window = getWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black))

        bottomNavbar = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)

        // load default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, SpeechToTextFragment())
            .commit()


        bottomNavbar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.speechToText -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, SpeechToTextFragment())
                        .commit()
                    true
                }
                R.id.textToSpeech -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, TextToSpeechFragment())
                        .commit()
                    true
                }
                R.id.simulations -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, LoadSimulationFragment())
                        .commit()
                    true
                }
                else -> false
            }

        }
    }

}