package com.fit2081.nutritrack.data.genai

/**
 * A sealed hierarchy describing the state of the text generation.
 */
// Referenced from W7 Lab
sealed interface UIState {
    /**
     * Empty state when the screen is first shown
     */
    object Initial : UIState

    /**
     * Still loading
     */
    object Loading : UIState

    /**
     * Text has been generated
     */
    data class Success(val outputText: String) : UIState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UIState
}