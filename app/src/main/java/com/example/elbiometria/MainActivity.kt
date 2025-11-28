package com.example.elbiometria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.elbiometria.ui.theme.MyApplicationTheme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.elbiometria.data.CsvManager
import com.example.elbiometria.data.PreferencesManager
import com.example.elbiometria.navigation.NavGraph

class MainActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var csvManager: CsvManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(this)
        csvManager = CsvManager(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var exercice by remember { mutableStateOf(preferencesManager.exercice) }
                    var dossier by remember { mutableStateOf(preferencesManager.dossier) }
                    var isFirstLaunch by remember { mutableStateOf(preferencesManager.isFirstLaunch) }

                    val navController = rememberNavController()

                    NavGraph(
                        navController = navController,
                        exercice = exercice,
                        dossier = dossier,
                        isFirstLaunch = isFirstLaunch,
                        csvManager = csvManager,
                        onExerciceChange = { newExercice ->
                            exercice = newExercice
                            preferencesManager.exercice = newExercice
                        },
                        onDossierChange = { newDossier ->
                            dossier = newDossier
                            preferencesManager.dossier = newDossier
                        },
                        onFirstLaunchComplete = {
                            isFirstLaunch = false
                            preferencesManager.isFirstLaunch = false
                        }
                    )
                }
            }
        }
    }
}
