package com.example.hospitapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<AppCompatButton>(R.id.btn_login).setOnClickListener {
            // Iniciar la actividad InicioSesion
            val intent = Intent(this, InicioSesion::class.java)
            intent.putExtra("value", 1)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textView).setOnClickListener {
            // Iniciar la actividad InicioSesion/registrar
            val intent = Intent(this, InicioSesion::class.java)
            intent.putExtra("value", 2)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Cierra la aplicación cuando se presiona el botón "Atrás"
        finishAffinity()
    }



}

