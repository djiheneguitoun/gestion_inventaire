package com.example.elbiometria.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.elbiometria.data.CsvManager
import com.example.elbiometria.ui.theme.*
import kotlin.math.sqrt
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportationScreen(
    exercice: String,
    dossier: String,
    csvManager: CsvManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    var recordCount by remember { mutableStateOf(0) }
    val configuration = LocalConfiguration.current

    // use width and height dp ints to compute diagonal and detect very small screens
    val screenWidthDpInt = configuration.screenWidthDp
    val screenHeightDpInt = configuration.screenHeightDp
    val screenWidth = screenWidthDpInt.dp
    val screenHeight = screenHeightDpInt.dp

    // compute diagonal size in dp and convert to inches (1 inch = 160 dp)
    val diagonalDp = remember(screenWidthDpInt, screenHeightDpInt) {
        sqrt((screenWidthDpInt * screenWidthDpInt + screenHeightDpInt * screenHeightDpInt).toDouble())
    }
    val screenInches = remember(diagonalDp) { diagonalDp / 160.0 }

    // responsive scaling factor tuned to very small screens (e.g. < 3.5")
    val scaleFactor = remember(screenWidthDpInt, screenHeightDpInt, screenInches) {
        when {
            screenInches < 3.5 -> 0.5f     // very small handhelds (~3.5")
            screenInches < 4.0 -> 0.75f     // small phones
            screenWidth < 320.dp -> 0.78f
            screenWidth < 360.dp -> 0.85f
            screenWidth < 400.dp -> 0.95f
            screenWidth < 600.dp -> 1f
            else -> 1.1f
        }
    }

    LaunchedEffect(Unit) {
        recordCount = csvManager.readAllInventoryItems(dossier, exercice).size
    }
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            try {
                val file = csvManager.getFile(dossier, exercice)
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    file.inputStream().use { input -> input.copyTo(out) }
                }
                showSuccessMessage = true
            } catch (e: Exception) {
                e.printStackTrace()
                showErrorMessage = true
            }
        } else {
            // annulation par l'utilisateur
            showErrorMessage = true
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        "EXPORTATION",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = SoftWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOcean,
                    titleContentColor = SoftWhite
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding((24 * scaleFactor).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((24 * scaleFactor).dp)
            ) {
                Spacer(modifier = Modifier.weight(0.2f))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftWhite.copy(alpha = 0.95f)
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding((32 * scaleFactor).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy((20 * scaleFactor).dp)
                    ) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size((64 * scaleFactor).dp),
                            tint = DeepOcean
                        )




                        Column(
                            verticalArrangement = Arrangement.spacedBy((8 * scaleFactor).dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            InfoRow(label = "Fichier", value = csvManager.getFileName(dossier, exercice))
                            InfoRow(label = "Exercice", value = exercice)
                            InfoRow(label = "Dossier", value = dossier)
                            InfoRow(label = "Enregistrements", value = recordCount.toString())
                        }

                        Divider(color = LightGray)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy((12 * scaleFactor).dp)
                        ) {
                            Button(
                                onClick = {
                                    val file = csvManager.getFile(dossier, exercice)
                                    if (file.exists() && recordCount > 0) {
                                        val suggestedName = csvManager.getFileName(dossier, exercice)
                                        createDocumentLauncher.launch(suggestedName) // ouvre "Enregistrer sous..."
                                    } else {
                                        showErrorMessage = true
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height((56 * scaleFactor).dp),
                                shape = RoundedCornerShape((16 * scaleFactor).dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SuccessGreen,
                                    contentColor = SoftWhite
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 8.dp
                                )
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width((12 * scaleFactor).dp))
                                Text(
                                    "Télécharger",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        if (showSuccessMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showSuccessMessage = false
            }

            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding((16 * scaleFactor).dp),
                containerColor = SuccessGreen,
                contentColor = SoftWhite
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy((8 * scaleFactor).dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Text("Opération réussie!", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showErrorMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showErrorMessage = false
            }

            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding((16 * scaleFactor).dp),
                containerColor = ErrorRed,
                contentColor = SoftWhite
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy((8 * scaleFactor).dp)
                ) {
                    Icon(Icons.Default.Error, contentDescription = null)
                    Text("Aucune donnée à exporter!", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$label:",
            style = MaterialTheme.typography.bodyLarge,
            color = MediumGray,
            fontWeight = FontWeight.Medium
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = DarkGray,
            fontWeight = FontWeight.Bold
        )
    }
}