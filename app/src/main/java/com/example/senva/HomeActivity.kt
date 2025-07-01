package com.example.senva

import android.content.Intent
import android.content.SharedPreferences
import android.credentials.ClearCredentialStateRequest
import android.credentials.CredentialManager
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.senva.LoginActivity.Global
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    // 19062025 - EEP - Invocando allow los id de xml Home
    private lateinit var tv_extocorreo: TextView
    private lateinit var tv_cerrarsesion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //20062025 - EEP - Hacer que el bar status resalte
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = getColor(R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            // Para Android 10 o menor (seguridad)
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearHome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AgregarReferencia()

    }

    fun AgregarReferencia() {
        tv_extocorreo = findViewById<TextView>(R.id.tvextracorreo)
        tv_cerrarsesion = findViewById<TextView>(R.id.tvcerrarsesion)

        tv_cerrarsesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            borrar_sesion()
            finish()
        }

        val correo = intent.getStringExtra("Correo")
        if (correo != null){
            tv_extocorreo.text = correo
        } else{
            tv_extocorreo.text = "No se recibió correo"
        }

    }

    fun borrar_sesion() {
        val borrarSesion = getSharedPreferences(Global.preferencias_compartidas, MODE_PRIVATE).edit()
        borrarSesion.clear()
        borrarSesion.apply()

        Firebase.auth.signOut()
    }
}