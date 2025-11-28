package com.example.elbiometria.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileInputStream

object FileUtils {
    fun copyFileToDownloads(context: Context, file: File): Boolean {
        return try {
            val fileName = file.name
            val mimeType = "text/plain"


            val resolver = context.contentResolver

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri: Uri? = resolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                )

                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        FileInputStream(file).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    values.clear()
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)

                    Log.d("FileUtils", "✅ File copied to Downloads via MediaStore")
                    true
                } else {
                    Log.e("FileUtils", "❌ Failed to create file in MediaStore")
                    false
                }
            } else {
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()

                val destinationFile = File(downloadsDir, fileName)
                file.copyTo(destinationFile, overwrite = true)

                Log.d("FileUtils", "✅ File copied to legacy Downloads path: ${destinationFile.path}")
                true
            }

        } catch (e: Exception) {
            Log.e("FileUtils", "❌ Error copying file: ${e.message}", e)
            false
        }
    }
}
