package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.myapplication.data.CsvManager
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.FileUtils
import java.io.File

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
    val screenWidth = configuration.screenWidthDp.dp
    val scaleFactor = remember(screenWidth) {
        when {
            screenWidth < 360.dp -> 0.8f
            screenWidth < 400.dp -> 0.9f
            screenWidth < 600.dp -> 1f
            else -> 1.1f
        }
    }

    LaunchedEffect(Unit) {
        recordCount = csvManager.readAllInventoryItems(dossier, exercice).size
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
                    .padding((24* scaleFactor).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((24* scaleFactor).dp)
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
                        modifier = Modifier.padding((32* scaleFactor).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size((64* scaleFactor).dp),
                            tint = DeepOcean
                        )

                        Text(
                            "Export de données",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepOcean,
                            textAlign = TextAlign.Center
                        )

                        Divider(color = LightGray)

                        Column(
                            verticalArrangement = Arrangement.spacedBy((8* scaleFactor).dp),
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
                            horizontalArrangement = Arrangement.spacedBy((12* scaleFactor).dp)
                        ) {
                            Button(
                                onClick = {
                                    val file = csvManager.getFile(dossier, exercice)
                                    if (file.exists() && recordCount > 0) {
                                        val success = FileUtils.copyFileToDownloads(context, file)
                                        if (success) {
                                            showSuccessMessage = true
                                        } else {
                                            showErrorMessage = true
                                        }
                                    } else {
                                        showErrorMessage = true
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape((16* scaleFactor).dp),
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
                                Spacer(modifier = Modifier.width((12* scaleFactor).dp))
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
                    .padding(16.dp),
                containerColor = SuccessGreen,
                contentColor = SoftWhite
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    .padding((16* scaleFactor).dp),
                containerColor = ErrorRed,
                contentColor = SoftWhite
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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


