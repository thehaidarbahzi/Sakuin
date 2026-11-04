package com.example.sakuin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.sakuin.R

@Composable
fun SakuinLogo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(
            id = R.drawable.sakuin_logo
        ),
        contentDescription = "Logo Sakuin",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}