package com.example.elbiometria.data
data class InventoryItem(
    val dossier: String,       // ajouter le dossier
    val codeBarre: String,
    val etat: String,
    val dateHeure: String,
    val localisation: String
) {
    fun toCsvLine(): String {
        return "$dossier$codeBarre,$etat,$dateHeure,$localisation"
    }
}
