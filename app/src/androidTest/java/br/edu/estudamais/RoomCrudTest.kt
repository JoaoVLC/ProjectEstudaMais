package br.edu.estudamais

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.edu.estudamais.data.local.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomCrudTest {
    @Test fun crudEPersistenciaAoReabrirBanco() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val nome = "somente-teste-estudamais.db"
        context.deleteDatabase(nome)
        fun abrir() = Room.databaseBuilder(context, EstudaMaisDatabase::class.java, nome).build()
        var db = abrir()
        try {
            val a = AtividadeEntity(titulo = "Prova", disciplina = "Mobile", descricao = "", prazoDia = 20_000,
                prioridade = "ALTA", concluida = false)
            val id1 = db.atividadeDao().inserir(a)
            val id2 = db.atividadeDao().inserir(a.copy(titulo = "Trabalho"))
            assertTrue(id1 > 0 && id2 > id1)
            assertEquals(2, db.atividadeDao().observarTodas().first().size)
            db.atividadeDao().atualizar(a.copy(id = id1, titulo = "Editada"))
            db.atividadeDao().alterarConclusao(id1, true)
            db.atividadeDao().excluir(id2)
            db.close(); db = abrir()
            val lista = db.atividadeDao().observarTodas().first()
            assertEquals(1, lista.size); assertEquals("Editada", lista.single().titulo); assertTrue(lista.single().concluida)
        } finally { db.close(); context.deleteDatabase(nome) }
    }
}
