package com.samyak.iptvminepro.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.samyak.iptvminepro.R
import com.samyak.iptvminepro.model.Channel
import com.samyak.iptvminepro.model.VegaPost
import com.samyak.iptvminepro.model.VegaCatalog
import com.samyak.iptvminepro.model.ProviderType
import com.samyak.iptvminepro.provider.ChannelsProvider
import com.samyak.iptvminepro.provider.ExtensionRepository
import com.samyak.iptvminepro.ui.components.ChannelCard
import com.samyak.iptvminepro.ui.components.MovieCard
import com.samyak.iptvminepro.ui.components.rememberPosterRequest
import com.samyak.iptvminepro.ui.theme.Ink
import com.samyak.iptvminepro.ui.theme.InkStroke
import com.samyak.iptvminepro.ui.theme.Midnight
import com.samyak.iptvminepro.ui.theme.VioletDeep
import com.samyak.iptvminepro.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ChannelsProvider = viewModel(),
    homeViewModel: HomeViewModel,            // Must be Activity-scoped (passed from MainApp)
    navController: NavController,
    onChannelClick: (Channel) -> Unit = {}
) {
    val context = LocalContext.current
    val channels by viewModel.channels.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.error.observeAsState(null)

    // User-added direct video URLs (grouped into a single "My Videos" home card).
    val providers by viewModel.providers.observeAsState(emptyList())
    val videoProviders = remember(providers) {
        providers.filter { it.safeType == ProviderType.VIDEO && it.isActive }
    }

    // VOD movie state – backed by Activity-scoped ViewModel so it survives back-navigation.
    // Returning from MovieDetailScreen reuses cached data; no API call, no spinner.
    val isMoviesLoading by homeViewModel.isMoviesLoading.collectAsState()
    val moviesByCategory by homeViewModel.moviesByCategory.collectAsState()
    val selectedProvider by homeViewModel.selectedProvider.collectAsState()
    val selectedScraper by homeViewModel.selectedScraper.collectAsState()

    val extensionRepo = remember { ExtensionRepository.getInstance(context) }
    val installedExtensionsState by extensionRepo.installedExtensionsFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadIfNeeded()
    }

    // Delegate loading to ViewModel – internal guards skip the call on back-navigation
    LaunchedEffect(installedExtensionsState) {
        homeViewModel.loadMoviesIfNeeded(installedExtensionsState)
    }

    val openMovie: (VegaPost) -> Unit = { movie ->
        val provider = selectedProvider
        val scraper = selectedScraper
        if (provider != null && scraper != null) {
            val encodedLink = Uri.encode(movie.link)
            val encodedProviderUrl = Uri.encode(provider.url)
            val scraperValue = scraper.value
            navController.navigate("movie_detail?link=$encodedLink&providerUrl=$encodedProviderUrl&scraperValue=$scraperValue")
        }
    }

    val openCategory: (VegaCatalog) -> Unit = { catalog ->
        val provider = selectedProvider
        val scraper = selectedScraper
        if (provider != null && scraper != null) {
            val encodedTitle = Uri.encode(catalog.title)
            val encodedFilter = Uri.encode(catalog.filter)
            val encodedProviderUrl = Uri.encode(provider.url)
            val scraperValue = scraper.value
            navController.navigate("category_movies?categoryName=$encodedTitle&categoryFilter=$encodedFilter&providerUrl=$encodedProviderUrl&scraperValue=$scraperValue")
        }
    }

    // The first poster of the first non-empty row becomes the featured hero card.
    val hero = remember(moviesByCategory) {
        moviesByCategory.entries.firstOrNull { it.value.isNotEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if ((isLoading && channels.isEmpty()) || (isMoviesLoading && moviesByCategory.isEmpty())) {
                com.samyak.iptvminepro.ui.components.HomeShimmerPlaceholder(
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            } else {
                val chunkedChannels = remember(channels) { channels.chunked(2) }

                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (hero != null) {
                        item {
                            HeroCard(
                                movie = hero.value.first(),
                                label = hero.key.title,
                                onWatch = { openMovie(hero.value.first()) },
                                onSeeAll = { openCategory(hero.key) }
                            )
                        }
                    }

                    if (videoProviders.isNotEmpty()) {
                        item {
                            MyVideosCard(
                                count = videoProviders.size,
                                onClick = { navController.navigate("video_list") }
                            )
                        }
                    }

                    if (moviesByCategory.isNotEmpty()) {
                        moviesByCategory.forEach { (catalog, movies) ->
                            item {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    SectionHeader(
                                        title = catalog.title,
                                        onSeeAll = { openCategory(catalog) }
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(movies) { movie ->
                                            MovieCard(
                                                movie = movie,
                                                onClick = { openMovie(movie) },
                                                modifier = Modifier.width(132.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (channels.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Live TV Channels", onSeeAll = null)
                        }
                        items(chunkedChannels) { rowChannels ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowChannels.forEach { channel ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ChannelCard(channel = channel, onClick = {
                                            onChannelClick(channel)
                                        })
                                    }
                                }
                                if (rowChannels.size < 2) {
                                    repeat(2 - rowChannels.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    } else if (!isLoading && !isMoviesLoading && moviesByCategory.isEmpty() && videoProviders.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.undraw_files_missing_ntwe),
                                    contentDescription = stringResource(id = R.string.desc_no_channels),
                                    modifier = Modifier.size(150.dp).padding(bottom = 16.dp)
                                )
                                Text(
                                    text = "No content available",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Overlay loading indicator for background fetches
            if ((isLoading && channels.isNotEmpty()) || (isMoviesLoading && moviesByCategory.isNotEmpty())) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (onSeeAll != null) {
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onSeeAll)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

/** Large featured poster with a white "Watch now" pill and a frosted secondary action. */
@Composable
private fun HeroCard(
    movie: VegaPost,
    label: String,
    onWatch: () -> Unit,
    onSeeAll: () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clip(shape)
            .background(Ink)
            .border(1.dp, InkStroke, shape)
            .clickable(onClick = onWatch)
    ) {
        AsyncImage(
            model = rememberPosterRequest(movie.image),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.30f to Color.Transparent,
                        0.70f to Midnight.copy(alpha = 0.70f),
                        1.00f to Midnight.copy(alpha = 0.97f)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onWatch)
                        .padding(start = 14.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF0A0A1F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Watch now",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0A0A1F)
                    )
                }
                Text(
                    text = "See all",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                        .clickable(onClick = onSeeAll)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                )
            }
        }
    }
}

@Composable
private fun MyVideosCard(count: Int, onClick: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(listOf(VioletDeep, Ink)))
            .border(1.dp, InkStroke, shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PlayCircleFilled,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "My Videos",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = if (count == 1) "1 video" else "$count videos",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}
