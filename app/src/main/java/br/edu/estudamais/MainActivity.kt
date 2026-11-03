package br.edu.estudamais

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.estudamais.ui.navigation.EstudaMaisApp
import br.edu.estudamais.ui.theme.EstudaMaisTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { EstudaMaisTheme { EstudaMaisApp() } }
    }
}
