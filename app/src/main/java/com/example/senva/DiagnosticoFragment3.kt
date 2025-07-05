package com.example.senva

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.senva.model.Doctor
import com.example.senva.model.Receta

class DiagnosticoFragment3 : Fragment() {
    companion object {
        fun newInstance(doctor: Doctor, receta: Receta): DiagnosticoFragment3 {
            val fragment = DiagnosticoFragment3()
            val args = Bundle()
            args.putParcelable("doctor", doctor)
            args.putParcelable("receta", receta)
            fragment.arguments = args
            return fragment
        }
    }
} 