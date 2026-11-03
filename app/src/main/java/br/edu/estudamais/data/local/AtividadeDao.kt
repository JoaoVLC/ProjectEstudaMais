package br.edu.estudamais.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AtividadeDao {
    @Query("SELECT * FROM atividades ORDER BY prazoDia ASC, id DESC")
    fun observarTodas(): Flow<List<AtividadeEntity>>
    @Query("SELECT * FROM atividades WHERE id = :id")
    fun observarPorId(id: Long): Flow<AtividadeEntity?>
    @Query("SELECT * FROM atividades WHERE id = :id")
    suspend fun buscarPorId(id: Long): AtividadeEntity?
    @Insert suspend fun inserir(atividade: AtividadeEntity): Long
    @Update suspend fun atualizar(atividade: AtividadeEntity): Int
    @Query("UPDATE atividades SET concluida = :concluida WHERE id = :id")
    suspend fun alterarConclusao(id: Long, concluida: Boolean): Int
    @Query("DELETE FROM atividades WHERE id = :id")
    suspend fun excluir(id: Long): Int
}
