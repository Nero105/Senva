package com.example.senva

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class HistorialCitasAdapter(
    private val citas: List<Cita>,
    private val onEliminarCita: (Cita) -> Unit
) : RecyclerView.Adapter<HistorialCitasAdapter.CitaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita_historial, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(citas[position])
    }

    override fun getItemCount() = citas.size

    inner class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvEspecialidad: TextView = itemView.findViewById(R.id.tvEspecialidad)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        private val tvProvincia: TextView = itemView.findViewById(R.id.tvProvincia)
        private val tvDistrito: TextView = itemView.findViewById(R.id.tvDistrito)
        private val btnVerMapa: Button = itemView.findViewById(R.id.btnVerMapa)
        private val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)

        fun bind(cita: Cita) {
            tvEspecialidad.text = cita.especialidad.ifEmpty { "Sin especialidad" }
            tvFecha.text = cita.fechaCreacion
            tvDireccion.text = cita.direccion.ifEmpty { "Sin dirección" }
            tvProvincia.text = "Prov: ${cita.provincia}"
            tvDistrito.text = "Dist: ${cita.distrito}"

            // Botón para ver en mapa
            btnVerMapa.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:${cita.latitud},${cita.longitud}?q=${cita.latitud},${cita.longitud}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                
                if (mapIntent.resolveActivity(itemView.context.packageManager) != null) {
                    itemView.context.startActivity(mapIntent)
                } else {
                    // Si no hay Google Maps, abrir con cualquier app de mapas
                    val fallbackIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    itemView.context.startActivity(fallbackIntent)
                }
            }

            // Botón para eliminar
            btnEliminar.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Eliminar Cita")
                    .setMessage("¿Estás seguro de que quieres eliminar esta cita?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        onEliminarCita(cita)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }
} 