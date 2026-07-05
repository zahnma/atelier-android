package com.zahnma.atelier.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.zahnma.atelier.data.model.Brand

@Composable
fun BrandLogo(
    brand: Brand,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .crossfade(true)
            .build()
    }

    val model = remember(brand.id, brand.logoAsset, brand.logoUrl) {
        when {
            !brand.logoAsset.isNullOrBlank() ->
                "file:///android_asset/${brand.logoAsset}"
            !brand.logoUrl.isNullOrBlank() ->
                brand.logoUrl
            else -> null
        }
    }

    val shape = RoundedCornerShape(14.dp)
    val initials = remember(brand.name) { brandInitials(brand.name) }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (model != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(model)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "${brand.name} logo",
                modifier = Modifier
                    .size(size)
                    .clip(shape)
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun brandInitials(name: String): String {
    val words = name.split(" ", "&").filter { it.isNotBlank() }
    return when {
        words.size >= 2 -> "${words.first().first()}${words[1].first()}".uppercase()
        name.isNotBlank() -> name.take(2).uppercase()
        else -> "?"
    }
}
