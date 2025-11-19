package com.example.glibicompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glibicompose.data.GhibliCharacter

@Composable
fun CharacterCard(character: GhibliCharacter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF003153)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.name.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Character Info
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = character.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003153)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CharacterAttribute("Gender", character.gender)
                    CharacterAttribute("Age", character.age)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CharacterAttribute("Eyes", character.eyeColor)
                    CharacterAttribute("Hair", character.hairColor)
                }
            }
        }
    }
}

@Composable
fun CharacterAttribute(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0066CC)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
    }
}