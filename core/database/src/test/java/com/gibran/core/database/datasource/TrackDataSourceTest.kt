package com.gibran.core.database.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibran.core.database.dao.TrackDao
import com.gibran.core.database.entity.TrackEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TrackDataSourceTest {

    private val fakeDao = mockk<TrackDao>()
    private lateinit var dataSource: TrackDataSource

    @Before
    fun setup() {
        dataSource = TrackDataSource(fakeDao)
    }

    @Test
    fun `getPagedTracks returns correct paging source`() = runTest {
        // Arrange
        val expected = listOf(
            TrackEntity(
                id = 1,
                title = "Test Track",
                artist = "Artist 1",
                albumTitle = "Album 1",
                albumCoverUrl = "url1",
                previewUrl = "preview1",
                duration = 180,
                explicitLyrics = false,
                rank = 1
            )
        )

        val fakePagingSource = object : PagingSource<Int, TrackEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrackEntity> {
                return LoadResult.Page(data = expected, prevKey = null, nextKey = null)
            }

            override fun getRefreshKey(state: PagingState<Int, TrackEntity>): Int? = null
        }

        coEvery { fakeDao.getPagedTracks() } returns fakePagingSource

        // Act
        val result = dataSource.getPagedTracks().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Assert
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(expected, page.data)
    }

    @Test
    fun `getPagedTracks returns error when paging source fails`() = runTest {
        // Arrange
        val fakePagingSource = object : PagingSource<Int, TrackEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrackEntity> {
                return LoadResult.Error(RuntimeException("Paging failed"))
            }

            override fun getRefreshKey(state: PagingState<Int, TrackEntity>): Int? = null
        }

        coEvery { fakeDao.getPagedTracks() } returns fakePagingSource

        // Act
        val result = dataSource.getPagedTracks().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Assert
        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `getPagedTracks returns empty page when no data available`() = runTest {
        // Arrange
        val fakePagingSource = object : PagingSource<Int, TrackEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrackEntity> {
                return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            }

            override fun getRefreshKey(state: PagingState<Int, TrackEntity>): Int? = null
        }

        coEvery { fakeDao.getPagedTracks() } returns fakePagingSource

        // Act
        val result = dataSource.getPagedTracks().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Assert
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
    }

    @Test
    fun `insertAll calls DAO with expected tracks`() = runTest {
        // Arrange
        val tracks = listOf(
            TrackEntity(1, "A", "Artist", "Album", "url", "preview", 180, false, 1)
        )
        val dao = mockk<TrackDao>(relaxed = true)
        val dataSource = TrackDataSource(dao)

        // Act
        dataSource.insertAll(tracks)

        // Assert
        coVerify(exactly = 1) { dao.insertAll(tracks) }
    }

    @Test
    fun `clearAll calls DAO clearAll`() = runTest {
        // Arrange
        val dao = mockk<TrackDao>(relaxed = true)
        val dataSource = TrackDataSource(dao)

        // Act
        dataSource.clearAll()

        // Assert
        coVerify(exactly = 1) { dao.clearAll() }
    }
}
