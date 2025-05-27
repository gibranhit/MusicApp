package com.gibran.feature.search.data.repository

import androidx.paging.testing.asSnapshot
import com.gibran.core.data.AlbumResponse
import com.gibran.core.data.ArtistResponse
import com.gibran.core.data.TrackItemResponse
import com.gibran.core.data.TrackListResponse
import com.gibran.feature.search.data.api.SearchApi
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoryImplTest {
    private val api: SearchApi = mockk(relaxed = true)
    private lateinit var repository: SearchRepositoryImpl

    @Before
    fun setUp() {
        repository = SearchRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `searchTracks returns empty when API returns no data`() = runTest {
        // Arrange
        coEvery {
            api.searchTracks("unknown", any(), any())
        } returns TrackListResponse(
            data = emptyList(),
            next = null,
            total = 0
        )

        // Act
        val result = repository.searchTracks("unknown")
        val snapshot = result.asSnapshot { scrollTo(0) }

        // Assert
        assertTrue(snapshot.isEmpty())
    }

    @Test
    fun `searchTracks returns multiple items`() = runTest {
        // Arrange
        val response = TrackListResponse(
            data = listOf(
                TrackItemResponse(
                    id = 1L,
                    title = "Track One",
                    preview = null,
                    duration = 0,
                    explicitLyrics = false,
                    rank = 0,
                    artist = ArtistResponse("Artist1"),
                    album = AlbumResponse("Album1", "cover1.jpg")
                ),
                TrackItemResponse(
                    id = 2L,
                    title = "Track Two",
                    preview = null,
                    duration = 0,
                    explicitLyrics = false,
                    rank = 0,
                    artist = ArtistResponse("Artist2"),
                    album = AlbumResponse("Album2", "cover2.jpg")
                )
            ),
            next = null,
            total = 2
        )
        coEvery { api.searchTracks("multi", 0, 10) } returns response

        // Act
        val result = repository.searchTracks("multi")
        val snapshot = result.asSnapshot { scrollTo(0) }

        // Assert
        assertEquals(2, snapshot.size)

        val first = snapshot[0]
        assertEquals(1L, first.id)
        assertEquals("Track One", first.title)
        assertEquals("Artist1", first.artist)
        assertEquals("Album1", first.albumTitle)
        assertEquals("cover1.jpg", first.albumCoverUrl)

        val second = snapshot[1]
        assertEquals(2L, second.id)
        assertEquals("Track Two", second.title)
        assertEquals("Artist2", second.artist)
        assertEquals("Album2", second.albumTitle)
        assertEquals("cover2.jpg", second.albumCoverUrl)

        coVerify { api.searchTracks("multi", 1, 10) }
    }

    @Test(expected = RuntimeException::class)
    fun `searchTracks throws exception when API fails`() = runTest {
        // Arrange
        coEvery { api.searchTracks("fail", any(), any()) } throws RuntimeException("API failure")

        // Act + Assert
        try {
            repository.searchTracks("fail").asSnapshot { scrollTo(0) }
        } catch (e: RuntimeException) {
            // Assert
            assertEquals("API failure", e.message)
            throw e // rethrow to satisfy expected = RuntimeException::class
        }
    }
}