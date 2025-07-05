package com.example.senva

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EspecialidadesAdapter(
    private val especialidades: List<Especialidad>,
    private val onClick: (Especialidad) -> Unit
) : RecyclerView.Adapter<EspecialidadesAdapter.EspecialidadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EspecialidadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_especialidad, parent, false)
        return EspecialidadViewHolder(view)
    }

    override fun onBindViewHolder(holder: EspecialidadViewHolder, position: Int) {
        holder.bind(especialidades[position])
    }

    override fun getItemCount() = especialidades.size

    inner class EspecialidadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgEspecialidad: ImageView = itemView.findViewById(R.id.imgEspecialidad)
        private val tvNombreEspecialidad: TextView = itemView.findViewById(R.id.tvNombreEspecialidad)

        fun bind(especialidad: Especialidad) {
            imgEspecialidad.setImageResource(especialidad.imagenRes)
            tvNombreEspecialidad.text = especialidad.nombre
            itemView.setOnClickListener { onClick(especialidad) }
        }
    }
}

// Modelo de datos para especialidad
class Especialidad(val nombre: String, val imagenRes: Int) 