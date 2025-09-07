package com.ragnar.ncertLearningApp.Simulations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ragnar.ncertLearningApp.R

class SimulationAdapter(
    private val simulationList: List<String>,
    private val onItemClicked: (String) -> Unit

): RecyclerView.Adapter<SimulationAdapter.SimulationViewHolder>() {


    /**
     * The ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class SimulationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val simulationNameTextView: TextView = itemView.findViewById(R.id.simulationNameTextView)
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * We inflate the layout for a single row here.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimulationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_simulations, parent, false)
        return SimulationViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the ViewHolder to reflect the item at the given position.
     */
    override fun onBindViewHolder(holder: SimulationViewHolder, position: Int) {
        val simulationName = simulationList[position]
        holder.simulationNameTextView.text = simulationName

        holder.itemView.setOnClickListener {
            onItemClicked(simulationName)
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return simulationList.size
    }
}