package com.example.sakuin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog

private val RestoreBackground = Color(0xFFF7F9F4)
private val RestoreGreen = Color(0xFF104B1C)
private val RestoreDark = Color(0xFF1D2F20)
private val RestoreMuted = Color(0xFF748176)
private val RestoreBorder = Color(0xFFDCE5D8)
private val RestorePaleGreen = Color(0xFFEAF1E5)
private val RestorePaleYellow = Color(0xFFFBF3DC)

@Composable
fun RestoreScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onContinueRestoreClick: () -> Unit = {}
) {

    var showRestoreDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = RestoreBackground,
        bottomBar = {
            RestoreBottomBar(
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 28.dp)
        ) {

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Kembali",
                        tint = RestoreDark,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Restore Data",
                    fontSize = 21.sp,
                    color = RestoreDark
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Kartu konfirmasi restore
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, RestoreBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Konfirmasi restore data",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = RestoreDark,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Restore mengambil cadangan manual terakhir dari cloud dan mengganti data perangkat. Ini bukan sinkronisasi otomatis.",
                        fontSize = 16.sp,
                        color = RestoreMuted,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Informasi restore dari cloud
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                RestorePaleGreen,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    RestorePaleYellow,
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudDownload,
                                contentDescription = null,
                                tint = RestoreGreen,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Restore cadangan dari cloud",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RestoreDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Proses ini memerlukan koneksi internet untuk mengambil cadangan terakhir dari cloud.",
                                fontSize = 14.sp,
                                color = RestoreMuted,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Cadangan yang akan dipulihkan
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                RestorePaleGreen,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Cadangan yang akan dipulihkan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RestoreGreen
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "5 Okt 2026 · 20.15 WIB",
                            fontSize = 16.sp,
                            color = RestoreGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Kotak perhatian
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = RestoreBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(
                                Color.White,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Perhatian",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = RestoreDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Restore mengganti semua transaksi perangkat, termasuk yang menunggu sinkronisasi. Buat backup perubahan terbaru sebelum melanjutkan.",
                            fontSize = 14.sp,
                            color = RestoreMuted,
                            lineHeight = 21.sp,
                            textAlign = TextAlign.Justify
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Tombol lanjutkan pemulihan
                    Button(
                        onClick = {
                            showRestoreDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RestoreGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Lanjutkan pemulihan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Pencadangan & pemulihan memerlukan internet.",
                fontSize = 14.sp,
                color = RestoreMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }


    if (showRestoreDialog) {
        Dialog(
            onDismissRequest = {
                showRestoreDialog = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(28.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Ikon cloud download dan loading
                Box(
                    modifier = Modifier.size(116.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF0B481A),
                        strokeWidth = 4.dp,
                        trackColor = Color(0xFFEAF0E5)
                    )

                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .background(
                                Color(0xFFFBF3D9),
                                RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudDownload,
                            contentDescription = null,
                            tint = Color(0xFF0B481A),
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Judul
                Text(
                    text = "Sedang memulihkan data",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F3022),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Deskripsi
                Text(
                    text = "Cadangan terakhir dari cloud sedang dipulihkan untuk menggantikan transaksi di perangkat ini.",
                    fontSize = 15.sp,
                    color = Color(0xFF718073),
                    lineHeight = 23.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Kotak informasi
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDFE6DA),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(
                            Color.White,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Tetap buka aplikasi",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F3022)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Jangan tutup atau tinggalkan Sakuin. Pastikan koneksi internet tetap stabil hingga proses selesai.",
                        fontSize = 14.sp,
                        color = Color(0xFF718073),
                        lineHeight = 21.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RestoreBottomBar(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Beranda
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(onClick = onHomeClick),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Beranda",
                    tint = RestoreMuted,
                    modifier = Modifier.size(27.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Beranda",
                    fontSize = 13.sp,
                    color = RestoreMuted
                )
            }

            // Profil aktif
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(onClick = onProfileClick),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(40.dp)
                        .background(
                            RestorePaleGreen,
                            RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profil",
                        tint = RestoreGreen,
                        modifier = Modifier.size(27.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Profil",
                    fontSize = 13.sp,
                    color = RestoreGreen
                )
            }
        }
    }
}
