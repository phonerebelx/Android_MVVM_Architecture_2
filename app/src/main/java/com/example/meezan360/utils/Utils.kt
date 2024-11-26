package com.example.meezan360.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Utils {

    companion object {
        private val CIPHER_NAME = "AES/CBC/PKCS5PADDING"
        private val CIPHER_KEY_LEN = 16 //128 bits
        private val CIPHER_NAME_NEW = "AES/GCM/NoPadding"
        private val alias = "device_id_key"


        fun encryptPass(key: String, iv: String, data: String): String? {
            var key = key
            try {
                if (key.length < CIPHER_KEY_LEN) {
                    val numPad: Int = CIPHER_KEY_LEN - key.length
                    for (i in 0 until numPad) {
                        key += "0" //0 pad to len 16 bytes
                    }
                } else if (key.length > CIPHER_KEY_LEN) {
                    key = key.substring(0, CIPHER_KEY_LEN) //truncate to 16 bytes
                }
                val initVector = IvParameterSpec(iv.toByteArray(charset("UTF-8")))
                val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
                val cipher: Cipher = Cipher.getInstance(CIPHER_NAME)
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initVector)
                val encryptedData: ByteArray = cipher.doFinal(data.toByteArray())
                val base64_EncryptedData: String =
                    Base64.encodeToString(encryptedData, Base64.NO_WRAP)
                val base64_IV: String =
                    Base64.encodeToString(iv.toByteArray(charset("UTF-8")), Base64.NO_WRAP)

                return "$base64_EncryptedData:$base64_IV"
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return null
        }

        fun encryptPassNew(plainText: String): String {
            try {
                val key = "1234567890123456"
                val secretKey = SecretKeySpec(key.toByteArray(), "AES")
                val iv = ByteArray(12)
                SecureRandom().nextBytes(iv)
                val gcmSpec = GCMParameterSpec(128, iv)

                val cipher = Cipher.getInstance(CIPHER_NAME_NEW)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

                val cipherTextWithTag = cipher.doFinal(plainText.toByteArray())
                val cipherText =
                    cipherTextWithTag.copyOfRange(0, cipherTextWithTag.size - 16) // Ciphertext
                val tag = cipherTextWithTag.copyOfRange(
                    cipherTextWithTag.size - 16,
                    cipherTextWithTag.size
                )

                val encodedIV = java.util.Base64.getEncoder().encodeToString(iv)
                val encodedCipherText = java.util.Base64.getEncoder().encodeToString(cipherText)
                val encodedTag = java.util.Base64.getEncoder().encodeToString(tag)

                return "$encodedCipherText:$encodedIV:$encodedTag"
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }

        }

        fun parseColorSafely(colorString: String?): Int {
            if (!TextUtils.isEmpty(colorString)) {
                try {
                    return Color.parseColor(colorString)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return Constants.defaultColor
        }

        @SuppressLint("HardwareIds")
        fun getDeviceId(context: Context): String {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }

        //for fingerprint work


        //secure shared preference


        fun encryptedSharedPref(context: Context, data: String): String {
            val ENCRYPTION_KEY = "Ytr0ngS3cur3K3y!"

            val (iv, encryptedData) = encryptData(ENCRYPTION_KEY, data)

            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_prefs",
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val encryptedBase64Data = Base64.encodeToString(encryptedData, Base64.DEFAULT)
            sharedPreferences.edit()
                .putString("encrypted_data", encryptedBase64Data)
                .putString("iv", Base64.encodeToString(iv, Base64.DEFAULT))
                .apply()

            return encryptedBase64Data
        }

        fun getEncryptedKey(context: Context): String? {
            // Initialize the EncryptedSharedPreferences
            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_prefs",
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            // Retrieve the encrypted data from shared preferences
            return sharedPreferences.getString("encrypted_data", null)
        }

        fun retrieveDecryptedData(context: Context, alias: String): String? {
            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_prefs",
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val encryptedData =
                Base64.decode(sharedPreferences.getString("encrypted_data", null), Base64.DEFAULT)
            val iv = Base64.decode(sharedPreferences.getString("iv", null), Base64.DEFAULT)


            return decryptData(alias, iv, encryptedData)
        }


        fun generateKey(alias: String) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }

        fun encryptData(key: String, data: String): Pair<ByteArray, ByteArray> {
            try {

                val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")

                val iv = key.toByteArray(Charsets.UTF_8).copyOfRange(0, 12)

                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                val gcmSpec = GCMParameterSpec(128, iv)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

                val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

                return Pair(iv, encryptedData)
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException("Encryption failed: ${e.message}")
            }
        }


        fun decryptData(key: String, iv: ByteArray, encryptedData: ByteArray): String {
            val secretKey = SecretKeySpec(key.toByteArray(), "AES")
            val cipher = Cipher.getInstance(CIPHER_NAME_NEW)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedData = cipher.doFinal(encryptedData)
            return String(decryptedData)
        }


    }


    fun initializeEncryptionKey(alias: String) {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(alias)) {
            generateKey(alias)
        }
    }



}



