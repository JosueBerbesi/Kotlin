package com.example.hospitapp.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.hospitapp.data.database.entity.CitaPaciente
import com.example.hospitapp.databinding.ItemCitapacienteBinding

class CitaPacienteViewHolder(private val binding: ItemCitapacienteBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(citaPaciente: CitaPaciente, position: Int, onOption1Click: (Int) -> Unit, onOption2Click: (Int) -> Unit) {
        binding.tvNombrePacienteto.text = citaPaciente.nombrePaciente
        binding.tvEdadPacienteto.text = citaPaciente.edad.toString()
        binding.tvDepartamento.text = citaPaciente.departamento
        binding.tvNPacienteto.text = citaPaciente.numeroCita.toString()
        binding.tvSexoPaciente.text = citaPaciente.sexo

        binding.check.setOnClickListener { onOption1Click(position) }
        binding.edit.setOnClickListener { onOption2Click(position) }
    }
}
