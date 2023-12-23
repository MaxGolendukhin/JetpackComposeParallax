package com.example.parallax

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parallax.ui.theme.ParallaxTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParallaxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray
                ) {
                    val images = listOf(R.drawable.apple, R.drawable.bananas, R.drawable.onion,
                        R.drawable.pears, R.drawable.oranges, R.drawable.tomatoes)
                    val names = listOf("Apple", "Bananas", "Onion", "Pears", "Oranges", "Tomatoes")
                    Content(names.zip(images))
                }
            }
        }
    }
}

@Composable
fun Content(content: List<Pair<String, Int>>) {
    Column {
        Horizontal(content)
    }
}

@Composable
fun Horizontal(content: List<Pair<String, Int>>) {
    val scrollState = rememberScrollState()

    Row (
        modifier = Modifier
            .padding(top = 8.dp)
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content.forEach {
            Box(
                modifier = Modifier
                    .size(256.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(it.second),
                    contentDescription = null,
                    contentScale = object : ContentScale {
                        override fun computeScaleFactor(srcSize: Size, dstSize: Size) =
                            ScaleFactor(1.4f, 1.4f)
                    },
                )
                Text(
                    text = it.first,
                    textAlign = TextAlign.Center,
                    fontSize = 48.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(8f, 0f),
                            blurRadius = 12f
                        )
                    ),
                )
            }
        }
    }
}
