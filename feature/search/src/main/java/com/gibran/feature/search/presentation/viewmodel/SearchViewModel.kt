package com.gibran.feature.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.gibran.core.domain.model.Track
import com.gibran.feature.search.domain.repository.SearchRepository
import com.gibran.feature.search.presentation.intent.SearchIntent
import com.gibran.feature.search.presentation.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var _results: Flow<PagingData<Track>> = emptyFlow()
    val results: Flow<PagingData<Track>> get() = _results

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Load -> {
                _uiState.value = SearchUiState()
                _results = emptyFlow()
            }
            is SearchIntent.UpdateQuery -> {
                _uiState.value = _uiState.value.copy(query = intent.query)
                _results = repository.searchTracks(intent.query)
            }
        }
    }
}
