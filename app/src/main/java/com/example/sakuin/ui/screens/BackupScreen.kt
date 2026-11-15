package com.example.sakuin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.CircularProgressIndicator

private val BackupBackground = Color(0xFFF7F9F4)
private val BackupGreen = Color(0xFF104B1C)
private val BackupDark = Color(0xFF1D2F20)
private val BackupMuted = Color(0xFF748176)
private val BackupBorder = Color(0xFFDCE5D8)
private val BackupPaleGreen = Color(0xFFEAF1E5)
private val BackupPaleYellow = Color(0xFFFBF3DC)

@Composable
fun BackupScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onContinueBackupClick: () -> Unit = {}
) {

    var showBackupDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = BackupBackground,
        bottomBar = {
            BackupBottomBar(
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
                        tint = BackupDark,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Backup Data",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Normal,
                    color = BackupDark
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Kartu konfirmasi backup
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    BackupBorder
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Konfirmasi backup manual",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BackupDark,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Buat cadangan manual data saat ini ke cloud. Backup terpisah dari sinkronisasi otomatis dan dapat dipulihkan nanti.",
                        fontSize = 16.sp,
                        color = BackupMuted,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Informasi backup ke cloud
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                BackupPaleGreen,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    BackupPaleYellow,
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudUpload,
                                contentDescription = null,
                                tint = BackupGreen,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cadangan manual ke cloud",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BackupDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Backup memerlukan internet. Sinkronisasi otomatis tetap berjalan terpisah saat online.",
                                fontSize = 14.sp,
                                color = BackupMuted,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Backup terakhir
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                BackupPaleGreen,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Cadangan terakhir",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BackupGreen
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "5 Okt 2026 · 20.15 WIB",
                            fontSize = 16.sp,
                            color = BackupGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Tombol lanjutkan
                    Button(
                        onClick = {
                            showBackupDialog = true
                            onContinueBackupClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BackupGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Lanjutkan pencadangan",
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
                color = BackupMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }


    if (showBackupDialog) {
        Dialog(
            onDismissRequest = {
                showBackupDialog = false
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

                // Ikon dan indikator loading
                Box(
                    modifier = Modifier.size(116.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        color = BackupGreen,
                        strokeWidth = 4.dp,
                        trackColor = BackupPaleGreen
                    )

                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .background(
                                BackupPaleYellow,
                                RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            tint = BackupGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Judul popup
                Text(
                    text = "Sedang mencadangkan data",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackupDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Deskripsi
                Text(
                    text = "Cadangan manual sedang dibuat di cloud, terpisah dari sinkronisasi otomatis.",
                    fontSize = 15.sp,
                    color = BackupMuted,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Informasi proses
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, BackupBorder),
                            RoundedCornerShape(20.dp)
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
                        color = BackupDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Jangan tutup atau tinggalkan Sakuin. Pastikan koneksi internet tetap stabil hingga proses selesai.",
                        fontSize = 14.sp,
                        color = BackupMuted,
                        lineHeight = 21.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BackupBottomBar(
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
                    tint = BackupMuted,
                    modifier = Modifier.size(27.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Beranda",
                    fontSize = 13.sp,
                    color = BackupMuted
                )
            }

            // Profil
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
                            BackupPaleGreen,
                            RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profil",
                        tint = BackupGreen,
                        modifier = Modifier.size(27.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Profil",
                    fontSize = 13.sp,
                    color = BackupGreen
                )
            }
        }
    }
}
