package com.example.senva

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class HistorialCitasActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var llNoCitas: LinearLayout
    private lateinit var adapter: HistorialCitasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_citas)

        // Inicializar la base de datos
        databaseHelper = DatabaseHelper(this)

        // Inicializar vistas
        recyclerView = findViewById(R.id.rvHistorialCitas)
        llNoCitas = findViewById(R.id.llNoCitas)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar botón de regreso
        btnBack.setOnClickListener {
            finish()
        }

        // Cargar citas
        cargarCitas()
    }

    private fun cargarCitas() {
        val citas = databaseHelper.obtenerTodasLasCitas()

        if (citas.isEmpty()) {
            // Mostrar mensaje de no hay citas
            recyclerView.visibility = View.GONE
            llNoCitas.visibility = View.VISIBLE
        } else {
            // Mostrar lista de citas
            recyclerView.visibility = View.VISIBLE
            llNoCitas.visibility = View.GONE

            adapter = HistorialCitasAdapter(citas) { cita ->
                eliminarCita(cita)
            }
            recyclerView.adapter = adapter
        }
    }

    private fun eliminarCita(cita: Cita) {
        val filasEliminadas = databaseHelper.eliminarCita(cita.id)
        
        if (filasEliminadas > 0) {
            Snackbar.make(recyclerView, "Cita eliminada exitosamente", Snackbar.LENGTH_SHORT).show()
            // Recargar la lista
            cargarCitas()
        } else {
            Snackbar.make(recyclerView, "Error al eliminar la cita", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar citas cuando se regrese a esta actividad
        cargarCitas()
    }
} 