package com.gibran.feature.home.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.gibran.core.database.datasource.TrackDataSource
import com.gibran.core.database.entity.TrackEntity
import com.gibran.feature.home.data.api.HomeApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeRepositoryImplTest {

    private val api: HomeApi = mockk(relaxed = true)
    private val trackDataSource: TrackDataSource = mockk(relaxed = true)
    private lateinit var repository: HomeRepositoryImpl

    @Before
    fun setUp() {
        repository = HomeRepositoryImpl(api, trackDataSource)
    }

    @Test
    fun `getNewReleases returns paging data with expected tracks`() = runTest {
        // Arrange
        val expected = TrackEntity(
            id = 1L,
            title = "HUMBLE.",
            artist = "Kendrick Lamar",
            albumTitle = "DAMN.",
            albumCoverUrl = "cover.jpg",
            previewUrl = null,
            duration = 180,
            explicitLyrics = true,
            rank = 1
        )
        coEvery { trackDataSource.getPagedTracks() } returns listOf(expected).let {
            object : PagingSource<Int, TrackEntity>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrackEntity> {
                    return LoadResult.Page(data = it, prevKey = null, nextKey = null)
                }

                override fun getRefreshKey(state: PagingState<Int, TrackEntity>): Int? = null
            }
        }

        // Act
        val snapshot = repository.getNewReleases().asSnapshot { scrollTo(0) }

        // Assert
        assertEquals(1, snapshot.size)
        val track = snapshot[0]
        assertEquals("HUMBLE.", track.title)
        assertEquals("Kendrick Lamar", track.artist)
        assertEquals("DAMN.", track.albumTitle)
        assertEquals("cover.jpg", track.albumCoverUrl)
    }

    @Test
    fun `getNewReleases returns empty when no tracks in DB`() = runTest {
        // Arrange
        coEvery { trackDataSource.getPagedTracks() } returns emptyList<TrackEntity>().let {
            object : PagingSource<Int, TrackEntity>() {
                override fun getRefreshKey(state: PagingState<Int, TrackEntity>): Int? = null

                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrackEntity> {
                    return LoadResult.Page(data = it, prevKey = null, nextKey = null)
                }
            }
        }

        // Act
        val snapshot = repository.getNewReleases().asSnapshot { scrollTo(0) }

        // Assert
        assertTrue(snapshot.isEmpty())
    }

    @Test(expected = RuntimeException::class)
    fun `getNewReleases throws exception when paging source fails`() = runTest {
        // Arrange
        val exception = RuntimeException("Paging failure")
        coEvery { trackDataSource.getPagedTracks() } throws exception

        // Act & Assert
        repository.getNewReleases().asSnapshot { scrollTo(0) }
    }
}