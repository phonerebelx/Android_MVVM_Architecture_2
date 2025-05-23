package com.example.architecture.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Base64
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.architecture.R
import java.security.SecureRandom
import javax.crypto.Cipher
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

        fun encryptedSharedPref(context: Context, data: String, userId: String): String {
            val ENCRYPTION_KEY = "Ytr0ngS3cur3K3y!"


            val (ivData, encryptedData) = encryptData(ENCRYPTION_KEY, data)
            val (ivUsername, encryptedUsername) = encryptUserName(ENCRYPTION_KEY, userId)


            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_prefs",
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val encryptedBase64Data = Base64.encodeToString(encryptedData, Base64.DEFAULT)
            val encryptedBase64Username = Base64.encodeToString(encryptedUsername, Base64.DEFAULT)

            sharedPreferences.edit()
                .putString("encrypted_data", encryptedBase64Data)
                .putString("iv_data", Base64.encodeToString(ivData, Base64.DEFAULT))
                .putString("encrypted_username", encryptedBase64Username)
                .putString("iv_username", Base64.encodeToString(ivUsername, Base64.DEFAULT))
                .apply()

            return encryptedBase64Data
        }

        fun clearEncryptedSharedPref(context: Context) {
            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_prefs",
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            sharedPreferences.edit().clear().apply()
        }


        fun getEncryptedKey(context: Context): String? {

            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_prefs",
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )


            return sharedPreferences.getString("encrypted_data", null)
        }

        fun retrieveDecryptedUserId(context: Context): String? {
            val ENCRYPTION_KEY = "Ytr0ngS3cur3K3y!"

            val sharedPreferences = EncryptedSharedPreferences.create(
                "secure_prefs",
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val encryptedUsername = sharedPreferences.getString("encrypted_username", null)?.let {
                Base64.decode(it, Base64.DEFAULT)
            } ?: return null

            val ivUsername = sharedPreferences.getString("iv_username", null)?.let {
                Base64.decode(it, Base64.DEFAULT)
            } ?: return null

            return decryptData(ENCRYPTION_KEY, ivUsername, encryptedUsername)
        }


        fun encryptUserName(key: String, data: String): Pair<ByteArray, ByteArray> {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
            val encryptedData = cipher.doFinal(data.toByteArray())
            return Pair(iv, encryptedData)
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
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
            val ivSpec = IvParameterSpec(iv)

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
            return String(cipher.doFinal(encryptedData))
        }


        // Abdul Ali:: This function is create to change the color of text for fingerprint dialog
        fun changeDescriptionColor(context: Context, text: String): SpannableString {
            val description = SpannableString(text)
            description.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)),
                0,
                description.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return description
        }
    }

}



