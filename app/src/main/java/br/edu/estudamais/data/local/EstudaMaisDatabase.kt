package br.edu.estudamais.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AtividadeEntity::class], version = 1, exportSchema = true)
abstract class EstudaMaisDatabase : RoomDatabase() {
    abstract fun atividadeDao(): AtividadeDao
}
