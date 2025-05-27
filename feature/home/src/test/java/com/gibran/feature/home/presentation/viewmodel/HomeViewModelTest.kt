package com.gibran.feature.home.presentation.viewmodel

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.gibran.core.domain.model.Track
import com.gibran.feature.home.domain.repository.HomeRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val repository: HomeRepository = mockk(relaxed = true)
    private lateinit var viewModel: HomeViewModel


    fun setUpVieModel() {
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `tracks emits expected data`() = runTest {
        // Arrange
        val expectedTrack = Track(
            id = 1L,
            title = "Blinding Lights",
            previewUrl = null,
            duration = 200,
            artist = "The Weeknd",
            albumTitle = "After Hours",
            albumCoverUrl = "",
            explicitLyrics = false,
            rank = 0
        )
        coEvery { repository.getNewReleases() } returns flowOf(PagingData.from(listOf(expectedTrack)))
        advanceUntilIdle()
        setUpVieModel()

        // Act
        val snapshot = viewModel.tracks.asSnapshot { scrollTo(0) }

        // Assert
        assertEquals(1, snapshot.size)
        assertEquals("Blinding Lights", snapshot[0].title)
        assertEquals("The Weeknd", snapshot[0].artist)
        coVerify(exactly = 1) { repository.getNewReleases() }
    }

    @Test
    fun `tracks emits empty list when repository returns no results`() = TestScope(UnconfinedTestDispatcher()).runTest {
        // Arrange
        coEvery { repository.getNewReleases() } returns flowOf(PagingData.from(emptyList()))
        setUpVieModel()
        // Act
        val snapshot = viewModel.tracks.asSnapshot { scrollTo(0) }
        advanceTimeBy(100)

        // Assert
        assertTrue(snapshot.isEmpty())
        coVerify(exactly = 1) { repository.getNewReleases() }
    }

    @Test(expected = RuntimeException::class)
    fun `tracks throws exception when repository fails`() = runTest {
        // Arrange
        coEvery { repository.getNewReleases() } returns flowOf(
            throw RuntimeException("Repository failed")
        )
        setUpVieModel()

        // Act
        viewModel.tracks.asSnapshot { scrollTo(0) }

        // Assert is handled by expected exception
        coVerify(exactly = 1) { repository.getNewReleases() }
    }
}
