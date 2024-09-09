package com.example.meezan360.security.logger

import android.content.Context
import android.util.Log
import com.example.meezan360.security.EncryptionKeyStoreImpl
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class SecureLogger {

    companion object {
        private const val TAG = "SecureLogger"
        private val encryptionKeyStore = EncryptionKeyStoreImpl.instance

        fun init(context: Context) {
            encryptionKeyStore.setContext(context)
            encryptionKeyStore.loadKey()
        }

        fun logInfo(tag: String, message: String) {
            Log.i(tag, encryptMessage(message))
        }

        fun logDebug(tag: String, message: String) {
            Log.d(tag, encryptMessage(message))
        }

        fun logError(tag: String, message: String) {
            Log.e(tag, encryptMessage(message))
        }

        private fun encryptMessage(message: String): String {
            return encryptionKeyStore.encrypt(message) ?: "Encryption failed"
        }

        fun logDecrypted(tag: String, encryptedMessage: String) {
            Log.i(tag, decryptMessage(encryptedMessage))
        }

        private fun decryptMessage(encryptedMessage: String): String {
            return encryptionKeyStore.decrypt(encryptedMessage) ?: "Decryption failed"
        }


        fun captureLogcat(context: Context) {
            val logcatFile = File(context.filesDir, "logcat.txt")

            try {
                val process = Runtime.getRuntime().exec("logcat -d")
                val inputStream = process.inputStream
                val outputStream = FileOutputStream(logcatFile)

                inputStream.copyTo(outputStream)
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun encryptLogcatFile(context: Context) {
            val encryptionKeyStore = EncryptionKeyStoreImpl.instance
            encryptionKeyStore.setContext(context)
            encryptionKeyStore.loadKey()

            val logcatFile = File(context.filesDir, "logcat.txt")
            val encryptedFile = File(context.filesDir, "logcat_encrypted.txt")

            try {
                val fileContent = logcatFile.readText()
                val encryptedContent = encryptionKeyStore.encrypt(fileContent) ?: ""

                val outputStream = FileOutputStream(encryptedFile)
                outputStream.write(encryptedContent.toByteArray())
                outputStream.close()

                logcatFile.delete() // Optionally delete the original logcat file
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun processLogcat(context: Context) {
            captureLogcat(context)
            encryptLogcatFile(context)
        }
    }
}

