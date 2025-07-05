package com.example.senva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.senva.model.Doctor
import com.example.senva.model.Receta

class DiagnosticoFragment2 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var doctor: Doctor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doctor = requireArguments().getParcelable("doctor")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diagnostico2, container, false)
        recyclerView = view.findViewById(R.id.recycler_recetas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RecetaAdapter(doctor.recetas) { receta ->
            (activity as? HomeActivity)?.navigateToDiagnostico3(doctor, receta)
        }
        recyclerView.adapter = adapter
        // Mostrar datos del doctor centrado
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreDoctor2)
        val tvEspecialidad = view.findViewById<TextView>(R.id.tvEspecialidad2)
        val tvFecha = view.findViewById<TextView>(R.id.tvFechaDoctor2)
        tvNombre.text = doctor.nombre
        tvEspecialidad.text = doctor.especialidad
        tvFecha.text = doctor.fecha
        return view
    }

    companion object {
        fun newInstance(doctor: Doctor): DiagnosticoFragment2 {
            val fragment = DiagnosticoFragment2()
            val args = Bundle()
            args.putParcelable("doctor", doctor)
            fragment.arguments = args
            return fragment
        }
    }
} 