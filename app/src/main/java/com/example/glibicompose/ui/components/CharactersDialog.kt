package com.example.glibicompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.glibicompose.R
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.glibicompose.data.GhibliCharacter

@Composable
fun CharactersDialog(
    characters: List<GhibliCharacter>,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header dengan Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF003153),
                                    Color(0xFF005580)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.film_characters),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .offset(x = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Content
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF003153),
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = stringResource(R.string.loading_characters),
                                    color = Color(0xFF666666),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFEBEE),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFFD32F2F),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = errorMessage,
                                        color = Color(0xFFC62828),
                                        textAlign = TextAlign.Center,
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    characters.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(72.dp),
                                    tint = Color(0xFFBDBDBD)
                                )
                                Text(
                                    text = stringResource(R.string.no_characters),
                                    color = Color(0xFF757575),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF8F9FA))
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(characters) { character ->
                                    CharacterCard(character)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========== PREVIEW ==========
@Preview(showBackground = true)
@Composable
fun CharactersDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        CharactersDialog(
            characters = listOf(
                GhibliCharacter(
                    id = "1",
                    name = "Chihiro Ogino",
                    gender = "Female",
                    age = "10",
                    eyeColor = "Brown",
                    hairColor = "Brown"
                ),
                GhibliCharacter(
                    id = "2",
                    name = "Haku",
                    gender = "Male",
                    age = "Unknown",
                    eyeColor = "Green",
                    hairColor = "White"
                ),
                GhibliCharacter(
                    id = "3",
                    name = "Yubaba",
                    gender = "Female",
                    age = "Unknown",
                    eyeColor = "Brown",
                    hairColor = "Gray"
                ),
                GhibliCharacter(
                    id = "4",
                    name = "Character With Very Very Long Name That Should Be Displayed Properly",
                    gender = "Female",
                    age = "25",
                    eyeColor = "Very Bright Blue",
                    hairColor = "Long Black Hair"
                )
            ),
            isLoading = false,
            errorMessage = null,
            onDismiss = { showDialog = false }
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun CharactersDialogLoadingPreview() {
    CharactersDialog(
        characters = emptyList(),
        isLoading = true,
        errorMessage = null,
        onDismiss = {}
    )
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun CharactersDialogEmptyPreview() {
    CharactersDialog(
        characters = emptyList(),
        isLoading = false,
        errorMessage = null,
        onDismiss = {}
    )
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun CharactersDialogErrorPreview() {
    CharactersDialog(
        characters = emptyList(),
        isLoading = false,
        errorMessage = "Failed to load characters. Please check your internet connection and try again.",
        onDismiss = {}
    )
}