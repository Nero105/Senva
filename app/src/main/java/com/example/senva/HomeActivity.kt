package com.example.senva

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.senva.LoginActivity.Global
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Estilo barra de estado
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = getColor(R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearHome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        navigation = findViewById(R.id.navMenu)
        navigation.selectedItemId = R.id.itemFragment2

        // Cargar fragmento por defecto
        supportFragmentManager.commit {
            replace<InicioFragmento>(R.id.frameContainer)
        }
        
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemFragment1 -> {
                    val intent = Intent(this, MiCitaActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.itemFragment2 -> {
                    supportFragmentManager.commit {
                        replace<InicioFragmento>(R.id.frameContainer)
                    }
                    true
                }
                R.id.itemFragment3 -> {
                    supportFragmentManager.commit {
                        replace<DiagnosticoFragment>(R.id.frameContainer)
                    }
                    true
                }
                else -> false
            }
        }

        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<com.google.android.material.navigation.NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itemCerrarSesion -> {
                    borrar_sesion()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun borrar_sesion() {
        val borrarSesion = getSharedPreferences(Global.preferencias_compartidas, MODE_PRIVATE).edit()
        borrarSesion.clear()
        borrarSesion.apply()
        FirebaseAuth.getInstance().signOut()
    }
}
