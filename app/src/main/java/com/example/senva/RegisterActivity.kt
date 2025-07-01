package com.example.senva

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.credentials.CredentialManager
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
// firestore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    // 19062025 - EEP - Invocando allow los id de xml Register
    private lateinit var txt_logincorreo: EditText
    private lateinit var txt_loginpassword: EditText
    private lateinit var btn_register: Button
    private lateinit var tv_iniciarsesion: TextView
    private lateinit var txt_dni: EditText
    private lateinit var txt_primernombre: EditText
    private lateinit var txt_segundonombre: EditText
    private lateinit var txt_primerapellido: EditText
    private lateinit var txt_segundoapellido: EditText
    private lateinit var txt_telefono: EditText

    // Declaracion para el spinner
    private lateinit var sp_documentos: Spinner
    private lateinit var firestore: FirebaseFirestore

    // Crear cuenta con Google
    private lateinit var btn_IngresarGoogle: Button

    // pantalla de carga
    private lateinit var iv_loading: ImageView

    // Layout carga
    private lateinit var loadin_layout: RelativeLayout

    // Mostrar password
    private lateinit var ic_eye_close: ImageView
    private var passwordVisible = false

    private var esCuentaGoogle: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //20062025 - EEP - Hacer que el bar status resalte
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = getColor(R.color.white)

        //20062025 -EEP - Controlador de apariencia del sistema
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearRegister)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AgregarReferencia()

        val correoGoogle = intent.getStringExtra("CORREO")
        val nombreGoogle = intent.getStringExtra("NOMBRE")
        esCuentaGoogle = intent.getBooleanExtra("GOOGLE", false)

        if (esCuentaGoogle) {
            txt_logincorreo.setText(correoGoogle)

            val partes = nombreGoogle?.split(" ")
            if (partes != null && partes.size >= 2) {
                txt_primernombre.setText(partes[0])
                txt_primerapellido.setText(partes[1])
            } else if (partes != null && partes.isNotEmpty()) {
                txt_primernombre.setText(partes[0])
            }
        }



    }

    fun AgregarReferencia() {
        txt_logincorreo = findViewById<EditText>(R.id.txtcrearemail)
        txt_loginpassword = findViewById<EditText>(R.id.txtcrearpassword)
        btn_register = findViewById<Button>(R.id.btnenviarregister)
        tv_iniciarsesion = findViewById<TextView>(R.id.tviniciarseionmedientotexto)
        txt_dni = findViewById<EditText>(R.id.txtdni)
        txt_telefono = findViewById<EditText>(R.id.txttelefono)

        // Google
        btn_IngresarGoogle = findViewById<Button>(R.id.btnIngresarGoogle)

        txt_primernombre = findViewById<EditText>(R.id.txtprimernombre)
        txt_segundonombre = findViewById<EditText>(R.id.txtsegundonombre)
        txt_primerapellido = findViewById<EditText>(R.id.txtprimerapellido)
        txt_segundoapellido = findViewById<EditText>(R.id.txtsegundoapellido)

        // Spinner
        sp_documentos = findViewById<Spinner>(R.id.spdocumentos)

        // Gif
        iv_loading = findViewById<ImageView>(R.id.iv_loading)

        // Relative layout
        loadin_layout = findViewById<RelativeLayout>(R.id.loading_layout)

        // Password
        ic_eye_close = findViewById<ImageView>(R.id.iv_eye_close)

        ic_eye_close.setOnClickListener {
            passwordVisible = !passwordVisible

            if (passwordVisible) {
                txt_loginpassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ic_eye_close.setImageResource(R.drawable.ic_eye)
            } else {
                txt_loginpassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ic_eye_close.setImageResource(R.drawable.ic_eye_close)
            }
            txt_loginpassword.setSelection(txt_loginpassword.text.length)

        }


        btn_register.setOnClickListener {

            // Cerrar teclado automáticamente
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            val correo = txt_logincorreo.text.toString()
            val password = txt_loginpassword.text.toString()
            val dni = txt_dni.text.toString()
            val primernombre = txt_primernombre.text.toString()
            val segundonombre = txt_segundonombre.text.toString()
            val primerapellido = txt_primerapellido.text.toString()
            val segundoapellido = txt_segundoapellido.text.toString()

            // 24062025 - EEP - Creando una array de forma local
            val campos = listOf(
                txt_primernombre.text.toString(),
                txt_segundonombre.text.toString(),
                txt_primerapellido.text.toString(),
                txt_segundoapellido.text.toString()
            )

            // 24062025 - EEP - Creando una variable para limpiar espacios
            val campovacio = campos.any() { it.trim().isEmpty() }

            if (campovacio) {
                Toast.makeText(this, "Por favor no deje espacios vacios!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            fun expresionregular(texto: String): Boolean {
                val regex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
                return regex.matches(texto)
            }

            if (!expresionregular(primernombre.trim())) {
                Toast.makeText(
                    this,
                    "Por favor no introducir emojis ni caracteres especiales",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (!expresionregular(segundonombre.trim())) {
                Toast.makeText(
                    this,
                    "Por favor no introducir emojis ni caracteres especiales",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (!expresionregular(primerapellido.trim())) {
                Toast.makeText(
                    this,
                    "Por favor no introducir emojis ni caracteres especiales",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (!expresionregular(segundoapellido.trim())) {
                Toast.makeText(
                    this,
                    "Por favor no introducir emojis ni caracteres especiales",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (correo.isEmpty() || password.isEmpty() || dni.isEmpty() || primernombre.isEmpty() ||
                segundonombre.isEmpty() || primerapellido.isEmpty() || segundoapellido.isEmpty()
            ) {
                Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_LONG).show()
            } else if (!correo.endsWith("@gmail.com") || !Patterns.EMAIL_ADDRESS.matcher(correo)
                    .matches()
            ) {
                Toast.makeText(this, "Por favor ingrese un gmail valido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if (primernombre.trim().length < 2) {
                Toast.makeText(
                    this,
                    "El primer nombre debe tener al menos 2 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (segundonombre.trim().length < 2) {
                Toast.makeText(
                    this,
                    "El segundo nombre debe tener al menos 2 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (primerapellido.trim().length < 2) {
                Toast.makeText(
                    this,
                    "El Apellido debe tener al menos 2 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (segundoapellido.trim().length < 2) {
                Toast.makeText(
                    this,
                    "El Apellido debe tener al menos 2 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (txt_telefono.text.toString().length < 9) {
                Toast.makeText(this, "Por favor ingrese 9 digitos", Toast.LENGTH_SHORT).show()
            } else if (txt_dni.text.toString().length < 8) {
                Toast.makeText(this, "Por favor ingrese 8 digitos", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Por favor ingresar contrasena mayor a 6 digitos",
                    Toast.LENGTH_LONG
                ).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Formato de correo incorrecto", Toast.LENGTH_LONG).show()
            } else {
                loadin_layout.visibility = View.VISIBLE
                Glide.with(this)
                    .asGif()
                    .load(R.raw.loading)
                    .into(iv_loading)
                if (esCuentaGoogle) {
                    firestore.collection("usuarios")
                        .document(dni)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val correoExistente = document.getString("correo")
                                if (correoExistente != correo) {
                                    loadin_layout.visibility = View.GONE
                                    Toast.makeText(this, "Este DNI ya está registrado con otro correo", Toast.LENGTH_LONG).show()
                                    btn_register.isEnabled = true
                                } else {
                                    // mismo correo
                                    guardarUsuarioFirestore(
                                        dni,
                                        correo,
                                        primernombre,
                                        segundonombre,
                                        primerapellido,
                                        segundoapellido
                                    ) {
                                        guardar_sesion(correo)
                                        val intent = Intent(this, HomeActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("Correo", correo)
                                        startActivity(intent)
                                        finish()
                                        loadin_layout.visibility = View.GONE
                                    }
                                }
                            } else {
                                // No existe ese DNI → registrar sin problema
                                guardarUsuarioFirestore(
                                    dni,
                                    correo,
                                    primernombre,
                                    segundonombre,
                                    primerapellido,
                                    segundoapellido
                                ) {
                                    guardar_sesion(correo)
                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("Correo", correo)
                                    startActivity(intent)
                                    finish()
                                    loadin_layout.visibility = View.GONE
                                }
                            }
                        }
                        .addOnFailureListener {
                            loadin_layout.visibility = View.GONE
                            Toast.makeText(this, "Error al verificar el DNI", Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    verificarDniYRegistrar(dni, correo, password)
                }
            }
        }

        btn_IngresarGoogle.setOnClickListener {
            Toast.makeText(this, "Iniciando login con Google...", Toast.LENGTH_SHORT).show()
            Log.d("LOGIN_GOOGLE", "Se presionó el botón")
            login_google()
        }

        tv_iniciarsesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Agregando el spinner a la lista
        val listadodocumentos = resources.getStringArray(R.array.documentos)

        val adaptadordocumentos = ArrayAdapter(this, R.layout.spinner_doc_color, listadodocumentos)
        sp_documentos.adapter = adaptadordocumentos

        sp_documentos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val seleccionado = parent?.getItemAtPosition(position).toString()

                txt_dni.isEnabled = seleccionado != "Seleccione"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        firestore = FirebaseFirestore.getInstance()
    }

    fun verificarDniYRegistrar(dni: String, correo: String, password: String) {
        firestore.collection("usuarios")
            .whereEqualTo("dni", dni)
            .get()
            .addOnSuccessListener { dniResult ->
                if (!dniResult.isEmpty) {
                    loadin_layout.visibility = View.GONE
                    Toast.makeText(this, "El DNI ya está registrado", Toast.LENGTH_LONG).show()
                    btn_register.isEnabled = true
                } else {
                    // Validar si el correo ya existe también
                    firestore.collection("usuarios")
                        .whereEqualTo("correo", correo)
                        .get()
                        .addOnSuccessListener { correoResult ->
                            if (!correoResult.isEmpty) {
                                iv_loading.visibility = View.GONE
                                Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_LONG)
                                    .show()
                                btn_register.isEnabled = true
                            } else {
                                btn_register_firebase(correo, password)
                            }
                        }
                        .addOnFailureListener {
                            btn_register.isEnabled = true
                            iv_loading.visibility = View.GONE
                            Toast.makeText(this, "Error al verificar correo", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                btn_register.isEnabled = true
                iv_loading.visibility = View.GONE
                Toast.makeText(this, "Error al verificar DNI", Toast.LENGTH_SHORT).show()
            }
    }

    fun btn_register_firebase(xemail: String, xpassword: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(xemail, xpassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    firebaseUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Verifica tu correo antes de iniciar sesión",
                                    Toast.LENGTH_LONG
                                ).show()

                                // Guardamos los datos en Firestore
                                val correo = firebaseUser.email.toString()
                                val dni = txt_dni.text.toString()
                                val primernombre = txt_primernombre.text.toString()
                                val segundonombre = txt_segundonombre.text.toString()
                                val primerapellido = txt_primerapellido.text.toString()
                                val segundoapellido = txt_segundoapellido.text.toString()
                                btn_register.isEnabled = true

                                guardarUsuarioFirestore(
                                    dni,
                                    correo,
                                    primernombre,
                                    segundonombre,
                                    primerapellido,
                                    segundoapellido
                                ) {
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                    loadin_layout.visibility = View.GONE
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "No se pudo enviar el correo de verificación",
                                    Toast.LENGTH_LONG
                                ).show()
                                loadin_layout.visibility = View.GONE
                            }
                        }

                } else {
                    btn_register.isEnabled = true
                    val error = task.exception
                    val mensaje = when {
                        error?.message?.contains("email address is already in use", ignoreCase = true) == true ->
                            "Este correo ya está en uso. Inicie sesión o use otro."
                        error?.message?.contains("badly formatted", ignoreCase = true) == true ->
                            "El formato del correo es inválido."
                        else ->
                            "Error al crear cuenta: ${error?.localizedMessage}"
                    }
                    Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
                    loadin_layout.visibility = View.GONE
                }
            }
    }
    fun login_google() {
        val credentialManager = CredentialManager.create(this)

        val signInWithGoogleOption =
            GetSignInWithGoogleOption.Builder(getString(R.string.web_cliente))
                .setNonce("nonce") // Opcional, puedes eliminarlo si no lo necesitas
                .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        lifecycleScope.launch {
            try {
                Log.d("LOGIN_GOOGLE", "Intentando obtener credencial...")
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@RegisterActivity
                )
                Log.d("LOGIN_GOOGLE", "Credencial obtenida: ${result.credential}")
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.e("LOGIN_GOOGLE", "Error al obtener la credencial", e)
            }
        }
    }


    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        Log.d("CREDENTIAL_TYPE", "Tipo de credential: ${credential::class.java.name}")

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val firebaseCredential = GoogleAuthProvider.getCredential(
                            googleIdTokenCredential.idToken, null
                        )

                        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val user = task.result.user
                                    val nombreCompleto = user?.displayName ?: ""
                                    val correo = user?.email ?: ""

                                    txt_logincorreo.setText(correo)

                                    val partes = nombreCompleto.split(" ")
                                    if (partes.size >= 2) {
                                        txt_primernombre.setText(partes[0])
                                        txt_primerapellido.setText(partes[1])
                                    } else {
                                        txt_primernombre.setText(nombreCompleto)
                                    }
                                    guardarUsuarioFirestore(
                                        dni = "pendiente",
                                        correo = correo,
                                        primernombre = partes.getOrNull(0) ?: "",
                                        segundonombre = "",
                                        primerapellido = partes.getOrNull(1) ?: "",
                                        segundoapellido = ""
                                    ){
                                    Toast.makeText(
                                        this,
                                        "Verifica y completa tus datos antes de continuar",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    }
                                }
                            }

                    } catch (e: GoogleIdTokenParsingException) {
                        Toast.makeText(applicationContext, "Token inválido: $e", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    // Aquí sí puedes usar else, porque es parte del if-else.
                    Toast.makeText(
                        applicationContext,
                        "Tipo de credencial no reconocido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        Log.d("LOGIN_GOOGLE", "Entrando a handleSignIn")
        Log.d("LOGIN_GOOGLE", "Tipo de credential: ${credential::class.java.name}")
    }

    fun guardarUsuarioFirestore(
        dni: String,
        correo: String,
        primernombre: String,
        segundonombre: String,
        primerapellido: String,
        segundoapellido: String,
        onSuccess: () -> Unit
    ) {
        val usuario = hashMapOf(
            "dni" to dni,
            "correo" to correo,
            "primeronombre" to primernombre,
            "segundonombre" to segundonombre,
            "primerapellido" to primerapellido,
            "segundoapellido" to segundoapellido
        )

        val docRef = firestore.collection("usuarios").document(dni)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Ya existe un usuario con este DNI
                    val correoExistente = document.getString("correo")
                    if (correoExistente != correo) {
                        Toast.makeText(
                            applicationContext,
                            "Este DNI ya está registrado con otro correo.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Este usuario ya está registrado.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    loadin_layout.visibility = View.GONE
                } else {
                    // DNI nuevo → registrar
                    docRef.set(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(
                                applicationContext,
                                "Registro exitoso.",
                                Toast.LENGTH_SHORT
                            ).show()
                            onSuccess()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                "Error al registrar datos.",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadin_layout.visibility = View.GONE
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "Error al verificar el DNI.",
                    Toast.LENGTH_SHORT
                ).show()
                loadin_layout.visibility = View.GONE
            }
    }
    fun guardar_sesion(correo: String) {
        val guardar =
            getSharedPreferences(LoginActivity.Global.preferencias_compartidas, MODE_PRIVATE).edit()
        guardar.putString("Correo", correo)
        guardar.apply()
    }


}