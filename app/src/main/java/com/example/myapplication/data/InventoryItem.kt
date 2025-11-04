package com.example.myapplication.data
data class InventoryItem(
    val article: String,
    val etat: String,
    val loc1: String,
    val loc2: String,
    val loc3: String,
    val loc4: String,
    val status: String = "S",
    val date: String,
    val heure: String
) {
    fun toCsvLine(): String {
        return "$article,$etat,$loc1,$loc2,$loc3,$loc4,$status,$date,$heure"
    }
}
