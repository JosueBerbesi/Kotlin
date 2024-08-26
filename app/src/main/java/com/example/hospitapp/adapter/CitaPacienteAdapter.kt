package com.example.hospitapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hospitapp.data.database.entity.CitaPaciente
import com.example.hospitapp.databinding.ItemCitapacienteBinding
import com.example.hospitapp.ui.CitaPacienteViewHolder

class CitaPacienteAdapter(
    var citaPaciente: MutableList<CitaPaciente>,
    private val onOption1Click: (Int) -> Unit,
    private val onOption2Click: (Int) -> Unit
) : RecyclerView.Adapter<CitaPacienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaPacienteViewHolder {
        val binding =
            ItemCitapacienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitaPacienteViewHolder(binding)
    }

    override fun getItemCount(): Int = citaPaciente.size

    override fun onBindViewHolder(holder: CitaPacienteViewHolder, position: Int) {
        val cita = citaPaciente[position]
        holder.render(cita, position, onOption1Click, onOption2Click)
    }

    fun updateData(nuevasCitas: List<CitaPaciente>) {
        citaPaciente = nuevasCitas.toMutableList()
        notifyDataSetChanged()
    }

    fun updateCita(position: Int, updatedCitaPaciente: CitaPaciente) {
        citaPaciente[position] = updatedCitaPaciente
        notifyDataSetChanged()
    }
}
