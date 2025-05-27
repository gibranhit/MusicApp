package com.gibran.musicapp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gibran.feature.detail.presentation.ui.TrackDetailScreen
import com.gibran.feature.home.presentation.ui.HomeScreen
import com.gibran.feature.search.presentation.screen.SearchScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeDestination
    ) {
        composable<HomeDestination> {
            HomeScreen(
                onTrackClick = { track ->
                    navController.navigate(
                        TrackDetailDestination(
                            id = track.id,
                            title = track.title,
                            artist = track.artist,
                            albumTitle = track.albumTitle,
                            albumCoverUrl = track.albumCoverUrl,
                            previewUrl = track.previewUrl,
                            duration = track.duration,
                            explicitLyrics = track.explicitLyrics,
                            rank = track.rank
                        )
                    )
                },
                onSearch = {
                    navController.navigate(SearchDestination)
                }
            )
        }


        composable<TrackDetailDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<TrackDetailDestination>()
            val track = args.toTrack()

            TrackDetailScreen(
                track = track,
            )
        }

        composable<SearchDestination> {
            SearchScreen { track ->
                navController.navigate(
                    TrackDetailDestination(
                        id = track.id,
                        title = track.title,
                        artist = track.artist,
                        albumTitle = track.albumTitle,
                        albumCoverUrl = track.albumCoverUrl,
                        previewUrl = track.previewUrl,
                        duration = track.duration,
                        explicitLyrics = track.explicitLyrics,
                        rank = track.rank
                    )
                )
            }
        }
    }
}