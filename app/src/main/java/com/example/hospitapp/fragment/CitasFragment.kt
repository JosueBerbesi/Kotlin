package com.example.hospitapp.fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.hospitapp.MainActivity
import com.example.hospitapp.R
import com.example.hospitapp.adapter.CitaPacienteAdapter
import com.example.hospitapp.customDialog.CustomCheck
import com.example.hospitapp.customDialog.CustomEdit
import com.example.hospitapp.data.database.datab.AppDatabase
import com.example.hospitapp.data.database.entity.CitaPaciente
import com.example.hospitapp.databinding.FragmentCitasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CitasFragment : Fragment() {

    private var _binding: FragmentCitasBinding? = null
    private val binding get() = _binding!!
    private var departamentoSeleccionado: String? = null
    private val db by lazy {
        Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()
    }

    // Lista de citas de pacientes
    private val listaCitasPaciente = mutableListOf<CitaPaciente>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño del fragmento
        _binding = FragmentCitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar los botones inferiores
        configureBottom()

        // Configurar el spinner
        configurarSpinner()

        // Inicializar el RecyclerView
        configurarRecyclerView()

        // Cargar datos desde la base de datos
        cargarCitasDesdeBaseDeDatos()
    }

    private fun configurarSpinner() {
        // Crear un adaptador para el spinner con las opciones proporcionadas
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.departamento_array,
            R.layout.spinner_item // Usar layout específico para el spinner
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerCita.adapter = adapter

        // Configurar el listener para detectar cambios en el spinner
        binding.spinnerCita.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                departamentoSeleccionado = parent.getItemAtPosition(position).toString()
                // Actualizar el RecyclerView basado en la selección
                actualizarRecyclerView(departamentoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
    }

    private fun cargarCitasDesdeBaseDeDatos() {
        val citaPacienteDao = db.citaPacienteDao()

        lifecycleScope.launch {
            val citas = withContext(Dispatchers.IO) {
                citaPacienteDao.getAllCitasPacientes()
            }
            listaCitasPaciente.clear()
            listaCitasPaciente.addAll(citas)
            binding.RecicleCitas.adapter?.notifyDataSetChanged()
        }
    }

    private fun actualizarRecyclerView(departamento: String?) {
        val status = "Pendiente"
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()
        // Filtrar la lista de citas de pacientes por el departamento seleccionado y el estado de la cita

        val citasFiltradas = listaCitasPaciente.filter {
            it.departamento == departamento && it.status == status && it.fecha == fechaHoy
        }
        (binding.RecicleCitas.adapter as CitaPacienteAdapter).updateData(citasFiltradas)
    }

    private fun configureBottom() {
        // Configurar el click listener para el botón de ir a la actividad principal
        binding.off0.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun configurarRecyclerView() {
        binding.RecicleCitas.apply {
            adapter = CitaPacienteAdapter(mutableListOf(),
                onOption1Click = { position ->
                    val adapter = binding.RecicleCitas.adapter as CitaPacienteAdapter
                    val cita = adapter.citaPaciente[position]
                    val id: Int = cita.id
                    updateDatacheck(id, position)
                },
                onOption2Click = { position ->
                    val adapter = binding.RecicleCitas.adapter as CitaPacienteAdapter
                    val cita = adapter.citaPaciente[position]
                    val id: Int = cita.id
                    EditCustomDialog(id, position)
                }
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateDatacheck(id: Int, position: Int) {
        try {
            val adapter = binding.RecicleCitas.adapter as CitaPacienteAdapter
            val cita = adapter.citaPaciente[position]
            if (id != null) {
                CustomCheck({ Position, updatedCita, departamento ->
                    adapter.updateCita(Position, updatedCita)
                    // Seleccionar el primer elemento en el spinner
                    binding.spinnerCita.setSelection(0)
                    recargarFragmento()
                }, id, position).show(childFragmentManager, "CustomEdit")
            } else {
                // Manejar el caso en que el ID es nulo
                Toast.makeText(requireContext(), "ID de cita nulo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en updateDatacheck: ${e.message}")
            Toast.makeText(requireContext(), "Error al actualizar la cita", Toast.LENGTH_SHORT).show()
        }
    }

    private fun EditCustomDialog(id: Int, position: Int) {
        CustomEdit({ Position, updatedCita, departamento ->
            val adapter = binding.RecicleCitas.adapter as CitaPacienteAdapter
            adapter.updateCita(Position, updatedCita)
            when (departamento) {
                "Odontología" -> binding.spinnerCita.setSelection(1)
                "Posparto" -> binding.spinnerCita.setSelection(2)
                "Emergencia" -> binding.spinnerCita.setSelection(3)
                "Consulta General" -> binding.spinnerCita.setSelection(4)
            }
            cargarCitasDesdeBaseDeDatos()
        }, id, position).show(childFragmentManager, "CustomEdit")
    }

    fun recargarFragmento() {
        cargarCitasDesdeBaseDeDatos()
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.detach(this).attach(this).commit()
        Toast.makeText(requireContext(), "Cita Actualizada", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        db.close()
        // Liberar la referencia al binding cuando el fragmento se destruye
        _binding = null
        super.onDestroyView()
    }
}
