package com.example.senva

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.senva.model.Doctor

class DoctorAdapter(
    private val doctores: List<Doctor>,
    private val onDoctorClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_diagnostico, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(doctores[position])
    }

    override fun getItemCount(): Int = doctores.size

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivDoctor: ImageView = itemView.findViewById(R.id.ivDoctor)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreDoctor)
        private val tvEspecialidad: TextView = itemView.findViewById(R.id.tvEspecialidad)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFechaDoctor)
        private val btnFlecha: ImageView = itemView.findViewById(R.id.btnFlecha)

        fun bind(doctor: Doctor) {
            ivDoctor.setImageResource(doctor.imagenResId)
            tvNombre.text = doctor.nombre
            tvEspecialidad.text = doctor.especialidad
            tvFecha.text = doctor.fecha
            btnFlecha.setOnClickListener {
                onDoctorClick(doctor)
            }
        }
    }
} 