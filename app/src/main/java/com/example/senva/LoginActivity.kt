package com.example.senva

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.senva.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    object Global{
        var preferencias_compartidas = "sharedpreferences"
    }

    // 19062025 - EEP - Invocando allow los id de xml Login
    private lateinit var txt_logincorreo: EditText
    private lateinit var txt_loginpassword: EditText
    private lateinit var btn_ingresar: Button
    private lateinit var btn_IngresarGoogle: Button
    private lateinit var tv_crearcuenta: TextView

    private var passwordVisible = false

    // Caja de loading
    private lateinit var loading_layout_L: RelativeLayout
    // Loading
    private lateinit var iv_loading: ImageView
    // Eye Password
    private lateinit var iv_eye_closed: ImageView

    private lateinit var tv_olvidaste: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLogin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        verificar_sesion_abierta()
        AgregarReferencia()
    }

    fun AgregarReferencia(){
        txt_logincorreo = findViewById<EditText>(R.id.txtlogincorreo)
        txt_loginpassword = findViewById<EditText>(R.id.txtloginpassword)
        btn_ingresar = findViewById<Button>(R.id.btnIngresar)
        tv_crearcuenta = findViewById<TextView>(R.id.tvcrearcuenta)
        // Id de caja loading
        loading_layout_L = findViewById<RelativeLayout>(R.id.loading_layoutL)
        // Eye Password
        iv_eye_closed = findViewById<ImageView>(R.id.iv_eye_closeL)
        // Loading
        iv_loading = findViewById<ImageView>(R.id.iv_loadingL)
        // Ingresar con Google
        btn_IngresarGoogle = findViewById<Button>(R.id.btnIngresarGoogleL)
        // Recuperar contraseña
        tv_olvidaste = findViewById<TextView>(R.id.tvolvidaste)

        tv_olvidaste.setOnClickListener {
            val correo = txt_logincorreo.text.toString().trim()

            if (correo.isEmpty()) {
                Toast.makeText(this, "Por favor escribe tu correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                .addOnSuccessListener {
                    Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
        }

        iv_eye_closed.setOnClickListener {
            passwordVisible = !passwordVisible

            if (passwordVisible){
                txt_loginpassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv_eye_closed.setImageResource(R.drawable.ic_eye)
            } else {
                txt_loginpassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_eye_closed.setImageResource(R.drawable.ic_eye_close)
            }
            txt_loginpassword.setSelection(txt_loginpassword.text.length)
        }

        btn_ingresar.setOnClickListener {

            // 24062025 - EEP - Creando una array de forma local
            val campos = listOf(
                txt_logincorreo.text.toString(),
                txt_loginpassword.text.toString()
            )

            // 24062025 - EEP - Creando una variable para limpiar espacios
            val camposvacio = campos.any(){it.trim().isEmpty()}


            if (camposvacio){
                Toast.makeText(this, "Por favor no deje espacios vacios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 19062025 - EEP - Verificacion de contraseña
            if (txt_loginpassword.text.toString() != ""){
                var cajaCarga = loading_layout_L
                var carga = iv_loading
                // 19062025 - EEP - Verificacion de formato de Correo con "EMAIL_ADDRESS"
                if (txt_logincorreo.text.toString().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(txt_logincorreo.text.toString()).matches()){

                    cajaCarga.visibility = View.VISIBLE
                    Glide.with(this)
                        .asGif()
                        .load(R.raw.loading)
                        .into(carga)
                    login_firebase(txt_logincorreo.text.toString(),txt_loginpassword.text.toString())
                }
                else{
                    Toast.makeText(applicationContext,"Correo o Contraseña incorrecta", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext,"Escriba la contraseña", Toast.LENGTH_LONG).show()
            }
        }

        tv_crearcuenta.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_IngresarGoogle.setOnClickListener {
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
                    context = this@LoginActivity
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
                                        val documento = result.documents[0]
                                        val primerNombre = documento.getString("primeronombre") ?: ""

                                        val guardarSesion = getSharedPreferences(Global.preferencias_compartidas, MODE_PRIVATE).edit()
                                        guardarSesion.putString("Nombre", primerNombre)
                                        guardarSesion.putString("Correo", correo)
                                        guardarSesion.apply()

                                        val intent = Intent(this, HomeActivity::class.java)
                                        intent.putExtra("Nombre", primerNombre)
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

    fun login_firebase(xemail: String, xpassword: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(xemail, xpassword)
            .addOnCompleteListener(this) { task ->
                loading_layout_L.visibility = View.GONE

                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.isEmailVerified) {
                        val correo = user.email.toString()

                        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        firestore.collection("usuarios")
                            .whereEqualTo("correo", correo)
                            .get()
                            .addOnSuccessListener { result ->
                                if (!result.isEmpty) {
                                    val documento = result.documents[0]
                                    val primerNombre = documento.getString("primeronombre") ?: ""
                                    val guardarSesion = getSharedPreferences(Global.preferencias_compartidas, MODE_PRIVATE).edit()
                                    guardarSesion.putString("Nombre", primerNombre)
                                    guardarSesion.putString("Correo", correo)
                                    guardarSesion.apply()

                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.putExtra("Correo", user.email)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "No se encontró información del usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(this, "Verifica tu correo antes de iniciar sesión", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this, "Correo o Contraseña Incorrecta", Toast.LENGTH_LONG).show()
                }
            }
    }
    fun verificar_sesion_abierta() {
        val sesion_abierta: SharedPreferences = getSharedPreferences(Global.preferencias_compartidas, MODE_PRIVATE)
        val correo = sesion_abierta.getString("Correo", null)

        if (correo != null) {
            val cajaCarga = loading_layout_L
            val carga = iv_loading

            cajaCarga.visibility = View.VISIBLE
            Glide.with(this)
                .asGif()
                .load(R.raw.loading)
                .into(carga)

            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("usuarios")
                .whereEqualTo("correo", correo)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val documento = result.documents[0]
                        val primerNombre = documento.getString("primeronombre") ?: ""

                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("Nombre", primerNombre)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "No se encontró información del usuario", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


    fun guardar_sesion(correo: String){
        var guardar_sesion: SharedPreferences.Editor = getSharedPreferences(Global.preferencias_compartidas, MODE_PRIVATE).edit()
        guardar_sesion.putString("Correo",correo)
        guardar_sesion.apply()
    }

}