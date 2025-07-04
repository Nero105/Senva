package com.example.senva

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.Gravity.*
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageButton
import com.bumptech.glide.Glide
import androidx.core.view.GravityCompat

class HomeActivity : AppCompatActivity() {

    private lateinit var tv_nombresaludo: TextView
    private lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Referencias al DrawerLayout y NavigationView
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        // Referencia al header del NavigationView
        val headerView = navigationView.getHeaderView(0)
        val imageViewProfile = headerView.findViewById<ImageView>(R.id.imageViewProfile)

        val textViewSaludo = headerView.findViewById<TextView>(R.id.textViewSaludo)

        // Cargar el nombre del usuario dinámicamente (ejemplo: desde SharedPreferences o Firestore)
        val sharedPreferences = getSharedPreferences(LoginActivity.Global.preferencias_compartidas, MODE_PRIVATE)
        val nombreUsuario = sharedPreferences.getString("Nombre", "Usuario")
        textViewSaludo.text = "Hola, $nombreUsuario!"

        // Configurar el botón de imagen de usuario para abrir el Drawer
        val btnDrawerUser = findViewById<ImageButton>(R.id.btnDrawerUser)
        btnDrawerUser.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Cargar la imagen del usuario (desde SharedPreferences, Firestore, etc.)
        val fotoPerfil = sharedPreferences.getString("FotoPerfil", null)
        if (fotoPerfil != null && fotoPerfil.isNotEmpty()) {
            // Si es una URL o ruta local, puedes usar Glide o BitmapFactory
            Glide.with(this)
                .load(fotoPerfil)
                .placeholder(R.drawable.usuario) // Cambia por tu imagen por defecto
                .into(btnDrawerUser)
        } else {
            btnDrawerUser.setImageResource(R.drawable.usuario) // Cambia por tu imagen por defecto
        }

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
                    supportFragmentManager.commit {
                        replace<MiCitaFragmento>(R.id.frameContainer)
                    }
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

        // Manejar clics en el menú lateral
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itemPerfil -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.itemCerrarSesion -> {
                    borrar_sesion()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    false
                }
            }
        }
    }

    private fun borrar_sesion() {
        val borrarSesion = getSharedPreferences(LoginActivity.Global.preferencias_compartidas, MODE_PRIVATE).edit()
        borrarSesion.clear()
        borrarSesion.apply()
        FirebaseAuth.getInstance().signOut()
    }
}
