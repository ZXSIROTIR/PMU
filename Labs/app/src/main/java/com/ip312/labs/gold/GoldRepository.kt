package com.ip312.labs.gold

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class GoldRepository private constructor(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("gold_rate", Context.MODE_PRIVATE)

    private val api: CbrApi = Retrofit.Builder()
        .baseUrl("https://www.cbr.ru/")
        .client(
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Android) BugsGame")
                            .build()
                    )
                }
                .build()
        )
        .build()
        .create(CbrApi::class.java)

    fun cached(): GoldRate? {
        val rate = prefs.getString(KEY_RATE, null)?.toDoubleOrNull()
        val date = prefs.getString(KEY_DATE, null)

        return if (rate != null && date != null) GoldRate(rate, date) else null
    }

    suspend fun refresh(): GoldRate? = withContext(Dispatchers.IO) {
        try {
            val (from, to) = requestRange()

            val body = api.getMetals(from, to)

            val rate = body.use {
                CbrXmlParser.parseGold(it.byteStream())
            }

            if (rate != null) {
                prefs.edit()
                    .putString(KEY_RATE, rate.rubPerGram.toString())
                    .putString(KEY_DATE, rate.date)
                    .apply()
            }

            rate
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Не удалось загрузить курс золота", e)
            null
        }
    }

    private fun requestRange(): Pair<String, String> {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("Europe/Moscow")
        }

        val now = System.currentTimeMillis()

        return Pair(
            format.format(Date(now - 14 * DAY_MS)),
            format.format(Date(now))
        )
    }

    companion object {

        private const val TAG = "GoldRepository"
        private const val KEY_RATE = "rate"
        private const val KEY_DATE = "date"
        private const val DAY_MS = 24L * 60 * 60 * 1000

        @Volatile
        private var instance: GoldRepository? = null

        fun get(context: Context): GoldRepository =
            instance ?: synchronized(this) {
                instance ?: GoldRepository(context.applicationContext)
                    .also { instance = it }
            }
    }
}
