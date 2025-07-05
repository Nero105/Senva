package com.example.senva

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class EspecialidadesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_especialidades)

        val recyclerView = findViewById<RecyclerView>(R.id.rvEspecialidades)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val especialidades = listOf(
            Especialidad("Medicina G.", R.drawable.medicinageneral),
            Especialidad("Nutrición", R.drawable.nutricion),
            Especialidad("Pediatría", R.drawable.pediatria),
            Especialidad("Cardiología", R.drawable.cardiologia),
            Especialidad("Dermatología", R.drawable.dermatologia),
            Especialidad("Traumatología", R.drawable.traumatologia),
            Especialidad("Oftalmología", R.drawable.oftalmologia),
            Especialidad("Ginecología", R.drawable.ginecologia),
            Especialidad("Neumología", R.drawable.neumologia),
            Especialidad("Endocrinología", R.drawable.endocrinologia),
            Especialidad("Psicología", R.drawable.psicologia),
            Especialidad("Gastro", R.drawable.gastro),
            Especialidad("Reumatología", R.drawable.reumatologia),
            Especialidad("Nefrología", R.drawable.nefrologia),
            Especialidad("Geriatría", R.drawable.geriatria)
        )

        val adapter = EspecialidadesAdapter(especialidades) { especialidad ->
            // Acción al hacer click en una especialidad (puedes personalizarlo)
        }
        recyclerView.adapter = adapter

        val btnBack = findViewById<android.widget.ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
} 