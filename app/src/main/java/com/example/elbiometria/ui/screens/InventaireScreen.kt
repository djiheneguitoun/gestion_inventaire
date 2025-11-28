package com.example.elbiometria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.elbiometria.data.CsvManager
import com.example.elbiometria.data.InventoryItem
import com.example.elbiometria.ui.components.CustomTextField
import com.example.elbiometria.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventaireScreen(
    exercice: String,
    dossier: String,
    csvManager: CsvManager,
    onNavigateBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    var date by remember { mutableStateOf(getCurrentDate()) }
    var localisation by remember { mutableStateOf("") }
    var etat by remember { mutableStateOf("") }
    var article by remember { mutableStateOf("") }
    var inventoryLines by remember { mutableStateOf<List<String>>(emptyList()) }
    var showAllLines by remember { mutableStateOf(false) }
    var lastAddedLine by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        inventoryLines = csvManager.readAllInventoryItems(dossier, exercice)
    }

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


    Box(
        modifier = Modifier
            .fillMaxSize().background(color = LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        "INVENTAIRE",
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
                    .verticalScroll(rememberScrollState())
                    .padding((16* scaleFactor).dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                        Row(
                            modifier = Modifier.fillMaxWidth().padding((12 * scaleFactor).dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoChip(label = "Exercice", value = exercice)
                            InfoChip(label = "Dossier", value = dossier)
                        }

                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding((16* scaleFactor).dp),
                        verticalArrangement = Arrangement.spacedBy((12* scaleFactor).dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Date :",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGray
                            )
                            TextButton(onClick = { showDatePicker = true }) {
                                Text(
                                    date,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanBlue
                                )
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }


                        Text(
                            "Localisation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkGray
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy((8* scaleFactor).dp)
                        ) {
                            CustomTextField(
                                value = localisation,
                                onValueChange = { localisation = it },
                                label = "Localisation",
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                })
                            )

                        }



                        CustomTextField(
                            value = etat,
                            onValueChange = { etat = it },
                            label = "État",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(FocusDirection.Next)
                            })
                        )

                        CustomTextField(
                            value = article,
                            onValueChange = { article = it },
                            label = "Code à Barres"
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy((12* scaleFactor).dp)
                ) {
                    Button(
                        onClick = {
                            if (article.isNotBlank() && etat.isNotBlank()) {
                                val item = InventoryItem(
                                    dossier = dossier,
                                    codeBarre = article,
                                    etat = etat,
                                    dateHeure = "${date} ${getCurrentTime()}",
                                    localisation = localisation
                                )

                                csvManager.appendInventoryItem(item, dossier, exercice)
                                lastAddedLine = item.toCsvLine()
                                showAllLines = false

                                article = ""

                                showSuccessMessage = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen
                        ),                        contentPadding = PaddingValues(horizontal = 2.dp)

                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width((8* scaleFactor).dp))
                        Text("Valider", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            inventoryLines = csvManager.readAllInventoryItems(dossier, exercice)
                            showAllLines = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OceanBlue
                        ),                        contentPadding = PaddingValues(horizontal = 2.dp)

                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null)
                        Spacer(modifier = Modifier.width((8* scaleFactor).dp))
                        Text("Voir", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            csvManager.deleteAllItems(dossier, exercice)
                            inventoryLines = emptyList()
                            lastAddedLine = null
                            showAllLines = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed,
                            contentColor = SoftWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 2.dp)

                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width((4* scaleFactor).dp))

                        Text("Effacer", fontWeight = FontWeight.Bold)
                    }
                }

                if (lastAddedLine != null || showAllLines) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SoftWhite.copy(alpha = 0.95f)
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding((16* scaleFactor).dp)
                        ) {
                            if (showAllLines) {
                                Text(
                                    "Tous les enregistrements (${inventoryLines.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanBlue,
                                    modifier = Modifier.padding(bottom = (12* scaleFactor).dp)
                                )
                                inventoryLines.takeLast(10).reversed().forEach { line ->
                                    Text(
                                        line,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkGray,
                                        modifier = Modifier.padding(vertical = (4* scaleFactor).dp)
                                    )
                                }
                            } else if (lastAddedLine != null) {
                                Text(
                                    "Dernier enregistrement",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanBlue,
                                    modifier = Modifier.padding(bottom = (12* scaleFactor).dp)
                                )
                                Text(
                                    lastAddedLine!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkGray,
                                    modifier = Modifier.padding(vertical = (4* scaleFactor).dp)
                                )
                            }
                        }
                    }
                }

            }
        }



        if (showSuccessMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSuccessMessage = false
            }

            Snackbar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding((16* scaleFactor).dp),
                containerColor = SuccessGreen
            ) {
                Text("Article enregistré avec succès!", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrightCyan.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MediumGray,
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                color = OceanBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date())
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
