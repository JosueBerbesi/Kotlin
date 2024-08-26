package com.example.hospitapp.customDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
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
import com.example.hospitapp.databinding.TargetaRegistroBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomCita(
    private val onSubmitClickListener: (String, String, Int) -> Unit
) : DialogFragment() {

    private lateinit var binding: TargetaRegistroBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TargetaRegistroBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        // Configurar el adaptador para los spinners con los estilos personalizados
        val sexoAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sexo_array,
            R.layout.spinner_item // Usar layout específico para el spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnersexPaciente.adapter = adapter
        }

        val departamentoAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.departamentoCita_array,
            R.layout.spinner_item // Usar layout específico para el spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnerDepartamentoPaciente.adapter = adapter
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
                    // Crear una instancia de la base de datos
                    val db = Room.databaseBuilder(
                        requireContext().applicationContext,
                        AppDatabase::class.java, "app-database"
                    ).build()

                    // Obtener el DAO
                    val citaPacienteDao = db.citaPacienteDao()

                    // Verificar el numero de cita en la base de datos
                    lifecycleScope.launch {
                        var numeroCita = withContext(Dispatchers.IO) {
                            citaPacienteDao.getLastCitaNumero(departamento, fechaHoy) ?: 0

                        }
                        numeroCita += 1


                        val citaPaciente = CitaPaciente(
                            nombrePaciente = nombre,
                            edad = edad.toInt(),
                            sexo = if (sexo == "Masculino") "M" else "F",
                            departamento = departamento,
                            fecha = fechaHoy,
                            status = "Pendiente",
                            numeroCita = numeroCita
                        )
                        // Insertar la cita en la base de datos
                        withContext(Dispatchers.IO) {
                            citaPacienteDao.insertCitaPaciente(citaPaciente)
                        }
                        withContext(Dispatchers.Main) {

                            onSubmitClickListener.invoke(nombre, departamento, numeroCita)
                            dismiss()
                            db.close()

                        }
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "La edad Maxima es de 120 años y la minima 1",
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

}
