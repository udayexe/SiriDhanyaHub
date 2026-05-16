package com.siridhanya.hub.viewmodel

import androidx.lifecycle.*
import com.siridhanya.hub.data.entities.MandiPrice
import com.siridhanya.hub.data.repository.SiriDhanyaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MandiViewModel @Inject constructor(private val repo: SiriDhanyaRepository) : ViewModel() {

    private val _selectedCity = MutableStateFlow("All")
    val selectedCity: StateFlow<String> = _selectedCity

    val allCities = repo.getAllCities().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prices: StateFlow<List<MandiPrice>> = _selectedCity.flatMapLatest { city ->
        if (city == "All") repo.getAllPrices() else repo.getPricesForCity(city)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCity(city: String) { _selectedCity.value = city }

    fun refreshPrices() {
        viewModelScope.launch {
            // Simulate price refresh with slight random variation
            val current = prices.value
            val updated = current.map { price ->
                val variation = (-2..2).random().toDouble()
                val newPrice  = (price.pricePerKg + variation).coerceAtLeast(10.0)
                val newTrend  = when {
                    variation > 0 -> "UP"
                    variation < 0 -> "DOWN"
                    else -> "STABLE"
                }
                price.copy(pricePerKg = newPrice, trend = newTrend)
            }
            repo.updatePrices(updated)
        }
    }
}
