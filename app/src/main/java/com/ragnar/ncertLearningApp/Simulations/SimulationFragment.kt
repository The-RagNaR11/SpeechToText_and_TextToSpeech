package com.ragnar.ncertLearningApp.Simulations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.cardview.widget.CardView
import com.ragnar.ncertLearningApp.R

class SimulationFragment : Fragment() {
    private lateinit var webView: WebView

    private lateinit var parentBottomNavBar: CardView
    private lateinit var parentTitleBar: CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simulation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // load parent components
        parentTitleBar = requireActivity().findViewById<CardView>(R.id.titleCardView)
        parentBottomNavBar = requireActivity().findViewById<CardView>(R.id.navBarCardView)

        parentTitleBar.visibility = View.GONE
        parentBottomNavBar.visibility = View.GONE
        webView = view.findViewById<WebView>(R.id.simulationWebView)

        val simulationName = arguments?.getString("file")
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/$simulationName")

//        requireActivity().onBackPressedDispatcher.addCallback(
//            viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    // This will navigate back to LoadSimulationFragment
//                    parentFragmentManager.popBackStack()
//                }
//            }
//        )
    }
}