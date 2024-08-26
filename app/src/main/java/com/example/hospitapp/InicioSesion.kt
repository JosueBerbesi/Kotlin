package com.example.hospitapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.hospitapp.data.database.datab.AppDatabase
import com.example.hospitapp.data.database.entity.Usuario
import com.example.hospitapp.databinding.ActivityInicioSesionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InicioSesion : AppCompatActivity() {

    private lateinit var binding: ActivityInicioSesionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textBlack()

        // Obtener el valor de la variable que se envió desde el MainActivity
        val value: Int? = intent.extras?.getInt("value")

        // Al iniciar el layout, 1 = invisible, 2 = visible
        Utilidades.linearC(value, binding.liner1, binding.liner2)

        // Presionar los TextView
        binding.tvRegis.setOnClickListener {
            Utilidades.cambiarVisibilidad(
                binding.liner1,
                binding.liner2
            )
        }
        binding.tvIniciar1.setOnClickListener {
            Utilidades.cambiarVisibilidad(
                binding.liner2,
                binding.liner1
            )
        }

        // Iniciar sesión
        binding.btnLogin1.setOnClickListener {
            val correo = binding.edCorreo.text.toString()
            val contrasena = binding.edCont.text.toString()
            ingresar(correo, contrasena)
        }

        // Registrarse
        binding.btnLogin2.setOnClickListener {
            val correo = binding.edCorreo2.text.toString()
            val contrasena = binding.edCont2.text.toString()
            val confirmada = binding.edCont3.text.toString()
            registrar(correo, contrasena, confirmada)
        }
    }


    private fun registrar(correo: String, contrasena: String, confirmada: String) {
        if (correo.isEmpty() || contrasena.isEmpty() || confirmada.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
        } else if (contrasena != confirmada) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        } else {
            registrarDatos(correo, contrasena)
        }
    }

    private fun registrarDatos(correo: String, contrasena: String) {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()

        val usuarioDao = db.usuarioDao()
        val usuario = Usuario(correo = correo, contrasena = contrasena)

        // Usa lifecycleScope para lanzar una corrutina en el contexto del ciclo de vida de la actividad
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val usuarioExistente = usuarioDao.getUsuarioByCorreo(correo)
                withContext(Dispatchers.Main) {
                    if (usuarioExistente != null) {
                        Toast.makeText(
                            this@InicioSesion,
                            "El correo ya está registrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        withContext(Dispatchers.IO) {
                            usuarioDao.insertUsuario(usuario)
                            val usuarioGuardado = usuarioDao.getUsuarioByCorreo(correo)
                            withContext(Dispatchers.Main) {
                                if (usuarioGuardado != null) {
                                    Toast.makeText(
                                        this@InicioSesion,
                                        "Registro exitoso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@InicioSesion, Menu::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@InicioSesion,
                                        "Error en el registro",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
        db.close()
    }

    private fun ingresar(correo: String, contrasena: String) {
        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            IngresarDatos(correo, contrasena)
        }
    }

    private fun IngresarDatos(correo: String, contrasena: String) {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()

        val usuarioDao = db.usuarioDao()
        // Usa lifecycleScope para lanzar una corrutina en el contexto del ciclo de vida de la actividad
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val usuarioExistente = usuarioDao.getUsuarioRCorreo(correo, contrasena)
                withContext(Dispatchers.Main) {
                    if (usuarioExistente != null) {
                        Toast.makeText(this@InicioSesion, "Bienvenido", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@InicioSesion, Menu::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            this@InicioSesion,
                            "No esta Registrado, de click en Registrarse",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                }
            }
        }


    }


    private fun textBlack() {
        binding.edCorreo.text.clear()
        binding.edCont.text.clear()
        binding.edCorreo2.text.clear()
        binding.edCont2.text.clear()
        binding.edCont3.text.clear()
    }

    class Utilidades {
        companion object {
            fun cambiarVisibilidad(liner1: View, liner2: View) {
                liner1.visibility = View.INVISIBLE
                liner2.visibility = View.VISIBLE
            }

            fun linearC(value: Int?, liner1: View, liner2: View) {
                if (value != 1) {
                    cambiarVisibilidad(liner1, liner2)
                } else {
                    cambiarVisibilidad(liner2, liner1)
                }
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        // Redirigir a la pantalla principal (MainActivity)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}
