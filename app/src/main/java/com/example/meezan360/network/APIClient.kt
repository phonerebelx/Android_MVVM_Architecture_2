package com.example.meezan360.network


import android.content.Context
import com.example.meezan360.R
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Arrays
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class APIClient() {
    companion object {
        private const val BASE_URL = "http://thesalesforceapi.avengers.pk/api/v1/"
//        private const val BASE_URL = "https://bdosales.meezanbank.com:9988/api/v1/"



        fun create(sharedPreferencesManager: SharedPreferencesManager,context: Context): APIService {


            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + Arrays.toString(
                    trustManagers
                )
            }

            val trustManager = trustManagers.get(0) as X509TrustManager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
            val client = OkHttpClient.Builder().apply {

                addInterceptor(BaseHeadersInterceptor(sharedPreferencesManager))
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                connectTimeout(50, TimeUnit.SECONDS) // Set connection timeout
                readTimeout(50, TimeUnit.SECONDS)    // Set read timeout
                writeTimeout(50, TimeUnit.SECONDS)   // Set write timeout
            }
            client.sslSocketFactory(getSSLConfig(context)?.socketFactory!!, trustManager)
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
        }
        @Throws(
            CertificateException::class,
            IOException::class,
            KeyStoreException::class,
            NoSuchAlgorithmException::class,
            KeyManagementException::class
        )
        private fun getSSLConfig(context: Context): SSLContext? {
            val cf = CertificateFactory.getInstance("X.509")
            val cert = context.resources.openRawResource(R.raw.bdosales_meezanbank_com)
            val ca: Certificate
            // I'm using Java7. If you used Java6 close it manually with finally.
            ca = try {
                cf.generateCertificate(cert)
            } finally {
                cert.close()
            }

            // Creating a KeyStore containing our trusted CAs
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)

            // Creating a TrustManager that trusts the CAs in our KeyStore.
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)

            // Creating an SSLSocketFactory that uses our TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            return sslContext
        }
    }
}

class BaseHeadersInterceptor(private val sharedPreferencesManager: SharedPreferencesManager) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            val token = sharedPreferencesManager.getToken()
            if (!token.isNullOrBlank()) {
                header("Authorization", "Bearer $token")
                header("response-type", "1") //0 for static data and 1 for dynamic data
            }
        }.build()
        return chain.proceed(request)
    }
}