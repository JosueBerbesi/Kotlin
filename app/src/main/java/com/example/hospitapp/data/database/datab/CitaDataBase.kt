package com.example.hospitapp.data.database.datab

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hospitapp.data.database.dao.CitaPacienteDao
import com.example.hospitapp.data.database.dao.UsuarioDao
import com.example.hospitapp.data.database.entity.CitaPaciente
import com.example.hospitapp.data.database.entity.Usuario

@Database(entities = [Usuario::class, CitaPaciente::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun citaPacienteDao(): CitaPacienteDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
