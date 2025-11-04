package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import com.example.myapplication.ui.components.CustomTextField
import com.example.myapplication.ui.components.PrimaryButton
import com.example.myapplication.ui.theme.*
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    exercice: String,
    dossier: String,
    isFirstLaunch: Boolean,
    onExerciceChange: (String) -> Unit,
    onDossierChange: (String) -> Unit,
    onFirstLaunchComplete: () -> Unit,
    onNavigateToInventaire: () -> Unit,
    onNavigateToMouvements: () -> Unit,
    onNavigateToExportation: () -> Unit
) {
    var showConfigDialog by remember { mutableStateOf(isFirstLaunch) }
    var tempExercice by remember { mutableStateOf(exercice) }
    var tempDossier by remember { mutableStateOf(dossier) }

    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val configuration = LocalConfiguration.current
    val screenWidthDpInt = configuration.screenWidthDp
    val screenHeightDpInt = configuration.screenHeightDp
    val screenWidth = screenWidthDpInt.dp
    val screenHeight = screenHeightDpInt.dp
    val density = LocalDensity.current

    // compute diagonal size in inches (1 dp = 1/160 in)
    val diagonalDp = remember(screenWidthDpInt, screenHeightDpInt) {
        sqrt((screenWidthDpInt * screenWidthDpInt + screenHeightDpInt * screenHeightDpInt).toDouble())
    }
    val screenInches = remember(diagonalDp) { diagonalDp / 160.0 }

    // responsive scaling factor tuned to very small screens (e.g. < 3.5")
    val scaleFactor = remember(screenWidthDpInt, screenHeightDpInt, screenInches) {
        when {
            screenInches < 3.5 -> 0.65f     // very small handhelds (<= ~3.5")
            screenInches < 4.0 -> 0.75f     // small phones
            screenWidth < 320.dp -> 0.78f
            screenWidth < 360.dp -> 0.85f
            screenWidth < 400.dp -> 0.95f
            screenWidth < 600.dp -> 1f
            else -> 1.1f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding((24 * scaleFactor).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height((32 * scaleFactor).dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height((160 * scaleFactor).dp)
                        .width((160 * scaleFactor).dp)
                )

                Spacer(modifier = Modifier.height((48 * scaleFactor).dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy((16 * scaleFactor).dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedMenuButton(
                        text = "INVENTAIRE",
                        onClick = onNavigateToInventaire,
                        delay = 0
                    )

                    AnimatedMenuButton(
                        text = "MOUVEMENTS",
                        onClick = onNavigateToMouvements,
                        delay = 100
                    )

                    AnimatedMenuButton(
                        text = "EXPORTATION",
                        onClick = onNavigateToExportation,
                        delay = 200
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((12 * scaleFactor).dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = DeepOcean,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp)) // empêche le ripple de sortir
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = true,
                                color = DeepOcean.copy(alpha = 0.2f)
                            )
                        ) {
                            showConfigDialog = true
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((16 * scaleFactor).dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Exercice :",
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepOcean,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = exercice.ifBlank { "—" },
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepOcean,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = DeepOcean,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp)) // empêche le ripple de sortir
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = true,
                                color = DeepOcean.copy(alpha = 0.2f)
                            )
                        ) {
                            showConfigDialog = true
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((16 * scaleFactor).dp), // made responsive
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dossier :",
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepOcean,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = dossier.ifBlank { "—" },
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepOcean,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height((30 * scaleFactor).dp))
            }
        }

        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding((10 * scaleFactor).dp),
            containerColor = BrightCyan,
            contentColor = DeepOcean
        ) {
            Icon(Icons.Default.Add, contentDescription = "Configurer")
        }

        if (showConfigDialog) {
            AlertDialog(
                onDismissRequest = { if (!isFirstLaunch) showConfigDialog = false },
                title = {
                    Text(
                        text = if (isFirstLaunch) "Configuration Initiale" else "Modifier Configuration",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy((16 * scaleFactor).dp)) {
                        CustomTextField(
                            value = tempExercice,
                            onValueChange = { tempExercice = it },
                            label = "Exercice"
                        )
                        CustomTextField(
                            value = tempDossier,
                            onValueChange = { tempDossier = it },
                            label = "Dossier"
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onExerciceChange(tempExercice)
                            onDossierChange(tempDossier)
                            if (isFirstLaunch) {
                                onFirstLaunchComplete()
                            }
                            showConfigDialog = false
                        },
                        enabled = tempExercice.isNotBlank() && tempDossier.isNotBlank()
                    ) {
                        Text("Valider")
                    }
                },
                dismissButton = if (!isFirstLaunch) {
                    {
                        TextButton(onClick = { showConfigDialog = false }) {
                            Text("Annuler")
                        }
                    }
                } else null
            )
        }

        Text(
            text = "Tél : 0697277373 / 0770016832",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepOcean.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.6f)
                .padding(bottom = (8 * scaleFactor).dp)
        )
    }
}

@Composable
fun AnimatedMenuButton(
    text: String,
    onClick: () -> Unit,
    delay: Int
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    // compute a small local scale for the buttons so they shrink on very small screens
    val configuration = LocalConfiguration.current
    val diagDp = remember(configuration) {
        sqrt((configuration.screenWidthDp * configuration.screenWidthDp + configuration.screenHeightDp * configuration.screenHeightDp).toDouble())
    }
    val localInches = remember(diagDp) { diagDp / 160.0 }
    val localScale = remember(localInches, configuration.screenWidthDp) {
        when {
            localInches < 3.5 -> 0.68f
            localInches < 4.0 -> 0.80f
            configuration.screenWidthDp < 360 -> 0.88f
            else -> 1f
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) +
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600)
                )
    ) {
        var pressed by remember { mutableStateOf(false) }
        val scaleAnim by animateFloatAsState(
            targetValue = if (pressed) 0.95f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessHigh),
            label = "scale"
        )

        // keep the structure: PrimaryButton usage unchanged, but give it a responsive modifier
        PrimaryButton(
            text = text,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height((48 * localScale * scaleAnim).dp)
                .scale(1f), // scale animation applied through height factor to avoid clipping on tiny screens
            backgroundColor = DeepOcean,
            contentColor = Color.White
        )
    }
}