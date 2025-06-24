package com.example.senva

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Bienvenida
    private lateinit var btn_crearcuenta: Button
    private lateinit var btn_ingresarcongoogle: Button
    // Login
    private lateinit var tv_iniciarsesion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verificarSesion()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AgregarReferencia()
    }

    fun AgregarReferencia(){
        // 19062025 - EEP - Agregando las variable con el id correspondiente
        // Bienvenida
        btn_crearcuenta = findViewById<Button>(R.id.btncrearcuenta)

        // Login
        tv_iniciarsesion = findViewById<TextView>(R.id.tvbieniniciarsesion)

        //19062025 - EEP -Redireccion del Register
        btn_crearcuenta.setOnClickListener {
                val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        //19062025 - EEP - Redireccion del Login
        tv_iniciarsesion.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun verificarSesion() {
        val preferencias = getSharedPreferences(LoginActivity.Global.preferencias_compartidas, MODE_PRIVATE)
        val correo = preferencias.getString("Correo", null)

        if (correo != null) {
            // Si ya hay sesión, ir directo al Home
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("Correo", correo)
            startActivity(intent)
            finish()
        }
    }



}