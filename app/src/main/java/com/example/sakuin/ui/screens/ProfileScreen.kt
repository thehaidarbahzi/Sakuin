
package com.example.sakuin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private val ProfileBackground = Color(0xFFF7F9F4)
private val ProfileGreen = Color(0xFF174B22)
private val ProfileDark = Color(0xFF1D2F20)
private val ProfileMuted = Color(0xFF748176)
private val ProfileBorder = Color(0xFFDCE5D8)
private val ProfilePaleGreen = Color(0xFFEBF2E6)
private val ProfileYellow = Color(0xFFF4BD38)
private val ProfileOrange = Color(0xFFF2A344)
private val ProfileRed = Color(0xFFC64D3D)
private val ProfilePaleOrange = Color(0xFFFAEEE9)

@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackupClick: () -> Unit = {},
    onRestoreClick: () -> Unit = {}
) {

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = ProfileBackground,
        bottomBar = {
            ProfileBottomBar(onHomeClick = onHomeClick)
        }
    )
    { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = 28.dp)
        ) {

            Text(
                text = "Profil",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = ProfileDark
            )

            Spacer(modifier = Modifier.height(34.dp))

            // Identitas pengguna
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .background(
                            color = Color(0xFFFBF3DC),
                            shape = CircleShape
                        )
                        .then(
                            Modifier.background(
                                color = Color.Transparent,
                                shape = CircleShape
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = Color(0xFFFBF3DC),
                        border = BorderStroke(2.dp, ProfileYellow)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "U",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProfileGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "username",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "user@email.com",
                    fontSize = 16.sp,
                    color = ProfileMuted
                )
            }

            Spacer(modifier = Modifier.height(42.dp))

            Text(
                text = "PENGATURAN DATA",
                fontSize = 15.sp,
                color = ProfileMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sinkronisasi Data
            ProfileSettingCard(
                icon = Icons.Outlined.CloudUpload,
                title = "Sinkronisasi Data",
                actionText = "Tersinkron",
                description = "Otomatis ke cloud saat online. Saat offline, perubahan disimpan di perangkat.",
                footer = "Sinkronisasi otomatis aktif",
                iconColor = Color(0xFF6790D0),
                iconBackground = ProfilePaleGreen,
                actionColor = Color(0xFF6790D0),
                actionBackground = ProfilePaleGreen,
                footerIcon = Icons.Outlined.CloudDone
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Backup Data
            ProfileSettingCard(
                icon = Icons.Outlined.CloudUpload,
                title = "Backup Data",
                actionText = "Cadangkan",
                description = "Cadangan terakhir: 5 Okt 2026 · 20.15 WIB",
                iconColor = ProfileGreen,
                iconBackground = ProfilePaleGreen,
                actionColor = ProfileGreen,
                actionBackground = ProfilePaleGreen,
                onActionClick = onBackupClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Restore Data
            ProfileSettingCard(
                icon = Icons.Outlined.CloudDownload,
                title = "Restore Data",
                actionText = "Pulihkan",
                description = "Restore akan mengganti data perangkat.",
                iconColor = ProfileOrange,
                iconBackground = ProfilePaleOrange,
                actionColor = ProfileOrange,
                actionBackground = ProfilePaleOrange,
                onActionClick = onRestoreClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Keluar
            ProfileSettingCard(
                icon = Icons.Outlined.Logout,
                title = "Keluar",
                actionText = "Keluar",
                description = "Keluar dari akun Sakuin",
                iconColor = ProfileRed,
                iconBackground = ProfilePaleOrange,
                actionColor = ProfileRed,
                actionBackground = ProfilePaleOrange,
                onActionClick = {
                    showLogoutDialog = true
                },
                titleColor = ProfileRed
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Saat koneksi kembali, data otomatis tersinkron. Backup dan restore memerlukan internet.",
                fontSize = 14.sp,
                color = ProfileMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),

            title = {
                Text(
                    text = "Logout dari Akun?",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileDark,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },

            text = {
                Text(
                    text = "Kamu akan keluar dari akun SAKUIN. Jika ada perubahan yang menunggu sinkronisasi, tunggu hingga tersinkron sebelum logout.",
                    fontSize = 16.sp,
                    color = ProfileMuted,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProfileRed
                    )
                ) {
                    Text(
                        text = "Logout",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },

            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showLogoutDialog = false
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, ProfileBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ProfileDark
                    )
                ) {
                    Text(
                        text = "Batal",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        )
    }
}

@Composable
private fun ProfileSettingCard(
    icon: ImageVector,
    title: String,
    actionText: String,
    description: String,
    iconColor: Color,
    iconBackground: Color,
    actionColor: Color,
    actionBackground: Color,
    modifier: Modifier = Modifier,
    footer: String? = null,
    footerIcon: ImageVector? = null,
    titleColor: Color = ProfileDark,
    onActionClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, ProfileBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(iconBackground, RoundedCornerShape(17.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(29.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onActionClick,
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = actionBackground,
                        contentColor = actionColor
                    ),
                    shape = RoundedCornerShape(30.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(
                        text = actionText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = ProfileMuted,
                lineHeight = 20.sp
            )

            if (footer != null && footerIcon != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = footerIcon,
                        contentDescription = null,
                        tint = ProfileGreen,
                        modifier = Modifier.size(23.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = footer,
                        fontSize = 14.sp,
                        color = ProfileMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileBottomBar(
    onHomeClick: () -> Unit
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
                    tint = ProfileMuted,
                    modifier = Modifier.size(27.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Beranda",
                    fontSize = 13.sp,
                    color = ProfileMuted
                )
            }

            // Profil aktif
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(40.dp)
                        .background(
                            ProfilePaleGreen,
                            RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profil",
                        tint = ProfileGreen,
                        modifier = Modifier.size(27.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Profil",
                    fontSize = 13.sp,
                    color = ProfileGreen
                )
            }
        }
    }
}
