package com.example.senva

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    object Global{
        var preferencias_compartidas = "sharedpreferences"
    }

    // 19062025 - EEP - Invocando allow los id de xml Login
    private lateinit var txt_logincorreo: EditText
    private lateinit var txt_loginpassword: EditText
    private lateinit var btn_ingresar: Button
    private lateinit var tv_extracorreo: TextView
    private lateinit var tv_crearcuenta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        verificar_sesion_abierta()
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
        AgregarReferencia()
    }

    fun AgregarReferencia(){
        txt_logincorreo = findViewById<EditText>(R.id.txtlogincorreo)
        txt_loginpassword = findViewById<EditText>(R.id.txtloginpassword)
        btn_ingresar = findViewById<Button>(R.id.btnIngresar)
        tv_crearcuenta = findViewById<TextView>(R.id.tvcrearcuenta)

        btn_ingresar.setOnClickListener {
            // 19062025 - EEP - Verificacion de contraseña
            if (txt_loginpassword.text.toString() != ""){
                // 19062025 - EEP - Verificacion de formato de Correo con "EMAIL_ADDRESS"
                if (txt_logincorreo.text.toString().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(txt_logincorreo.text.toString()).matches()){
                    login_firebase(txt_logincorreo.text.toString(),txt_loginpassword.text.toString())
                }else{
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
    }

    fun login_firebase(xemail: String,xpassword: String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(xemail, xpassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    guardar_sesion(task.result.user?.email.toString())
                    var intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("Correo",task.result.user?.email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext,"Correo o Contrasena Incorrecta", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun verificar_sesion_abierta(){
        var sesion_abierta: SharedPreferences = getSharedPreferences(Global.preferencias_compartidas, MODE_PRIVATE)

        var correo = sesion_abierta.getString("Correo",null)

        if (correo != null){
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("Correo",correo)
            startActivity(intent)
            finish()
        }
    }

    fun guardar_sesion(correo: String){
        var guardar_sesion: SharedPreferences.Editor = getSharedPreferences(Global.preferencias_compartidas, MODE_PRIVATE).edit()
        guardar_sesion.putString("Correo",correo)
        guardar_sesion.apply()
    }

}