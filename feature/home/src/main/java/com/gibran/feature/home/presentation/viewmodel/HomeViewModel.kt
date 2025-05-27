package com.gibran.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.gibran.feature.home.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val tracks = repository.getNewReleases()

}
