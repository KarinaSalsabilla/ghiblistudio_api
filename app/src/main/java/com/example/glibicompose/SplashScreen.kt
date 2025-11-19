package com.example.glibicompose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.glibicompose.ui.theme.GlibiComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

@Composable
fun SplashScreen(onNavigateToSearch: () -> Unit) {
    var filmCover by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showContent by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Animasi entrance
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    // Fetch film untuk cover
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val cover = withContext(Dispatchers.IO) {
                    val url = URL("https://ghibliapi.vercel.app/films")
                    val response = url.readText()
                    val jsonArray = JSONArray(response)

                    if (jsonArray.length() > 0) {
                        val randomIndex = (0 until jsonArray.length()).random()
                        jsonArray.getJSONObject(randomIndex).getString("image")
                    } else null
                }
                filmCover = cover
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E), // Deep blue
                        Color(0xFF283593),
                        Color(0xFF3949AB),
                        Color(0xFF5C6BC0)
                    )
                )
            )
    ) {
        // Decorative circles (optional)
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .alpha(0.1f)
                .background(Color.White, shape = RoundedCornerShape(50))
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .alpha(0.1f)
                .background(Color.White, shape = RoundedCornerShape(50))
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (showContent) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Title Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "STUDIO GHIBLI",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 4.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Setiap Karya, Penuh Keajaiban",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Serif,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Film Cover Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        // Loading skeleton
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .aspectRatio(2f / 3f)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .shimmerEffect()
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        }
                    }

                    filmCover != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .aspectRatio(2f / 3f)
                                .shadow(
                                    elevation = 24.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    spotColor = Color.Black.copy(alpha = 0.5f)
                                )
                        ) {
                            AsyncImage(
                                model = filmCover,
                                contentDescription = "Ghibli Film Cover",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White.copy(alpha = 0.05f)),
                                contentScale = ContentScale.Crop
                            )

                            // Subtle border
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.3f)
                                            ),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Button Section
            if (!isLoading) {
                Column(
                    modifier = Modifier.padding(bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedButton(
                        text = "Mulai Petualangan",
                        onClick = onNavigateToSearch
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Jelajahi koleksi film Studio Ghibli",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 8.dp else 16.dp,
                shape = RoundedCornerShape(30.dp),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .width(240.dp)
            .height(56.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF00B4DB),
                        Color(0xFF0083B0)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            letterSpacing = 0.5.sp
        )
    }
}

// Shimmer effect untuk loading
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    return this.alpha(alpha)
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    GlibiComposeTheme {
        SplashScreen(onNavigateToSearch = { })
    }
}