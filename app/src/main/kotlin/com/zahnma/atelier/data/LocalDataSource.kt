package com.zahnma.atelier.data

import android.content.Context
import com.zahnma.atelier.data.model.BrandsData
import kotlinx.serialization.json.Json
import java.io.File

class LocalDataSource(
    private val context: Context,
    private val json: Json,
) {
    private val cacheFile: File
        get() = File(context.filesDir, CACHE_FILE_NAME)

    fun readSeedData(): BrandsData {
        context.assets.open(SEED_FILE_NAME).use { input ->
            val text = input.bufferedReader().readText()
            return json.decodeFromString<BrandsData>(text)
        }
    }

    fun readCache(): BrandsData? {
        if (!cacheFile.exists()) return null
        return runCatching {
            json.decodeFromString<BrandsData>(cacheFile.readText())
        }.getOrNull()
    }

    fun writeCache(data: BrandsData) {
        cacheFile.writeText(json.encodeToString(BrandsData.serializer(), data))
    }

    companion object {
        private const val SEED_FILE_NAME = "brands_seed.json"
        private const val CACHE_FILE_NAME = "brands_cache.json"
    }
}
