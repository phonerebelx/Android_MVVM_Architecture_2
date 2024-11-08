package com.example.meezan360.security

import android.content.Context
import android.os.Build
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.KeyStoreException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Suppress("UNCHECKED_CAST")
class EncryptionKeyStoreImpl {
    var key: SecretKey? = null
    private val encryptor: EnCryptor = EnCryptor()
    private var decryptor: DeCryptor? = null

    /**
     * Making this class Singleton
     * Using Bill Pugh Singleton Implementation
     */
    companion object {
        val instance: EncryptionKeyStoreImpl by lazy {
            EncryptionKeyStoreImpl()
        }

        const val SAMPLE_ALIAS = "MYALIAS22"
    }

    fun encryptList(list: List<Any>): List<Any> {
        for (item in list) {
            encryptObject(item)
        }
        return list
    }

    fun decryptList(list: List<Any>): List<Any> {
        for (item in list) {
            decryptObject(item)
        }
        return list
    }

    private fun encryptObject(classObject: Any): Any {
        val f = classObject.javaClass.declaredFields
        for (item in f) {
            val field = classObject.javaClass.getDeclaredField(item.name)
            field.isAccessible = true
            val value = field.get(classObject) //getting value if specific field
            if (value is List<*>) {
                encryptList(value as List<Any>)
            } else if (field.annotations.toList().toString().contains("NotToBeEncrypted"))
                field.set(classObject, value)
            else if (value !is String && value !is Long && value !is Int) {
                if (value != null)
                    encryptObject(value)
            } else if (value is String)
                field.set(classObject, encrypt(value.toString()))
        }
        return classObject
    }

    private fun decryptObject(classObject: Any): Any {
        val f = classObject.javaClass.declaredFields
        for (item in f) {
            val field = classObject.javaClass.getDeclaredField(item.name)
            field.isAccessible = true
            val value = field.get(classObject) //getting value if specific field
            if (value is List<*>) {
                decryptList(value as List<Any>)
            } else if (field.annotations.toList().toString().contains("NotToBeEncrypted"))
                field.set(classObject, value)
            else if (value !is String && value !is Long && value !is Int) {
                if (value != null)
                    decryptObject(value)
            } else if (value is String)
                field.set(classObject, decrypt(value.toString()))
        }
        return classObject
    }

    fun encrypt(value: Any?): String? {

        return if (value != null) {
            encryptor.encryptText(value, key)
        } else
            ""
    }

    fun decrypt(value: String?): String? {
        return if (value != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                decryptor?.decrypt(value, key)
            } else {
                TODO("VERSION.SDK_INT < KITKAT")
            }
        } else
            ""
    }

    private var ks: KeyStore? = null
    private val chars = "1234567890".toCharArray()

    private var context: Context? = null
    fun setContext(context: Context?) {
        this.context = context
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType())
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }
    }

    fun generateKey() {
        try {
            ks?.load(null, chars)
            val kg = KeyGenerator.getInstance("AES")
            kg.init(128)
            val key = kg.generateKey()
            val secretKeyEntry =
                KeyStore.SecretKeyEntry(key)
            ks?.setEntry(SAMPLE_ALIAS, secretKeyEntry, null)
            val fos =
                FileOutputStream(context!!.filesDir.absolutePath + "/OEKeyStore")
            ks?.store(fos, chars)
            loadKey()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun loadKey() {
        try {
            val fis =
                FileInputStream(context!!.filesDir.absolutePath + "/OEKeyStore")
            ks?.load(fis, chars)
            val secretKeyEntry = ks!!.getEntry(
                SAMPLE_ALIAS,
                null
            ) as KeyStore.SecretKeyEntry
            key = secretKeyEntry.secretKey
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        try {
            decryptor = DeCryptor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}