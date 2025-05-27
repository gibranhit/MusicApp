package com.gibran.feature.detail.presentation.ui

import android.R.attr.duration
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.gibran.core.domain.model.Track
import com.gibran.feature.detail.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TrackDetailScreen(
    modifier: Modifier = Modifier,
    track: Track
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.track_title, track.title)) })
        }
    ) { padding ->
        TrackDetailContent(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .padding(padding),
            track = track,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TrackDetailContent(
    modifier: Modifier = Modifier,
    track: Track,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AsyncImage(
            model = track.albumCoverUrl,
            contentDescription = track.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                stringResource(R.string.artist_label, track.artist),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                stringResource(R.string.album_label, track.albumTitle),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        if (track.previewUrl != null) {
            FullScreenPlayerScreen(
                previewUrl = track.previewUrl
            )
        } else {
            Text(stringResource(R.string.no_preview_available), style = MaterialTheme.typography.bodySmall)
        }

        AssistChip(
            onClick = {},
            label = { Text(stringResource(R.string.track_ranking, track.rank)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null
                )
            }
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                var expanded by remember { mutableStateOf(false) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.details_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        val trackDetails = listOf(
                            stringResource(R.string.duration_label, formatDuration(duration)),
                            stringResource(
                                R.string.explicit_label,
                                stringResource(if (track.explicitLyrics) R.string.yes else R.string.no)
                            )
                        )
                        trackDetails.forEach {
                            Row {
                                Text("•", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.width(6.dp))
                                Text(it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remaining = seconds % 60
    return "%d:%02d".format(minutes, remaining)
}

@Composable
fun FullScreenPlayerScreen(
    previewUrl: String?
) {
    val context = LocalContext.current
    val exoPlayer = rememberExoPlayer(context, previewUrl)

    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var userScrubbing by remember { mutableStateOf(false) }
    var scrubPosition by remember { mutableFloatStateOf(0f) }
    val duration = remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(play: Boolean) {
                isPlaying = play
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    exoPlayer.seekTo(0)
                    exoPlayer.pause()
                    isPlaying = false
                    progress = 0f
                    userScrubbing = false
                    scrubPosition = 0f
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            progress = (exoPlayer.currentPosition / exoPlayer.duration.toFloat()).coerceIn(0f, 1f)
            delay(200)
        }
    }

    LaunchedEffect(Unit) {
        while (duration.longValue <= 0) {
            if (exoPlayer.duration > 0) {
                duration.longValue = exoPlayer.duration
            }
            delay(100)
        }
    }

    val durationSeconds = duration.longValue / 1000
    val currentSeconds = remember(progress) { (progress * durationSeconds).toInt() }

    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAnim"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "%d:%02d".format(currentSeconds / 60, currentSeconds % 60),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "%d:%02d".format(durationSeconds / 60, durationSeconds % 60),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Slider(
            value = if (userScrubbing) scrubPosition else progress,
            onValueChange = {
                userScrubbing = true
                scrubPosition = it
            },
            onValueChangeFinished = {
                val newPosition = (exoPlayer.duration * scrubPosition).toLong()
                exoPlayer.seekTo(newPosition)
                userScrubbing = false
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                exoPlayer.pause()
                exoPlayer.seekTo(0)
                isPlaying = false
                progress = 0f
                userScrubbing = false
                scrubPosition = 0f
            }) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(
                onClick = {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                },
                modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}

@Composable
fun rememberExoPlayer(context: android.content.Context, previewUrl: String?): ExoPlayer {
    return remember(previewUrl) {
        ExoPlayer.Builder(context).build().apply {
            previewUrl?.let {
                val mediaItem = MediaItem.fromUri(it)
                setMediaItem(mediaItem)
                prepare()
            }
        }
    }
}
