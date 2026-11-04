package com.example.sakuin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sakuin.ui.components.SakuinLogo
import com.example.sakuin.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SakuinLogo(
                modifier = Modifier
                    .width(220.dp)
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sakuin",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Track Your Money, Manage Your Life",
                fontSize = 12.sp,
                color = SecondaryText,
                textAlign = TextAlign.Center
            )
        }
    }
}