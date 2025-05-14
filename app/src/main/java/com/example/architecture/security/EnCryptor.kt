package com.example.architecture.security

import android.os.Build
import android.util.Base64
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.*

class EnCryptor internal constructor() {
    private lateinit var iv: ByteArray
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encryptText(textToEncrypt: Any, key: SecretKey?): String {
        var result: String? = ""
        val cipher = Cipher.getInstance(TRANSFORMATION)
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        iv = cipher.iv
        val ivString: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) java.util.Base64.getEncoder()
            .encodeToString(iv) else Base64.encodeToString(iv, Base64.DEFAULT)
        result = ivString + IV_SEPARATOR
        val en = cipher.doFinal(textToEncrypt.toString().toByteArray())
        result += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getEncoder().encodeToString(en)
        } else {
            Base64.encodeToString(en, Base64.DEFAULT)
        }
        return result
    }

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SEPARATOR = "]"
    }
}