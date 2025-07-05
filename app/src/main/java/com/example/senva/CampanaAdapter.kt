package com.example.senva

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.senva.R

class CampanaAdapter(
    private val listaCampanas: List<Campana>,
    private val onClickVer: (Campana) -> Unit
) : RecyclerView.Adapter<CampanaAdapter.CampanaViewHolder>() {

    class CampanaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCampana: ImageView = itemView.findViewById(R.id.imgcampana)
        val tvTituloCampana: TextView = itemView.findViewById(R.id.tvTituloCampana)
        val tvMedicaCampana: TextView = itemView.findViewById(R.id.tvMedicaCampana)
        val tvLimiteCampana: TextView = itemView.findViewById(R.id.tvLimiteCampana)
        val btnVerCampana: Button = itemView.findViewById(R.id.btnVerCampana)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampanaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_campana, parent, false)
        return CampanaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CampanaViewHolder, position: Int) {
        val campana = listaCampanas[position]

        // Llenar los datos
        holder.tvTituloCampana.text = campana.titulo
        holder.tvMedicaCampana.text = campana.medica
        holder.tvLimiteCampana.text = campana.mensajeFinal

        // Si quieres usar imágenes más adelante, aquí iría con Glide o Picasso

        // Evento click en botón "Ver"
        holder.btnVerCampana.setOnClickListener {
            onClickVer(campana)
        }
    }

    override fun getItemCount(): Int = listaCampanas.size
}
