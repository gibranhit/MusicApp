package com.gibran.feature.search.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.gibran.core.common.handleError
import com.gibran.core.common.shimmerEffect
import com.gibran.core.domain.model.Track
import com.gibran.feature.search.R
import com.gibran.feature.search.presentation.intent.SearchIntent
import com.gibran.feature.search.presentation.state.SearchUiState
import com.gibran.feature.search.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val searchResults = viewModel.results.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = state.query,
            onValueChange = {
                viewModel.onIntent(SearchIntent.UpdateQuery(it))
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text(stringResource(R.string.search_label)) },
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        SearchResultsContent(
            state = state,
            searchResults = searchResults,
            onTrackClick = onTrackClick
        )
    }
}

@Composable
fun TrackCard(
    track: Track,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.albumCoverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "next",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TrackCardEmptyState() {
    Box(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .shimmerEffect(),
    )
}

@Composable
fun SearchResultsContent(
    state: SearchUiState,
    searchResults: LazyPagingItems<Track>,
    onTrackClick: (Track) -> Unit
) {
    val refreshState = searchResults.loadState.refresh
    val showError = refreshState is LoadState.Error && searchResults.itemCount == 0
    val showEmptyResults = refreshState is LoadState.NotLoading && searchResults.itemCount == 0

    if (state.query.length > 2) {
        if (showEmptyResults) {
            Text(
                text = stringResource(R.string.error_no_results),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults.itemCount) { index ->
                searchResults[index]?.let { track ->
                    TrackCard(
                        track = track,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
            item {
                when {
                    searchResults.loadState.append is LoadState.Loading -> {
                        TrackCardEmptyState()
                    }
                    showError -> {
                        val error = refreshState.error.handleError(R.string.error_loading_more_results)
                        Text(
                            text = stringResource(error),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private val previewTracks = listOf(
    Track(
        id = 1L,
        title = "Preview Song",
        artist = "Preview Artist",
        albumTitle = "Preview Album",
        albumCoverUrl = "",
        previewUrl = null,
        duration = 180,
        rank = 1,
        explicitLyrics = false
    )
)

class TrackListPreviewProvider : PreviewParameterProvider<List<Track>> {
    override val values = sequenceOf(previewTracks)
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview(@PreviewParameter(TrackListPreviewProvider::class) items: List<Track>) {
    val dummyState = SearchUiState(query = "Preview")
    val lazyItems = flowOf(PagingData.from(items)).collectAsLazyPagingItems()
    SearchResultsContent(
        state = dummyState,
        searchResults = lazyItems,
        onTrackClick = {}
    )
}

