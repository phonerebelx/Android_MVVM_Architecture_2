package com.example.meezan360.security

import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec

class DeCryptor internal constructor() {
    private lateinit var keyStore: KeyStore
    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class
    )

    private fun initKeyStore() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun decrypt(encrypted: String, key: SecretKey?): String? {
        val encodedString: String
        try {
            val split = encrypted.split(IV_SEPARATOR.toRegex()).toTypedArray()
            require(split.size == 2) { "Passed data is incorrect. There was no IV specified with it." }
            val ivString = split[0]
            encodedString = split[1]
            var ivParameterSpec: IvParameterSpec
            val spec: GCMParameterSpec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                GCMParameterSpec(128, java.util.Base64.getDecoder().decode(ivString))
            } else {
                GCMParameterSpec(128, Base64.decode(ivString, Base64.DEFAULT))
            }
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            val encryptedData: ByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) java.util.Base64.getDecoder()
                .decode(encodedString) else Base64.decode(encodedString, Base64.DEFAULT)
            val decodedData = cipher.doFinal(encryptedData)
            return String(decodedData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val IV_SEPARATOR = "]"
    }

    init {
        initKeyStore()
    }
}