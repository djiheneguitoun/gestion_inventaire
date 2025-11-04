package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.data.CsvManager
import com.example.myapplication.ui.screens.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Inventaire : Screen("inventaire")
    object Mouvements : Screen("mouvements")
    object Exportation : Screen("exportation")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    exercice: String,
    dossier: String,
    isFirstLaunch: Boolean,
    csvManager: CsvManager,
    onExerciceChange: (String) -> Unit,
    onDossierChange: (String) -> Unit,
    onFirstLaunchComplete: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = 1f
                )
            ) {
            HomeScreen(
                exercice = exercice,
                dossier = dossier,
                isFirstLaunch = isFirstLaunch,
                onExerciceChange = onExerciceChange,
                onDossierChange = onDossierChange,
                onFirstLaunchComplete = onFirstLaunchComplete,
                onNavigateToInventaire = { navController.navigate(Screen.Inventaire.route) },
                onNavigateToMouvements = { navController.navigate(Screen.Mouvements.route) },
                onNavigateToExportation = { navController.navigate(Screen.Exportation.route) }
            )}
        }

        composable(Screen.Inventaire.route) {
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = 1f
                )
            ) {
                InventaireScreen(
                    exercice = exercice,
                    dossier = dossier,
                    csvManager = csvManager,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Mouvements.route) {
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = 1f
                )
            ) {
            MouvementsScreen(
                onNavigateBack = { navController.popBackStack() }
            )}
        }

        composable(Screen.Exportation.route) {
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = 1f // 👈 désactive le zoom du texte
                )
            ) {
            ExportationScreen(
                exercice = exercice,
                dossier = dossier,
                csvManager = csvManager,
                onNavigateBack = { navController.popBackStack() }
            )}
        }
    }
}
