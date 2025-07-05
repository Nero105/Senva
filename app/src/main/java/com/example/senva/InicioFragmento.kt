package com.example.senva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.senva.LoginActivity.Global
import com.google.firebase.firestore.FirebaseFirestore

class InicioFragmento : Fragment() {

    private lateinit var tvNombreSaludo: TextView
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio_fragmento, container, false)
        tvNombreSaludo = view.findViewById(R.id.tvnombresaludo)
        viewPager = view.findViewById(R.id.viewPagerCampanas)

        obtenerNombreDesdeFirestore()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaCampanas = listOf(
            Campana(
                titulo = "Campaña",
                medica = "Medica",
                mensajeFinal = "Solo por este mes",
                imagenResId = R.drawable.mujeranuncio
            ),
            Campana(
                titulo = "Control General",
                medica = "Medicina General",
                mensajeFinal = "Sin costo adicional",
                imagenResId = R.drawable.sliderpag1
            )
        )

        val adapter = CampanaAdapter(listaCampanas) { campana ->
            // Aquí más adelante mostraremos el DialogFragment
            // Por ahora solo muestra un log
            println("Ver campaña: ${campana.titulo}")
        }

        viewPager.adapter = adapter
    }

    private fun obtenerNombreDesdeFirestore() {
        val sharedPreferences = requireActivity().getSharedPreferences(Global.preferencias_compartidas, AppCompatActivity.MODE_PRIVATE)
        val correo = sharedPreferences.getString("Correo", null)

        if (correo != null) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("usuarios")
                .whereEqualTo("correo", correo)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val nombre = result.documents[0].getString("primeronombre") ?: ""
                        tvNombreSaludo.text = nombre
                    } else {
                        tvNombreSaludo.text = "Nombre no encontrado"
                    }
                }
                .addOnFailureListener {
                    tvNombreSaludo.text = "Error al obtener nombre"
                }
        } else {
            tvNombreSaludo.text = "No se encontró el correo"
        }
    }
}
