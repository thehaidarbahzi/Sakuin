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
fun SakuinLogo2(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(
            id = R.drawable.sakuin_logo2
        ),
        contentDescription = "Logo Sakuin",
        modifier = Modifier.width(120.dp),
        contentScale = ContentScale.Fit
    )
}