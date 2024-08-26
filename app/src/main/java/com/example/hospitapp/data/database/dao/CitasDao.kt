package com.example.hospitapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hospitapp.data.database.entity.CitaPaciente
import com.example.hospitapp.data.database.entity.Usuario

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuario(usuario: Usuario)

    @Query("SELECT * FROM Usuario")
    fun getAllUsuarios(): List<Usuario>

    @Query("SELECT * FROM Usuario WHERE correo = :correo")
    fun getUsuarioByCorreo(correo: String): Usuario?

    @Query("SELECT * FROM Usuario WHERE correo = :correo And contrasena = :contrasena")
    fun getUsuarioRCorreo(correo: String, contrasena: String): Usuario?
}


@Dao
interface CitaPacienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCitaPaciente(citaPaciente: CitaPaciente)

    @Update
    fun updateCitaPaciente(citaPaciente: CitaPaciente)

    @Query("SELECT * FROM CitaPaciente WHERE id = :id LIMIT 1")
    fun getCitaPacienteById(id: Int): CitaPaciente?

    @Query("SELECT * FROM CitaPaciente ORDER BY numeroCita ASC")
    suspend fun getAllCitasPacientes(): List<CitaPaciente>


    @Query("SELECT numeroCita FROM CitaPaciente WHERE departamento = :departamento AND fecha = :fecha ORDER BY numeroCita DESC LIMIT 1")
    fun getLastCitaNumero(departamento: String, fecha: String): Int?


}
