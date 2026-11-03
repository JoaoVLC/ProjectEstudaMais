package br.edu.estudamais.di

import android.content.Context
import androidx.room.Room
import br.edu.estudamais.data.local.AtividadeDao
import br.edu.estudamais.data.local.EstudaMaisDatabase
import br.edu.estudamais.data.repository.AtividadeRepository
import br.edu.estudamais.data.repository.RoomAtividadeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EstudaMaisDatabase =
        Room.databaseBuilder(context, EstudaMaisDatabase::class.java, "estudamais.db").build()

    @Provides
    fun provideDao(database: EstudaMaisDatabase): AtividadeDao = database.atividadeDao()

    @Provides @Singleton
    fun provideRepository(repository: RoomAtividadeRepository): AtividadeRepository = repository
}
