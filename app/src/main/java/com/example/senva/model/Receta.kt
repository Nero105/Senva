package com.example.senva.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Receta(
    val codigo: String,
    val fecha: String,
    val esReciente: Boolean
) : Parcelable 