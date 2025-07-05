package com.example.senva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.senva.model.Persona
import com.example.senva.model.Doctor
import com.example.senva.model.Receta

class DiagnosticoFragment1 : Fragment() {
    private lateinit var spinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorAdapter
    private lateinit var personas: List<Persona>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diagnostico1, container, false)
        spinner = view.findViewById(R.id.spinner_personas)
        recyclerView = view.findViewById(R.id.recycler_doctores)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupData()
        val nombres = personas.map { it.nombre }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombres)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val persona = personas[position]
                adapter = DoctorAdapter(persona.doctores) { doctor ->
                    // Navegar a DiagnosticoFragment2 con el doctor seleccionado
                    (activity as? HomeActivity)?.navigateToDiagnostico2(doctor)
                }
                recyclerView.adapter = adapter
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        return view
    }

    private fun setupData() {
        // Datos de ejemplo siguiendo el prompt
        val recetaRecientes = listOf(
            Receta("001", "30/06/2025", true),
            Receta("002", "29/06/2025", true),
            Receta("003", "28/06/2025", true)
        )
        val recetaAnteriores = listOf(
            Receta("004", "27/06/2025", false),
            Receta("005", "26/06/2025", false)
        )
        val doctor1 = Doctor("DRA. RAMÍREZ", "Cardiología", R.drawable.doctoruno, "30/06/2025", recetaRecientes + recetaAnteriores)
        val doctor2 = Doctor("DR. PÉREZ", "Medicina General", R.drawable.doctoruno, "30/06/2025", recetaRecientes.take(2) + recetaAnteriores)
        val doctor3 = Doctor("DRA. LÓPEZ", "Pediatría", R.drawable.doctoruno, "30/06/2025", recetaRecientes + recetaAnteriores + recetaAnteriores)
        personas = listOf(
            Persona("YO", listOf(doctor1, doctor2, doctor3, doctor1, doctor2)), // 5 doctores
            Persona("Hija", listOf(doctor3, doctor2)), // 2 doctores
            Persona("Papá", listOf(doctor1, doctor2, doctor3, doctor1, doctor2, doctor3)) // 6 doctores
        )
    }
} 