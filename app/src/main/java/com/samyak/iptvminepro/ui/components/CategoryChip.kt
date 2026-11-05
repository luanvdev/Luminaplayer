package com.samyak.iptvminepro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samyak.iptvminepro.ui.theme.InkStroke

/** Pill-shaped filter: amber when selected, dark glass with a hairline otherwise. */
@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isSelected) scheme.primary else scheme.surfaceVariant)
            .border(1.dp, if (isSelected) scheme.primary else InkStroke, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category,
            color = if (isSelected) scheme.onPrimary else scheme.onSurface,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
