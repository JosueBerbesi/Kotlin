package com.example.hospitapp.fragment


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.hospitapp.MainActivity
import com.example.hospitapp.adapter.CitaPacienteAdapter
import com.example.hospitapp.data.database.datab.AppDatabase
import com.example.hospitapp.data.database.entity.CitaPaciente
import com.example.hospitapp.databinding.FragmentHistorialBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!
    private var departamentoSeleccionado: String? = null
    private var fechaSeleccionada =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            .toString() // Variable para almacenar la fecha seleccionada

    // Lista de citas de pacientes
    private val listaCitasPaciente = mutableListOf<CitaPaciente>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño del fragmento
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fechaHistorialFragment.text = fechaSeleccionada

        // Configurar los botones inferiores
        configureBottomButtons()

        // Inicializar el RecyclerView
        configureRecyclerView()

        // Configurar el evento de selección de fecha
        configureDateSelection()

        // Cargar las citas desde la base de datos
        cargarCitasDesdeBaseDeDatos()
    }


    private fun configureDateSelection() {
        // Configurar la selección de fecha
        binding.iconoCalendario.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                    fechaSeleccionada =
                        dateFormat.format(calendar.time) // Establecer la fecha seleccionada
                    binding.fechaHistorialFragment.text =
                        fechaSeleccionada// Actualizar el TextView con la fecha seleccionada
                    val departamentoSeleccionado = binding.spinnerHistorial.selectedItem.toString()
                    // Actualizar el RecyclerView con la fecha seleccionada
                    updateRecyclerView(departamentoSeleccionado, fechaSeleccionada)

                }
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            com.example.hospitapp.R.array.departamento_array,
            com.example.hospitapp.R.layout.spinner_item
        )

        adapter.setDropDownViewResource(com.example.hospitapp.R.layout.spinner_dropdown_item)
        binding.spinnerHistorial.adapter = adapter

        // Listener para detectar cambios en el spinner
        binding.spinnerHistorial.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    departamentoSeleccionado = parent.getItemAtPosition(position).toString()
                    // Actualizar el RecyclerView basado en la selección

                    val fechaSeleccionada = binding.fechaHistorialFragment.text

                    updateRecyclerView(departamentoSeleccionado, fechaSeleccionada.toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No hacer nada
                }
            }
        updateRecyclerView(departamentoSeleccionado, fechaSeleccionada)

    }

    private fun updateRecyclerView(departamento: String?, fechaSeleccionada: String) {
        // Filtrar la lista de citas de pacientes por el departamento seleccionado y la fecha seleccionada
        val status = "Finalizado"
        val citasFiltradas = listaCitasPaciente.filter {
            it.departamento == departamento && it.fecha == fechaSeleccionada && it.status == status
        }
        (binding.Reciclehistorial.adapter as CitaPacienteAdapter).updateData(citasFiltradas)
    }

    private fun configureBottomButtons() {
        // Configurar el click listener para el botón de ir a la actividad principal
        binding.off.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun configureRecyclerView() {
        // Configurar el RecyclerView con el adaptador de citas de pacientes y el administrador de diseño lineal
        binding.Reciclehistorial.apply {
            adapter = CitaPacienteAdapter(mutableListOf(),
                onOption1Click = { cita ->
                },
                onOption2Click = { cita ->
                }
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarCitasDesdeBaseDeDatos() {
        val db = Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()

        val citaPacienteDao = db.citaPacienteDao()

        lifecycleScope.launch {
            val citas = withContext(Dispatchers.IO) {
                citaPacienteDao.getAllCitasPacientes()
            }
            listaCitasPaciente.clear()
            listaCitasPaciente.addAll(citas)
            binding.Reciclehistorial.adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {

        // Liberar la referencia al binding cuando el fragmento se destruye
        _binding = null
        super.onDestroyView()
    }
}

