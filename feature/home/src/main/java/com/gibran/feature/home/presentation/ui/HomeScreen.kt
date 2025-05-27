package com.gibran.feature.home.presentation.ui


import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gibran.feature.home.presentation.viewmodel.HomeViewModel
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.gibran.core.common.handleError
import com.gibran.core.domain.model.Track
import com.gibran.feature.home.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onTrackClick: (Track) -> Unit,
    onSearch: () -> Unit,
) {

    val tracks = viewModel.tracks.collectAsLazyPagingItems()

    HomeScreenContent(
        modifier = modifier,
        lazyTracks = tracks,
        onSearch = onSearch,
        onRetry = { tracks.refresh() },
        onTrackClick = onTrackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    lazyTracks: LazyPagingItems<Track>,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onTrackClick: (Track) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.home_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        }
    ) { padding ->
        val refreshing = lazyTracks.loadState.refresh is LoadState.Loading

        val refreshState = lazyTracks.loadState.refresh
        val showError = refreshState is LoadState.Error && lazyTracks.itemCount == 0
        val isCurrentlyLoading = refreshState is LoadState.Loading

        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = onRetry,
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SearchBar(onSearch = onSearch)
                Spacer(modifier = Modifier.height(24.dp))

                when {
                    isCurrentlyLoading && lazyTracks.itemCount == 0 -> CircularProgressIndicator()
                    lazyTracks.itemCount > 0 -> {
                        Text(
                            stringResource(R.string.new_releases_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TrackRow(lazyTracks, onTrackClick)
                    }

                    showError -> {
                        val error = refreshState.error.handleError(R.string.error_loading_releases)
                        Column {
                            Text(
                                stringResource(error),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = onRetry) {
                                Text(stringResource(R.string.retry_button))
                            }
                        }
                    }

                    refreshState is LoadState.NotLoading && lazyTracks.itemCount == 0 -> {
                        Text(text = stringResource(R.string.no_releases_message))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        enabled = false,
        placeholder = { Text(stringResource(R.string.search_label)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onSearch()
            }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TrackRow(lazyTracks: LazyPagingItems<Track>, onTrackClick: (Track) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(lazyTracks.itemCount) { index ->
            lazyTracks[index]?.let { track ->
                TrackCard(
                    track,
                    onTrackClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TrackCard(
    track: Track,
    onClick: (Track) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick(track) }
    ) {
        AsyncImage(
            model = track.albumCoverUrl,
            contentDescription = track.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = track.title,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
        )
        Text(
            track.artist,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val previewTrackList = listOf(
    Track(
        id = 1L,
        title = "Sample Song",
        artist = "Sample Artist",
        albumTitle = "Sample Album",
        albumCoverUrl = "",
        previewUrl = null,
        duration = 180,
        rank = 1,
        explicitLyrics = false
    ),
    Track(
        id = 1L,
        title = "Sample Song",
        artist = "Sample Artist",
        albumTitle = "Sample Album",
        albumCoverUrl = "",
        previewUrl = null,
        duration = 180,
        rank = 1,
        explicitLyrics = false
    )
)

class PreviewTrackProvider : PreviewParameterProvider<List<Track>> {
    override val values = sequenceOf(previewTrackList)
}

@Preview(showBackground = true, name = "Home - Default State")
@Composable
fun HomeDefaultPreview(@PreviewParameter(PreviewTrackProvider::class) items: List<Track>) {
    val lazyItems = flowOf(PagingData.from(items)).collectAsLazyPagingItems()
    HomeScreenContent(
        lazyTracks = lazyItems,
        onSearch = {},
        onRetry = {},
        onTrackClick = {}
    )
}

@Preview(showBackground = true, name = "Home - Empty State")
@Composable
fun HomeEmptyPreview() {
    val lazyItems = flowOf(PagingData.empty<Track>()).collectAsLazyPagingItems()
    HomeScreenContent(
        lazyTracks = lazyItems,
        onSearch = {},
        onRetry = {},
        onTrackClick = {}
    )
}
