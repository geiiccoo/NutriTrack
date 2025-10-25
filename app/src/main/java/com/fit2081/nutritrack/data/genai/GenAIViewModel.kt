package com.fit2081.nutritrack.data.genai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.nutritrack.data.AuthManager
import com.fit2081.nutritrack.data.NutriDatabase
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Referenced from W7 Lab
class GenAIViewModel(context: Context) : ViewModel() {
    private val motivationalTipDao = NutriDatabase.getDatabase(context).motivationalTipDao()
    private val userId = AuthManager.getUserId().toString()

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val uiState: StateFlow<UIState> =_uiState.asStateFlow()

    private val _savedTips = MutableStateFlow<List<MotivationalTip>>(emptyList())
    val savedTips: StateFlow<List<MotivationalTip>> = _savedTips.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = YOUR_API_KEY_GOES_HERE
    )

    fun generateMotivationalMessage() {
        _uiState.value = UIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text("Generate a short encouraging message to help someone improve their fruit intake.")
                    }
                )
                response.text?.let {
                    val tip = MotivationalTip(userId = userId, message = it)
                    motivationalTipDao.insertTip(tip)
                    _uiState.value = UIState.Success(it)
                    loadAllTips()
                }
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun loadAllTips() {
        viewModelScope.launch {
            motivationalTipDao.getAllTipsForUser(userId).collect {
                _savedTips.value = it
            }
        }
    }

    class GenAIViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GenAIViewModel(context) as T
        }
    }
}