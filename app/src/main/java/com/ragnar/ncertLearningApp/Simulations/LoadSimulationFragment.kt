package com.ragnar.ncertLearningApp.Simulations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.ragnar.ncertLearningApp.R

/*
this fragment class used to set up a spinner that
contains list of all simulations

loads the RaceStory simulation by default and changes the simulation on the
basis of selected item on sinner i.e. dropdown menu
 */
class LoadSimulationFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_race_story_simulation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.webView)
        spinner = view.findViewById<Spinner>(R.id.simulationSpinner)


        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/RaceStory.html")

        setupSpinner()
        loadWebView()
    }

    private fun setupSpinner() {
        val simulationOptions = arrayOf<String?>(
            "Foundation",
            "FoundationVisualize",
            "RaceStory",
            "SpeedDistanceTimeAdventure",
            "SpeedDistanceTimeSimulation"
        )
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            simulationOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }

    private fun loadWebView() {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedFragment: String? = parent.getItemAtPosition(position).toString()
                // mapping the selected option with corresponding file
                val htmlFile = when (selectedFragment) {
                    "Foundation" -> "Foundation.html"
                    "FoundationVisualize" -> "FoundationVisualize.html"
                    "RaceStory" -> "RaceStory.html"
                    "SpeedDistanceTimeAdventure" -> "SpeedDistanceTimeAdventure.html"
                    "SpeedDistanceTimeSimulation" -> "SpeedDistanceTimeSim.html"
                    else -> "RaceStory.html" // default simulation
                }

    //                loading the selected simulation
                webView.loadUrl("file:///android_asset/$htmlFile")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // loading the default simulation i.e. RaceStory
            }
        }
    }

}