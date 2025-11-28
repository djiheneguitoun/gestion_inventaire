package com.example.elbiometria.data

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class CsvManager(private val context: Context) {

    fun getFileName(dossier: String, exercice: String): String {
        return "inventaire_${dossier}_${exercice}.txt"
    }


    fun appendInventoryItem(item: InventoryItem, dossier: String, exercice: String) {
        val fileName = getFileName(dossier, exercice)
        val file = File(context.filesDir, fileName)

        try {
            FileOutputStream(file, true).use { fos ->
                OutputStreamWriter(fos, Charsets.UTF_8).use { writer ->
                    writer.append(item.toCsvLine())
                    writer.append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun deleteAllItems(dossier: String, exercice: String) {
        val file = getFile(dossier, exercice)
        if (file.exists()) {
            file.delete()
        }
    }
    fun readAllInventoryItems(dossier: String, exercice: String): List<String> {
        val fileName = getFileName(dossier, exercice)
        val file = File(context.filesDir, fileName)

        return if (file.exists()) {
            try {
                file.readLines()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun getFile(dossier: String, exercice: String): File {
        val fileName = getFileName(dossier, exercice)
        return File(context.filesDir, fileName)
    }
}
