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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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

        // Login con Google
        btn_ingresarcongoogle = findViewById<Button>(R.id.btningresargoogleM)

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

        btn_ingresarcongoogle.setOnClickListener {
            iniciarSesionConGoogle()
        }

    }

     fun iniciarSesionConGoogle() {
         val credentialManager = androidx.credentials.CredentialManager.create(this)

         val signInWithGoogleOption = com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption.Builder(
             getString(R.string.web_cliente)
         ).build()

         val request = androidx.credentials.GetCredentialRequest.Builder()
             .addCredentialOption(signInWithGoogleOption)
             .build()

         lifecycleScope.launch {
             try {
                 val result = credentialManager.getCredential(
                     request = request,
                     context = this@MainActivity
                 )
                 handleSignIn(result)
             } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                 e.printStackTrace()
             }
         }
    }

    fun handleSignIn(result: androidx.credentials.GetCredentialResponse) {
        val credential = result.credential

        if (credential is androidx.credentials.CustomCredential &&
            credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
                    .createFrom(credential.data)

                val firebaseCredential = com.google.firebase.auth.GoogleAuthProvider.getCredential(
                    googleCredential.idToken, null
                )

                com.google.firebase.auth.FirebaseAuth.getInstance()
                    .signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = task.result.user
                            val correo = user?.email ?: ""
                            val nombre = user?.displayName ?: ""

                            // Verifica si existe en Firestore
                            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            firestore.collection("usuarios")
                                .whereEqualTo("correo", correo)
                                .get()
                                .addOnSuccessListener { result ->
                                    if (!result.isEmpty) {
                                        // Ya está registrado
                                        guardar_sesion(correo)
                                        val intent = Intent(this, HomeActivity::class.java)
                                        intent.putExtra("Correo", correo)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        // No está registrado → mandarlo al Register
                                        val intent = Intent(this, RegisterActivity::class.java)
                                        intent.putExtra("NOMBRE", nombre)
                                        intent.putExtra("CORREO", correo)
                                        intent.putExtra("GOOGLE", true)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                        }
                    }

            } catch (e: com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException) {
                e.printStackTrace()
            }
        }
    }

    fun guardar_sesion(correo: String) {
        val prefs = getSharedPreferences(LoginActivity.Global.preferencias_compartidas, MODE_PRIVATE).edit()
        prefs.putString("Correo", correo)
        prefs.apply()
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