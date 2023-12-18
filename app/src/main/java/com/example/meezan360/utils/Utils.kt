package com.example.meezan360.utils

import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Utils {

    companion object {
        fun md5(s: String): String {
            val MD5 = "MD5"
            try {
                // Create MD5 Hash
                val digest = MessageDigest
                    .getInstance(MD5)
                digest.update(s.toByteArray())
                val messageDigest = digest.digest()

                // Create Hex String
                val hexString = StringBuilder()
                for (aMessageDigest in messageDigest) {
                    var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                    while (h.length < 2) h = "0$h"
                    hexString.append(h)
                }
                return hexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                Log.e("Catch:Utils:335", e.localizedMessage)
            }
            return ""
        }
    }
}