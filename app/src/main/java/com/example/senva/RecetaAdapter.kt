package com.example.senva

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.senva.model.Receta

class RecetaAdapter(
    private val recetas: List<Receta>,
    private val onVerClick: (Receta) -> Unit
) : RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        holder.bind(recetas[position])
    }

    override fun getItemCount(): Int = recetas.size

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigoReceta)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFechaReceta)
        private val btnVer: ImageView = itemView.findViewById(R.id.btnVerReceta)
        private val tvVer: TextView = itemView.findViewById(R.id.tvVerReceta)
        fun bind(receta: Receta) {
            tvCodigo.text = "Receta - ${receta.codigo}"
            tvFecha.text = receta.fecha
            btnVer.setOnClickListener { onVerClick(receta) }
            tvVer.setOnClickListener { onVerClick(receta) }
        }
    }
} 