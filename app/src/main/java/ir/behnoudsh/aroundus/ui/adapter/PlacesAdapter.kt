package ir.behnoudsh.aroundus.ui.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import ir.behnoudsh.aroundus.R
import ir.behnoudsh.aroundus.data.room.FoursquarePlace

class PlacesAdapter(
    val context: Activity,
    var placesList: ArrayList<FoursquarePlace>,
    val cellClickListener: CellClickListener
) :
    RecyclerView.Adapter<PlacesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlacesViewHolder {

        return PlacesViewHolder(context.layoutInflater.inflate(R.layout.item_places, parent, false))

    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {

        holder.tv_placeName.setText(placesList.get(position).name)
        holder.tv_placeDistance.setText("در " + placesList.get(position).distance + " متری شما")
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(placesList.get(position))
        }
    }

}

class PlacesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val tv_placeName: TextView = view.findViewById(R.id.tv_placeName)
    val tv_placeDistance: TextView = view.findViewById(R.id.tv_placeDistance)

}

interface CellClickListener {
    fun onCellClickListener(place: FoursquarePlace)
}


