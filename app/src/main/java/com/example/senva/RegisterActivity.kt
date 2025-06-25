package com.example.senva

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
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
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
// firestore
import com.google.firebase.firestore.FirebaseFirestore


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

    // pantalla de carga
    private lateinit var iv_loading: ImageView

    // Layout carga
    private lateinit var loadin_layout: RelativeLayout

    // Mostrar password
    private lateinit var ic_eye_close: ImageView
    private var passwordVisible = false


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
    }

    fun AgregarReferencia() {
        txt_logincorreo = findViewById<EditText>(R.id.txtcrearemail)
        txt_loginpassword = findViewById<EditText>(R.id.txtcrearpassword)
        btn_register = findViewById<Button>(R.id.btnenviarregister)
        tv_iniciarsesion = findViewById<TextView>(R.id.tviniciarseionmedientotexto)
        txt_dni = findViewById<EditText>(R.id.txtdni)
        txt_telefono = findViewById<EditText>(R.id.txttelefono)

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

            if (passwordVisible){
                txt_loginpassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ic_eye_close.setImageResource(R.drawable.ic_eye)
            } else {
                txt_loginpassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ic_eye_close.setImageResource(R.drawable.ic_eye_close)
            }
            txt_loginpassword.setSelection(txt_loginpassword.text.length)

        }

        btn_register.setOnClickListener {
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
            val campovacio = campos.any(){it.trim().isEmpty()}

            if (campovacio){
                Toast.makeText(this, "Por favor no deje espacios vacios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fun expresionregular(texto: String): Boolean{
                val regex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
                return regex.matches(texto)
            }

            if (!expresionregular(primernombre.trim())){
                Toast.makeText(this, "Por favor no introducir emojis ni caracteres especiales", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!expresionregular(segundonombre.trim())){
                Toast.makeText(this, "Por favor no introducir emojis ni caracteres especiales", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!expresionregular(primerapellido.trim())){
                Toast.makeText(this, "Por favor no introducir emojis ni caracteres especiales", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!expresionregular(segundoapellido.trim())){
                Toast.makeText(this, "Por favor no introducir emojis ni caracteres especiales", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (correo.isEmpty() || password.isEmpty() || dni.isEmpty() || primernombre.isEmpty() ||
                segundonombre.isEmpty() || primerapellido.isEmpty() || segundoapellido.isEmpty()) {
                Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_LONG).show()
            } else if (!correo.endsWith("@gmail.com") || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                Toast.makeText(this, "Por favor ingrese un gmail valido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if(primernombre.trim().length < 2) {
                Toast.makeText(this, "El primer nombre debe tener al menos 2 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(segundonombre.trim().length < 2) {
                Toast.makeText(this, "El segundo nombre debe tener al menos 2 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(primerapellido.trim().length < 2) {
                Toast.makeText(this, "El Apellido debe tener al menos 2 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(segundoapellido.trim().length < 2) {
                Toast.makeText(this, "El Apellido debe tener al menos 2 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            else if(txt_telefono.text.toString().length < 9){
                Toast.makeText(this, "Por favor ingrese 9 digitos", Toast.LENGTH_SHORT).show()
            }
            else if(txt_dni.text.toString().length < 8){
                Toast.makeText(this, "Por favor ingrese 9 digitos", Toast.LENGTH_SHORT).show()
            }
            else if (password.length < 6){
                Toast.makeText(this, "Por favor ingresar contrasena mayor a 6 digitos", Toast.LENGTH_LONG).show()
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Formato de correo incorrecto", Toast.LENGTH_LONG).show()
            } else {
                loadin_layout.visibility = View.VISIBLE
                Glide.with(this)
                    .asGif()
                    .load(R.raw.loading)
                    .into(iv_loading)
                verificarDniYRegistrar(dni, correo, password)
            }
        }

        tv_iniciarsesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Agregando el spinner a la lista
        val listadodocumentos = resources.getStringArray(R.array.documentos)

        val adaptadordocumentos = ArrayAdapter(this,R.layout.spinner_doc_color,listadodocumentos)
        sp_documentos.adapter=adaptadordocumentos

        sp_documentos.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
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
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    iv_loading.visibility = View.GONE
                    Toast.makeText(this, "El DNI ya está registrado", Toast.LENGTH_LONG).show()
                } else {
                    // El DNI no existe aún, se puede registrar
                    btn_register_firebase(correo, password)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al verificar DNI", Toast.LENGTH_SHORT).show()
            }
    }


    fun btn_register_firebase(xemail: String,xpassword: String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(xemail, xpassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val correo = task.result.user?.email.toString()
                    val dni = txt_dni.text.toString()
                    val primernombre = txt_primernombre.text.toString()
                    val segundonombre = txt_segundonombre.text.toString()
                    val primerapellido = txt_primerapellido.text.toString()
                    val segundoapellido = txt_segundoapellido.text.toString()

                    guardarUsuarioFirestore(dni,correo,primernombre,segundonombre,primerapellido,segundoapellido)

                    guardar_sesion(correo)
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("Correo", correo)
                    startActivity(intent)
                    finish()
                    loadin_layout.visibility = View.GONE
                    Toast.makeText(applicationContext,"Cuenta Creada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext,"Contraseña corta o usuario ya existe", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun guardarUsuarioFirestore(
        dni: String,
        correo: String,
        primernombre: String,
        segundonombre: String,
        primerapellido: String,
        segundoapellido: String
    ) {
        val usuario = hashMapOf(
            "dni" to dni,
            "correo" to correo,
            "primeronombre" to primernombre,
            "segundonombre" to segundonombre,
            "primerapellido" to primerapellido,
            "segundoapellido" to segundoapellido
        )

        firestore.collection("usuarios")
            .add(usuario)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Datos registrados en Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                loadin_layout.visibility = View.GONE
                Toast.makeText(applicationContext, "Error al registrar en Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    fun guardar_sesion(correo: String){
        val guardar = getSharedPreferences(LoginActivity.Global.preferencias_compartidas, MODE_PRIVATE).edit()
        guardar.putString("Correo",correo)
        guardar.apply()
    }



}