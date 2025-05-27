package com.gibran.feature.search.presentation.intent

sealed class SearchIntent {
    data object Load : SearchIntent()
    data class UpdateQuery(val query: String) : SearchIntent()
}
