package com.fit2081.nutritrack.data.fruityviceapi

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Referenced from ChatGPT
class FruitViewModel(context: Context) : ViewModel() {
    private val fruitRepo = FruitsRepository()

    private val _fruit = MutableStateFlow<FruitResponse?>(null)
    val fruit: StateFlow<FruitResponse?> = _fruit

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchFruitInfo(fruitName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // reset error before fetch

            val result = fruitRepo.getFruit(fruitName.lowercase())

            if (result != null) {
                _fruit.value = result
            } else {
                _fruit.value = null
                _errorMessage.value = "No data found for \"$fruitName\"."
            }

            _isLoading.value = false
        }
    }

    class FruitViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FruitViewModel(context) as T
        }
    }
}