package com.gibran.feature.search.presentation.viewmodel

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.gibran.core.domain.model.Track
import com.gibran.feature.search.domain.repository.SearchRepository
import com.gibran.feature.search.presentation.intent.SearchIntent
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Before

@ExperimentalCoroutinesApi
class SearchViewModelTest {

    private val repository: SearchRepository = mockk(relaxed = true)
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getUiState initial state`() = runTest {
        // Arrange is implicit (state is fresh from setup)

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertEquals("", state.query)
    }

    @Test
    fun `onIntent Load resets UI state`() = runTest {
        // Arrange
        coEvery { repository.searchTracks(any()) } returns flowOf()
        viewModel.onIntent(SearchIntent.UpdateQuery("drake"))

        // Act
        viewModel.onIntent(SearchIntent.Load)
        val state = viewModel.uiState.value

        // Assert
        assertEquals("", state.query)
    }

    @Test
    fun `onIntent UpdateQuery updates UI state query`() = runTest {
        // Arrange
        coEvery { repository.searchTracks("taylor") } returns flowOf()

        // Act
        viewModel.onIntent(SearchIntent.UpdateQuery("taylor"))
        val state = viewModel.uiState.value

        // Assert
        assertEquals("taylor", state.query)
    }

    @Test
    fun `onIntent UpdateQuery calls repository with correct query`() = runTest {
        // Arrange
        coEvery { repository.searchTracks("drake") } returns flowOf()

        // Act
        viewModel.onIntent(SearchIntent.UpdateQuery("drake"))

        // Assert
        coVerify { repository.searchTracks("drake") }
    }

    @Test
    fun `results emits expected track list when query is updated`() = runTest {
        // Arrange
        val expectedTrack = Track(
            id = 1L,
            title = "One Dance",
            previewUrl = null,
            duration = 0,
            artist = "Drake",
            albumTitle = "",
            albumCoverUrl = "",
            explicitLyrics = false,
            rank = 0
        )
        coEvery { repository.searchTracks("drake") } returns flowOf(PagingData.from(listOf(expectedTrack)))

        // Act
        viewModel.onIntent(SearchIntent.UpdateQuery("drake"))

        // Assert
        val snapshot = viewModel.results.asSnapshot {
            scrollTo(0)
        }

        assertEquals(1, snapshot.size)
        assertEquals("One Dance", snapshot[0].title)
        assertEquals("Drake", snapshot[0].artist)
    }

    @Test
    fun `onIntent Load clears results`() = runTest {
        // Arrange
        val track = Track(id = 1L, title = "Hotline Bling", previewUrl = null, duration = 0, artist = "Drake", albumTitle = "", albumCoverUrl = "", explicitLyrics = false, rank = 0)
        coEvery { repository.searchTracks("drake") } returns flowOf(PagingData.from(listOf(track)))
        viewModel.onIntent(SearchIntent.UpdateQuery("drake"))

        // Act
        viewModel.onIntent(SearchIntent.Load)
        val snapshot = viewModel.results.asSnapshot()

        // Assert
        assertTrue(snapshot.isEmpty())
    }
}
