package com.example.glibicompose.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.glibicompose.R
import com.example.glibicompose.data.GhibliCharacter

@Composable
fun CharacterCard(character: GhibliCharacter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar with gradient
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF003153),
                                    Color(0xFF005580)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = character.name.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Character Name - dapat menampilkan nama panjang
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = character.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003153),
                        lineHeight = 24.sp
                        // Tidak ada maxLines, sehingga nama panjang akan tampil penuh
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Character Details - dengan layout yang lebih baik
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row pertama
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CharacterAttributeChip(
                        label = stringResource(R.string.gender),
                        value = character.gender,
                        modifier = Modifier.weight(1f)
                    )
                    CharacterAttributeChip(
                        label = stringResource(R.string.age),
                        value = character.age,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row kedua
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CharacterAttributeChip(
                        label = stringResource(R.string.eyes),
                        value = character.eyeColor,
                        modifier = Modifier.weight(1f)
                    )
                    CharacterAttributeChip(
                        label = stringResource(R.string.hair),
                        value = character.hairColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterAttributeChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF0F7FF),
        border = BorderStroke(1.dp, Color(0xFFD0E4FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value.ifEmpty { "-" },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0066CC),
                lineHeight = 18.sp
                // Tidak ada maxLines, sehingga teks panjang akan tampil penuh
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun CharacterCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CharacterCard(
            character = GhibliCharacter(
                id = "1",
                name = "Chihiro Ogino",
                gender = "Female",
                age = "10",
                eyeColor = "Brown",
                hairColor = "Brown"
            )
        )
        CharacterCard(
            character = GhibliCharacter(
                id = "2",
                name = "Haku",
                gender = "Male",
                age = "Unknown",
                eyeColor = "Green",
                hairColor = "White"
            )
        )
        CharacterCard(
            character = GhibliCharacter(
                id = "3",
                name = "Character With Very Very Long Name That Should Be Displayed Properly Without Truncation",
                gender = "Female",
                age = "25",
                eyeColor = "Very Bright Blue Color",
                hairColor = "Long Beautiful Black Hair"
            )
        )
    }
}