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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF003153))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Karakter Film",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
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
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFF003153))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Memuat karakter...",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    characters.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Tidak ada data karakter",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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