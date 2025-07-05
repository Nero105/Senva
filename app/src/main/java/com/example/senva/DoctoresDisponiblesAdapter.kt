package com.example.senva

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class DoctoresDisponiblesAdapter(
    private val doctores: List<DoctorDisponible>
) : RecyclerView.Adapter<DoctoresDisponiblesAdapter.DoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_disponible, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(doctores[position])
    }

    override fun getItemCount() = doctores.size

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgDoctor: ImageView = itemView.findViewById(R.id.imgDoctor)
        private val tvNombreDoctor: TextView = itemView.findViewById(R.id.tvNombreDoctor)
        private val tvEspecialidadDoctor: TextView = itemView.findViewById(R.id.tvEspecialidadDoctor)
        private val tvVerMas: TextView = itemView.findViewById(R.id.tvVerMas)
        private val imgFlecha: ImageView = itemView.findViewById(R.id.imgFlecha)

        fun bind(doctor: DoctorDisponible) {
            imgDoctor.setImageResource(doctor.imagenRes)
            tvNombreDoctor.text = doctor.nombre
            tvEspecialidadDoctor.text = "( ${doctor.especialidad} )"

            tvVerMas.setOnClickListener {
                Toast.makeText(itemView.context, "Ver más de ${doctor.nombre}", Toast.LENGTH_SHORT).show()
            }
            imgFlecha.setOnClickListener {
                Toast.makeText(itemView.context, "Ver más de ${doctor.nombre}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 