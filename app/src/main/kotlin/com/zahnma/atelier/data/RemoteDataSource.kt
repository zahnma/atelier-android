package com.zahnma.atelier.data

import com.zahnma.atelier.BuildConfig
import com.zahnma.atelier.data.model.BrandsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class RemoteDataSource(
    private val json: Json,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build(),
    private val remoteUrl: String = BuildConfig.REMOTE_DATA_URL,
) {
    suspend fun fetchRemoteData(): Result<BrandsData> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url(remoteUrl)
                .header("Accept", "application/json")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    error("Remote fetch failed: HTTP ${response.code}")
                }
                val body = response.body?.string()
                    ?: error("Remote fetch failed: empty body")
                json.decodeFromString<BrandsData>(body)
            }
        }
    }
}
