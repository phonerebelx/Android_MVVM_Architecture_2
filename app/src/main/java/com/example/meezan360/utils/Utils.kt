package com.example.meezan360.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Utils {

    companion object {
        private val CIPHER_NAME = "AES/CBC/PKCS5PADDING"
        private val CIPHER_KEY_LEN = 16 //128 bits
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
                Log.d( "encryptPass: ","$base64_EncryptedData:$base64_IV")
                return "$base64_EncryptedData:$base64_IV"
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return null
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
    }



}