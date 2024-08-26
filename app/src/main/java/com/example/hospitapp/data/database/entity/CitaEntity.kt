package com.example.hospitapp.data.database.entity

import androidx.room.*

@Entity
data class Usuario(
    @PrimaryKey val correo: String,
    val contrasena: String
)

@Entity
data class CitaPaciente(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nombrePaciente: String,
    var edad: Int,
    var departamento: String,
    var sexo: String,
    var fecha: String,
    var status: String,
    var numeroCita: Int
)
