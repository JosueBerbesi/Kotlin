package com.example.hospitapp.customDialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.hospitapp.R
import com.example.hospitapp.data.database.datab.AppDatabase
import com.example.hospitapp.data.database.entity.CitaPaciente
import com.example.hospitapp.databinding.EditCitaPacienteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomEdit(
    private val onSubmitClickListener: (Int, CitaPaciente, Any?) -> Unit,
    private val citaPacienteid: Int,
    private val position: Int
) : DialogFragment() {

    private lateinit var binding: EditCitaPacienteBinding
    private val db by lazy {
        Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = EditCitaPacienteBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        // Configurar el adaptador para los spinners con los estilos personalizados
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sexo_array,
            R.layout.spinner_item // Usar layout específico para el spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnersexPaciente.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.departamentoCita_array,
            R.layout.spinner_item // Usar layout específico para el spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnerDepartamentoPaciente.adapter = adapter
        }

        val citaPacienteDao = db.citaPacienteDao()
        lifecycleScope.launch {
            val citaPaciente = withContext(Dispatchers.IO) {
                citaPacienteDao.getCitaPacienteById(citaPacienteid)
            }
            if (citaPaciente != null) {
                val nombre = citaPaciente.nombrePaciente
                val edad = citaPaciente.edad.toString()
                val sexo = citaPaciente.sexo
                val departamento = citaPaciente.departamento

                binding.nombrePaciente.setText(nombre)
                binding.edadPaciente.setText(edad)
                when (sexo) {
                    "M" -> binding.spinnersexPaciente.setSelection(2)
                    "F" -> binding.spinnersexPaciente.setSelection(1)
                }
                when (departamento) {
                    "Odontología" -> binding.spinnerDepartamentoPaciente.setSelection(1)
                    "Posparto" -> binding.spinnerDepartamentoPaciente.setSelection(2)
                    "Emergencia" -> binding.spinnerDepartamentoPaciente.setSelection(3)
                    "Consulta General" -> binding.spinnerDepartamentoPaciente.setSelection(4)
                }
            } else {
                Log.e(ContentValues.TAG, "No se encontró la cita con el id: $citaPacienteid")
            }
        }

        binding.checked.setOnClickListener {



            val nombre = binding.nombrePaciente.text.toString()
            val edad = binding.edadPaciente.text.toString()
            val sexo = binding.spinnersexPaciente.selectedItem.toString()
            val departamento = binding.spinnerDepartamentoPaciente.selectedItem.toString()
            val fechaHoy =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()


            if (nombre.isNotEmpty() && edad.isNotEmpty() && sexo != "Sexo" && departamento != "Departamento") {
                if (edad.toInt() in 1..120) {
                    lifecycleScope.launch {
                        val citaPaciente = withContext(Dispatchers.IO) {
                            citaPacienteDao.getCitaPacienteById(citaPacienteid)
                        }
                        lifecycleScope.launch {
                            var numeroCita = withContext(Dispatchers.IO) {
                                citaPacienteDao.getLastCitaNumero(departamento, fechaHoy) ?: 0

                            }
                            numeroCita += 1

                            if (citaPaciente != null) {
                                citaPaciente.nombrePaciente = nombre
                                citaPaciente.edad = edad.toInt()
                                citaPaciente.sexo = if (sexo == "Masculino") "M" else "F"
                                citaPaciente.departamento = departamento
                                citaPaciente.fecha = fechaHoy
                                citaPaciente.numeroCita = numeroCita

                                withContext(Dispatchers.IO) {
                                    citaPacienteDao.updateCitaPaciente(citaPaciente)

                                }
                                withContext(Dispatchers.Main) {
                                    onSubmitClickListener.invoke(
                                        position,
                                        citaPaciente,
                                        departamento
                                    )
                                    recargarFragmento()
                                    dismiss()
                                }
                            } else {
                                Log.e(
                                    ContentValues.TAG,
                                    "No se encontró la cita con el id: $citaPacienteid"
                                )
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "La edad máxima es de 120 años y la mínima es 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Debe rellenar todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setOnShowListener {
            val window = dialog.window
            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val params = window?.attributes
            params?.gravity = Gravity.CENTER
            window?.attributes = params
        }

        return dialog
    }

    private fun recargarFragmento() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.detach(this).attach(this).commit()
    }
}
