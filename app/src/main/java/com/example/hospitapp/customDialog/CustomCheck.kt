package com.example.hospitapp.customDialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.hospitapp.data.database.datab.AppDatabase
import com.example.hospitapp.data.database.entity.CitaPaciente
import com.example.hospitapp.databinding.CheckCitaPacienteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomCheck(
    private val onSubmitClickListener: (Int, CitaPaciente, String) -> Unit, // Agregar String para el departamento
    private val citaPacienteid: Int,
    private val position: Int
) : DialogFragment() {

    private lateinit var binding: CheckCitaPacienteBinding
    private val db by lazy {
        Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = CheckCitaPacienteBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        binding.btnAccept.setOnClickListener {
            val citaPacienteDao = db.citaPacienteDao()

            lifecycleScope.launch {
                val citaPaciente = withContext(Dispatchers.IO) {
                    citaPacienteDao.getCitaPacienteById(citaPacienteid)
                }

                if (citaPaciente != null) {
                    citaPaciente.status = "Finalizado"

                    withContext(Dispatchers.IO) {
                        citaPacienteDao.updateCitaPaciente(citaPaciente)
                    }

                    val departamento = citaPaciente.departamento // Obtener el departamento

                    withContext(Dispatchers.Main) {
                        onSubmitClickListener.invoke(position, citaPaciente, departamento)
                        dismiss()
                    }
                } else {
                    Log.e(ContentValues.TAG, "No se encontró la cita con el id: $citaPacienteid")
                }
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
