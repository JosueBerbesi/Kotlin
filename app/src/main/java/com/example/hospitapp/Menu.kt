package com.example.hospitapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hospitapp.customDialog.CustomCita
import com.example.hospitapp.databinding.ActivityMenuBinding
import com.example.hospitapp.fragment.CitasFragment
import com.example.hospitapp.fragment.HistorialFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Menu : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    // Declaración de variables
    private lateinit var binding: ActivityMenuBinding
    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { doubleBackToExitPressedOnce = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el diseño usando View Binding
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración inicial
        bindingConfig()
        // Configuracion de Floating Action Button
        floatingConfig()
    }

    private fun floatingConfig() {
        binding.fab.setOnClickListener {
            CustomCita { nombrePaciente, departamento, n ->
                recargarFragmentoDeCitas()
                val message =
                    "Nombre: $nombrePaciente\nDepartamento: $departamento \n Numero de Cita : $n"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


            }.show(supportFragmentManager, "CustomCita")

        }
    }
    private fun recargarFragmentoDeCitas() {

        val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as? CitasFragment
        if (fragment != null && fragment.isVisible) {
            fragment.recargarFragmento()
        }
    }


    private fun bindingConfig() {
        // Configurar la barra de navegación inferior
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
        // Inhabilitar el fondo de la barra de navegación
        binding.bottomNavigation.background = null
        // Inhabilitar el segundo icono en la barra de navegación
        binding.bottomNavigation.menu.getItem(1).isEnabled = false
        // Establecer el icono de cita como seleccionado por defecto
        binding.bottomNavigation.selectedItemId = R.id.ico_cita
        // Cargar el fragmento de citas por defecto
        loadFragment(CitasFragment())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Manejar las selecciones de elementos en la barra de navegación inferior
        when (item.itemId) {
            R.id.ico_cita -> {
                // Cargar el fragmento de citas
                loadFragment(CitasFragment())
                return true
            }

            R.id.ico_historial -> {
                // Cargar el fragmento de historial
                loadFragment(HistorialFragment())
                return true
            }
        }
        return false
    }

    private fun loadFragment(fragment: Fragment) {
        // Reemplazar el fragmento actual con el nuevo fragmento
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        // Forzar la recreación del fragmento de citas cada vez que vuelvas a él
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
        if (currentFragment is CitasFragment) {
            loadFragment(CitasFragment())
        }
    }

    override fun onBackPressed() {
        // Manejar el botón de retroceso
        if (doubleBackToExitPressedOnce) {
            // Si el botón de retroceso se presiona dos veces, ir a la actividad de inicio de sesión
            val intent = Intent(this, InicioSesion::class.java)
            intent.putExtra("value", 1)
            startActivity(intent)
            finish() // Cierra la actividad actual
            return
        }

        // Mostrar mensaje para presionar nuevamente para salir
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Presiona nuevamente para salir", Toast.LENGTH_SHORT).show()
        // Restablecer la variable después de 2 segundos
        handler.postDelayed(runnable, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Eliminar callbacks pendientes cuando la actividad se destruya
        handler.removeCallbacks(runnable)
    }
}
