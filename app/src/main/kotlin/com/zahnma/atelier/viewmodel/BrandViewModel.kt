package com.zahnma.atelier.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zahnma.atelier.data.FashionRepository
import com.zahnma.atelier.data.model.Brand
import com.zahnma.atelier.data.model.CreativeDirector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class BrandViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = FashionRepository(application.applicationContext)

    val brands: StateFlow<List<Brand>> = repository.brands.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val dataVersion: StateFlow<String?> = repository.dataVersion.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val isSyncing: StateFlow<Boolean> = repository.isSyncing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    val syncMessage: StateFlow<String?> = repository.syncMessage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredBrands: StateFlow<List<Brand>> = combine(brands, searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { brand ->
                brand.name.contains(query, ignoreCase = true) ||
                    brand.country.contains(query, ignoreCase = true) ||
                    brand.category.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        viewModelScope.launch {
            repository.loadInitialData()
            repository.refreshFromRemote(force = false)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            repository.refreshFromRemote(force = force)
        }
    }

    fun brandFor(id: String): Brand? = repository.brandById(id)

    fun sortedDirectors(brand: Brand): List<CreativeDirector> {
        return brand.directors.sortedWith(
            compareByDescending<CreativeDirector> { it.currentlyServing }
                .thenByDescending { parseDate(it.startDate) },
        )
    }

    companion object {
        private val displayFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())

        fun formatTenure(startDate: String, endDate: String?): String {
            val start = formatDate(startDate)
            val end = endDate?.let(::formatDate) ?: "Present"
            return "$start – $end"
        }

        private fun formatDate(raw: String): String {
            return runCatching {
                LocalDate.parse(raw).format(displayFormatter)
            }.getOrDefault(raw)
        }

        private fun parseDate(raw: String): LocalDate {
            return runCatching { LocalDate.parse(raw) }
                .getOrDefault(LocalDate.MIN)
        }
    }
}
