package com.ragnar.ncertLearningApp.Simulations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ragnar.ncertLearningApp.R

/*
this fragment class used to set up a RecyclerView that
contains list of all simulations.
 */
class LoadSimulationFragment : Fragment() {

    private lateinit var simulationsRecyclerView: RecyclerView
    private lateinit var parentBottomNavBar: CardView
    private lateinit var parentTitleBar: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_load_simulation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // load parent components
        parentTitleBar = requireActivity().findViewById<CardView>(R.id.titleCardView)
        parentBottomNavBar = requireActivity().findViewById<CardView>(R.id.navBarCardView)


        simulationsRecyclerView = view.findViewById<RecyclerView>(R.id.simulationsRecycleView)


        parentTitleBar.visibility = View.VISIBLE
        parentBottomNavBar.visibility = View.VISIBLE
        // list of all available simulations
        val simulationFiles = listOf(
            "Foundation.html",
            "FoundationVisualize.html",
            "RaceStory.html",
            "SpeedDistanceTimeAdventure.html",
            "SpeedDistanceTimeSim.html"
        )

        val simulationAdapter = SimulationAdapter(simulationFiles, ::loadSimulations)

        simulationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        simulationsRecyclerView.adapter = simulationAdapter
    }

    private fun loadSimulations(file: String) {
        val fragment = SimulationFragment()

        val bundle = Bundle()
        bundle.putString("file", file)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}