package com.samyak.iptvminepro.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.samyak.iptvminepro.model.VegaPost
import com.samyak.iptvminepro.ui.theme.Ink
import com.samyak.iptvminepro.ui.theme.InkStroke

private const val POSTER_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

/**
 * Builds the Coil request used for posters/backdrops. Some providers serve protocol-relative
 * URLs and reject hot-linking, so we normalise the URL and send a browser UA + Referer.
 */
@Composable
fun rememberPosterRequest(imageUrl: String): ImageRequest {
    val context = LocalContext.current
    val resolvedImageUrl = remember(imageUrl) {
        if (imageUrl.startsWith("//")) "https:$imageUrl" else imageUrl
    }

    val refererHeader = remember(resolvedImageUrl) {
        try {
            val host = android.net.Uri.parse(resolvedImageUrl).host
            if (host != null) {
                val parts = host.split(".")
                if (parts.size >= 2) "https://${parts.takeLast(2).joinToString(".")}/" else "https://$host/"
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    return remember(resolvedImageUrl, refererHeader) {
        ImageRequest.Builder(context)
            .data(resolvedImageUrl)
            .apply {
                addHeader("User-Agent", POSTER_USER_AGENT)
                if (refererHeader != null) addHeader("Referer", refererHeader)
            }
            .crossfade(true)
            .build()
    }
}

@Composable
fun MovieCard(
    movie: VegaPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1.05f else 1.0f, label = "MovieCardScale")
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f) // Movie poster ratio
            .scale(scale)
            .clip(shape)
            .background(Ink)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) MaterialTheme.colorScheme.primary else InkStroke,
                shape = shape
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = rememberPosterRequest(movie.image),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF05050F).copy(alpha = 0.92f))
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = movie.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )
        }
    }
}
