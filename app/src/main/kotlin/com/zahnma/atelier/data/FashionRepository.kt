package com.zahnma.atelier.data

import android.content.Context
import com.zahnma.atelier.data.model.Brand
import com.zahnma.atelier.data.model.BrandsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

class FashionRepository(
    context: Context,
    private val localDataSource: LocalDataSource = LocalDataSource(
        context = context,
        json = json,
    ),
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(json = json),
    private val syncMetadataStore: SyncMetadataStore = SyncMetadataStore(context),
) {
    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: Flow<List<Brand>> = _brands.asStateFlow()

    private val _dataVersion = MutableStateFlow<String?>(null)
    val dataVersion: Flow<String?> = _dataVersion.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: Flow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: Flow<String?> = _syncMessage.asStateFlow()

    suspend fun loadInitialData() {
        val cached = localDataSource.readCache()
        val seed = localDataSource.readSeedData()
        val initial = pickNewer(cached, seed)
        applyData(initial)
    }

    suspend fun refreshFromRemote(force: Boolean = false) {
        if (_isSyncing.value) return
        _isSyncing.update { true }
        _syncMessage.update { null }

        try {
            val currentVersion = _dataVersion.value
            remoteDataSource.fetchRemoteData()
                .onSuccess { remote ->
                    val local = localDataSource.readCache()
                        ?: localDataSource.readSeedData()
                    if (force || isRemoteNewer(remote, local)) {
                        localDataSource.writeCache(remote)
                        applyData(remote)
                        syncMetadataStore.saveSync(remote.dataVersion)
                        _syncMessage.update { "Updated to ${remote.dataVersion}" }
                    } else if (currentVersion == remote.dataVersion) {
                        _syncMessage.update { "Already up to date" }
                    }
                }
                .onFailure { error ->
                    _syncMessage.update {
                        error.message ?: "Could not reach remote data"
                    }
                }
        } finally {
            _isSyncing.update { false }
        }
    }

    fun brandById(id: String): Brand? = _brands.value.firstOrNull { it.id == id }

    private fun applyData(data: BrandsData) {
        _dataVersion.update { data.dataVersion }
        _brands.update {
            data.brands.sortedBy { brand -> brand.name.lowercase() }
        }
    }

    private fun pickNewer(first: BrandsData?, second: BrandsData): BrandsData {
        if (first == null) return second
        return if (isRemoteNewer(first, second)) first else second
    }

    private fun isRemoteNewer(candidate: BrandsData, baseline: BrandsData): Boolean {
        return candidate.dataVersion > baseline.dataVersion
    }

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }
}
