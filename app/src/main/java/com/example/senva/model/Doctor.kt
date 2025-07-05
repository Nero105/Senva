package com.example.senva.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Doctor(
    val nombre: String,
    val especialidad: String,
    val imagenResId: Int,
    val fecha: String,
    val recetas: List<Receta>
) : Parcelable 