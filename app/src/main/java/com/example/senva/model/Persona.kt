package com.example.senva.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Persona(
    val nombre: String,
    val doctores: List<Doctor>
) : Parcelable 