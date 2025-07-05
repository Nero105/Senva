package com.example.senva

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class ReservarCitaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservarcita)

        val spinner = findViewById<Spinner>(R.id.spinnerQuienAtiende)
        val opciones = listOf("Seleccione", "Yo", "Hija", "Papá")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        spinner.adapter = adapter
    }
} 