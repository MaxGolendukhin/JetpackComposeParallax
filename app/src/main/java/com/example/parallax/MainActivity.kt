package com.example.parallax

import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parallax.ui.theme.ParallaxTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.collectAsState
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    val density = Resources.getSystem().displayMetrics.density

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private lateinit var gyroscopeEventListener: SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gyroscopeX = MutableStateFlow(0f)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        gyroscopeEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (abs(it.values[0]) < .2f && abs(it.values[1]) < 0.3f)
                        gyroscopeX.value = it.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
        }

        setContent {
            ParallaxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val images = listOf(R.drawable.apple, R.drawable.bananas, R.drawable.onion,
                        R.drawable.pears, R.drawable.oranges, R.drawable.tomatoes)
                    val names = listOf("Apple", "Bananas", "Onion", "Pears", "Oranges", "Tomatoes")
                    Content(names.zip(images), gyroscopeX)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensor?.let {
            sensorManager.registerListener(
                gyroscopeEventListener,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(gyroscopeEventListener)
    }
}

@Composable
fun Content(content: List<Pair<String, Int>>, gyroscopeX: StateFlow<Float>) {
    val density = Resources.getSystem().displayMetrics.density
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeigth = LocalConfiguration.current.screenHeightDp
    val widthInPixels = density * screenWidth
    val heigthInPixels = density * screenHeigth

    Surface(color = Color.Gray) {
        Column {
            Horizontal(content, widthInPixels)
            Vertical(content, heigthInPixels, gyroscopeX)
        }
    }
}

@Composable
fun Horizontal(content: List<Pair<String, Int>>, widthInPixels: Float) {
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
                var parallaxOffset by rememberSaveable { mutableFloatStateOf(0f) }

                Image(
                    painter = painterResource(it.second),
                    contentDescription = null,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        parallaxOffset = coordinates.positionInRoot().x / widthInPixels
                    },
                    alignment = BiasAlignment(parallaxOffset, 0f),
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

@Composable
fun Vertical(
    content: List<Pair<String, Int>>,
    heightInPixels: Float,
    gyroscopeX: StateFlow<Float>,
) {
    val scrollState = rememberScrollState()
    val axisX = gyroscopeX.collectAsState()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content.forEach {
            Box(
                modifier = Modifier
                    .height(256.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                var parallaxOffset by rememberSaveable { mutableFloatStateOf(0f) }

                Image(
                    painter = painterResource(it.second),
                    contentDescription = null,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        parallaxOffset = coordinates.positionInRoot().y / heightInPixels
                    },
                    alignment = BiasAlignment(axisX.value, parallaxOffset),
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
