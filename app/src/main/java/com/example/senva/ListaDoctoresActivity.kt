package com.example.senva

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaDoctoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctores)

        val recyclerView = findViewById<RecyclerView>(R.id.rvDoctoresDisponibles)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lista de ejemplo
        val doctores = listOf(
            DoctorDisponible("DRA. RAMÍREZ", "Cardióloga", R.drawable.doctoruno),
            DoctorDisponible("DRA. RAMÍREZ", "Cardióloga", R.drawable.doctoruno),
            DoctorDisponible("DRA. RAMÍREZ", "Cardióloga", R.drawable.doctoruno)
        )

        val adapter = DoctoresDisponiblesAdapter(doctores)
        recyclerView.adapter = adapter

        // Lista de ejemplo para ocupados
        val ocupados = listOf(
            DoctorDisponible("DRA. RAMÍREZ", "Cardióloga", R.drawable.doctoruno),
            DoctorDisponible("DRA. RAMÍREZ", "Cardióloga", R.drawable.doctoruno),
            DoctorDisponible("DRA. RAMÍREZ", "Cardióloga", R.drawable.doctoruno)
        )
        val rvOcupados = findViewById<RecyclerView>(R.id.rvDoctoresOcupados)
        rvOcupados.layoutManager = LinearLayoutManager(this)
        val adapterOcupados = DoctoresDisponiblesAdapter(ocupados)
        rvOcupados.adapter = adapterOcupados
    }
} 