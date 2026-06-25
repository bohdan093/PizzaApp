package com.core.pizzaapp.feature.pizzadetail.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.core.pizzaapp.R
import com.core.pizzaapp.feature.pizzalist.domain.Pizza
import com.core.pizzaapp.feature.pizzalist.domain.PizzaVariant
import com.core.pizzaapp.ui.theme.PizzaAccent
import com.core.pizzaapp.ui.theme.PizzaBackground
import com.core.pizzaapp.ui.theme.PizzaCardBg
import com.core.pizzaapp.ui.theme.PizzaChipSelected
import com.core.pizzaapp.ui.theme.PizzaChipUnselected
import com.core.pizzaapp.ui.theme.PizzaEllipse
import com.core.pizzaapp.ui.theme.PizzaTextPrimary
import com.core.pizzaapp.ui.theme.PizzaTextSecondary
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.sqrt

private val MaxPizzaContainerSize = 290.dp
private val ThumbnailSize = 80.dp
private val CarouselHeight = 310.dp

private val NavBarEntrySpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMediumLow,
)
private val ZoomAnimSpec = tween<Float>(durationMillis = 1000, easing = FastOutSlowInEasing)

private fun sizeToImageDp(size: String): Dp = when (size.uppercase()) {
    "S" -> 206.dp
    "L" -> 284.dp
    else -> 254.dp
}

@Composable
fun PizzaDetailScreen(viewModel: PizzaDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PizzaDetailEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PizzaBackground,
    ) { padding ->
        when {
            state.error != null && state.pizzas.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                    Button(
                        onClick = { viewModel.dispatch(PizzaDetailIntent.Retry) },
                        colors = ButtonDefaults.buttonColors(containerColor = PizzaAccent),
                    ) { Text("Retry") }
                }
            }

            state.pizzas.isNotEmpty() -> PizzaDetailContent(
                state = state,
                modifier = Modifier.padding(padding),
                onSelectSize = { id, size ->
                    viewModel.dispatch(PizzaDetailIntent.SelectSize(id, size))
                },
                onIncrease = { id -> viewModel.dispatch(PizzaDetailIntent.IncreaseQuantity(id)) },
                onDecrease = { id -> viewModel.dispatch(PizzaDetailIntent.DecreaseQuantity(id)) },
            )
        }
    }
}

@Composable
private fun PizzaDetailContent(
    state: PizzaDetailState,
    modifier: Modifier = Modifier,
    onSelectSize: (String, String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
) {
    val pizzas = state.pizzas
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { pizzas.size })
    val currentPizza = pizzas[pagerState.currentPage]
    val selectedSize = state.selectedSizeFor(currentPizza.id, currentPizza.defaultSize)
    val quantity = state.quantityFor(currentPizza.id)
    val selectedVariant = currentPizza.variants.find { it.size == selectedSize }
        ?: currentPizza.variants.firstOrNull()
    val totalPrice = (selectedVariant?.price ?: 0.0) * quantity

    val density = LocalDensity.current
    val mainSizeDp by animateDpAsState(
        targetValue = sizeToImageDp(selectedSize),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pizzaSizeOverlay",
    )
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val carouselHalfHeightPx = with(density) { CarouselHeight.toPx() / 2f }
    val maxContainerPx = with(density) { MaxPizzaContainerSize.toPx() }
    val overlayBaseScale = with(density) { mainSizeDp.toPx() } / maxContainerPx
    val overlayTargetScale = if (screenWidthPx > 0f) (screenWidthPx / maxContainerPx) * 2.2f else overlayBaseScale

    val zoomProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val pagerScrollEnabled by remember { derivedStateOf { zoomProgress.value < 0.01f } }

    var canvasTopInWindowPx by remember { mutableFloatStateOf(0f) }
    var canvasHeightPx by remember { mutableFloatStateOf(0f) }
    var selectorTopInWindowPx by remember { mutableFloatStateOf(0f) }
    var selectorHeightPx by remember { mutableFloatStateOf(0f) }
    var innerBoxTopWindowPx by remember { mutableFloatStateOf(0f) }
    var navBarHeightPx by remember { mutableFloatStateOf(0f) }
    var bottomContentHeightPx by remember { mutableFloatStateOf(0f) }
    var overlayGestureScale by remember { mutableFloatStateOf(1f) }

    val backAnim = remember { Animatable(-160f) }
    val titleAnim = remember { Animatable(-120f) }
    val heartAnim = remember { Animatable(160f) }
    val descAnim = remember { Animatable(120f) }
    val sizeAreaAnim = remember { Animatable(120f) }
    val flashAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch { backAnim.animateTo(0f, NavBarEntrySpring) }
            launch { titleAnim.animateTo(0f, NavBarEntrySpring) }
            launch { heartAnim.animateTo(0f, NavBarEntrySpring) }
            launch { descAnim.animateTo(0f, NavBarEntrySpring) }
            launch { sizeAreaAnim.animateTo(0f, NavBarEntrySpring) }
            launch {
                val halfPulse = tween<Float>(durationMillis = 125, easing = LinearEasing)
                repeat(2) {
                    flashAnim.animateTo(0.15f, halfPulse)
                    flashAnim.animateTo(0f, halfPulse)
                }
            }
        }
    }

    val selectorBottomRelDp = with(density) {
        (selectorTopInWindowPx + selectorHeightPx - innerBoxTopWindowPx).coerceAtLeast(0f).toDp()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PizzaCardBg)
            .onGloballyPositioned { coords ->
                canvasTopInWindowPx = coords.localToWindow(Offset.Zero).y
                canvasHeightPx = coords.size.height.toFloat()
            },
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 1f - zoomProgress.value },
        ) {
            drawCircle(
                color = PizzaEllipse,
                radius = 303.dp.toPx(),
                center = Offset(size.width / 2f, size.height * 0.26f),
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            PizzaNavBar(
                pizzaName = currentPizza.name,
                backTranslationX = backAnim.value,
                titleTranslationY = titleAnim.value,
                heartTranslationX = heartAnim.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { navBarHeightPx = it.size.height.toFloat()  }
                    .graphicsLayer { translationY = (-navBarHeightPx-100f) * zoomProgress.value },
            )

            PizzaCarousel(
                pagerState = pagerState,
                pizzas = pizzas,
                state = state,
                flashAlpha = { flashAnim.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CarouselHeight),
                zoomAnim = zoomProgress,
                pagerScrollEnabled = pagerScrollEnabled,
                onPinchIn = {
                    if (!zoomProgress.isRunning) {
                        scope.launch { zoomProgress.animateTo(1f, ZoomAnimSpec) }
                    }
                },
                onPinchOut = {
                    if (!zoomProgress.isRunning) {
                        scope.launch { zoomProgress.animateTo(0f, ZoomAnimSpec) }
                    }
                },
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned {
                        innerBoxTopWindowPx = it.localToWindow(Offset.Zero).y
                        bottomContentHeightPx = it.size.height.toFloat()
                    }
                    .graphicsLayer { translationY = bottomContentHeightPx * zoomProgress.value },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                ) {
                    Spacer(Modifier.height(selectorBottomRelDp))
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = currentPizza.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PizzaTextPrimary,
                        lineHeight = 20.sp,
                        modifier = Modifier.graphicsLayer { translationY = descAnim.value },
                    )
                    Spacer(Modifier.weight(1f))
                    PizzaOrderBar(
                        quantity = quantity,
                        totalPrice = totalPrice,
                        onIncrease = { onIncrease(currentPizza.id) },
                        onDecrease = { onDecrease(currentPizza.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(32.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .graphicsLayer { translationY = sizeAreaAnim.value },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BananaForScale(modifier = Modifier.height(64.dp))
                    PizzaSizeSelector(
                        variants = currentPizza.variants,
                        selectedSize = selectedSize,
                        onSizeSelected = { onSelectSize(currentPizza.id, it) },
                        canvasHeightPx = canvasHeightPx,
                        selectorOffsetFromCanvasTopPx = (selectorTopInWindowPx - canvasTopInWindowPx).coerceAtLeast(0f),
                        modifier = Modifier.onGloballyPositioned {
                            selectorTopInWindowPx = it.localToWindow(Offset.Zero).y
                            selectorHeightPx = it.size.height.toFloat()
                        },
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = if (zoomProgress.value > 0f) 1f else 0f }
                .then(
                    if (!pagerScrollEnabled) Modifier.pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            overlayGestureScale *= zoom
                            if (overlayGestureScale < 0.87f) {
                                if (!zoomProgress.isRunning) {
                                    scope.launch { zoomProgress.animateTo(0f, ZoomAnimSpec) }
                                }
                                overlayGestureScale = 1f
                            }
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = currentPizza.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(MaxPizzaContainerSize)
                    .graphicsLayer {
                        val startTransY = navBarHeightPx + carouselHalfHeightPx - canvasHeightPx / 2f
                        scaleX = lerp(overlayBaseScale, overlayTargetScale, zoomProgress.value)
                        scaleY = lerp(overlayBaseScale, overlayTargetScale, zoomProgress.value)
                        translationY = lerp(startTransY, 0f, zoomProgress.value)
                    }
                    .clip(CircleShape),
            )
        }
    }
}

@Composable
private fun PizzaNavBar(
    pizzaName: String,
    backTranslationX: Float,
    titleTranslationY: Float,
    heartTranslationX: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .graphicsLayer { translationX = backTranslationX },
            shape = CircleShape,
            color = PizzaCardBg,
            shadowElevation = 2.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = PizzaTextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .graphicsLayer { translationY = titleTranslationY },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Pizzas",
                style = MaterialTheme.typography.labelSmall,
                color = PizzaTextSecondary,
            )
            Text(
                text = pizzaName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PizzaTextPrimary,
            )
        }

        Surface(
            modifier = Modifier
                .size(44.dp)
                .graphicsLayer { translationX = heartTranslationX },
            shape = CircleShape,
            color = PizzaCardBg,
            shadowElevation = 2.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_heart),
                    contentDescription = "Favorite",
                    tint = PizzaTextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun PizzaCarousel(
    pagerState: PagerState,
    pizzas: List<Pizza>,
    state: PizzaDetailState,
    flashAlpha: () -> Float,
    modifier: Modifier = Modifier,
    zoomAnim: Animatable<Float, *>,
    pagerScrollEnabled: Boolean,
    onPinchIn: () -> Unit,
    onPinchOut: () -> Unit,
) {
    val density = LocalDensity.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val contentPaddingDp = 45.dp
    val carouselHeightPx = with(density) { CarouselHeight.toPx() }

    val edgeTranslationPx = remember(density, screenWidthDp) {
        with(density) { (screenWidthDp / 2f - contentPaddingDp - ThumbnailSize / 1.5f).toPx() }
    }

    var cumulativeScale by remember { mutableFloatStateOf(1f) }

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = contentPaddingDp),
        modifier = modifier,
        userScrollEnabled = pagerScrollEnabled,
    ) { page ->
        val pizza = pizzas[page]
        val selectedSize = state.selectedSizeFor(pizza.id, pizza.defaultSize)
        val isCurrentPage = pagerState.currentPage == page

        val mainSizeDp by animateDpAsState(
            targetValue = sizeToImageDp(selectedSize),
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "pizzaSize_$page",
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = pizza.imageUrl,
                contentDescription = pizza.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(MaxPizzaContainerSize)
                    .graphicsLayer {
                        val progress = if (isCurrentPage) zoomAnim.value else 0f

                        val rawOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        val absOffset = rawOffset.absoluteValue.coerceIn(0f, 1f)
                        val maxPx = MaxPizzaContainerSize.toPx()
                        val mainPx = mainSizeDp.toPx()
                        val thumbPx = ThumbnailSize.toPx()

                        val baseScale = lerp(thumbPx / maxPx, mainPx / maxPx, 1f - absOffset)

                        scaleX = baseScale
                        scaleY = baseScale
                        translationX = lerp(rawOffset.coerceIn(-1f, 1f) * edgeTranslationPx, 0f, progress)
                        translationY = 0f
                        alpha = if (progress > 0f) 0f else 1f
                    }
                    .clip(CircleShape)
                    .drawWithContent {
                        drawContent()
                        drawRect(Color.White, alpha = flashAlpha())
                    }
                    .pointerInput(isCurrentPage) {
                        if (!isCurrentPage) return@pointerInput
                        detectTransformGestures { _, _, zoom, _ ->
                            cumulativeScale *= zoom
                            when {
                                cumulativeScale > 1.15f -> {
                                    onPinchIn()
                                    cumulativeScale = 1f
                                }
                                cumulativeScale < 0.87f -> {
                                    onPinchOut()
                                    cumulativeScale = 1f
                                }
                            }
                        }
                    },
            )
        }
    }
}

@Composable
private fun BananaForScale(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Banana for scale",
                fontSize = 9.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFFA08060),
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(text = "🍌", fontSize = 30.sp)
        }
    }
}

@Composable
private fun PizzaSizeSelector(
    variants: List<PizzaVariant>,
    selectedSize: String,
    onSizeSelected: (String) -> Unit,
    canvasHeightPx: Float = 0f,
    selectorOffsetFromCanvasTopPx: Float = 0f,
    modifier: Modifier = Modifier,
) {
    val sortedVariants = remember(variants) { variants.sortedBy { sizeOrder(it.size) } }

    ParabolicSizeLayout(
        modifier = modifier,
        canvasHeightPx = canvasHeightPx,
        selectorOffsetFromCanvasTopPx = selectorOffsetFromCanvasTopPx,
    ) {
        sortedVariants.forEach { variant ->
            val isSelected = variant.size == selectedSize
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isSelected) PizzaChipSelected else PizzaChipUnselected,
                shadowElevation = if (isSelected) 0.dp else 2.dp,
                onClick = { onSizeSelected(variant.size) },
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = variant.size.take(1).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else PizzaTextPrimary,
                    )
                }
            }
        }
    }
}

private fun sizeOrder(size: String) = when (size.uppercase()) {
    "S" -> 0
    "M" -> 1
    "L" -> 2
    else -> 3
}

@Composable
private fun ParabolicSizeLayout(
    modifier: Modifier = Modifier,
    canvasHeightPx: Float = 0f,
    selectorOffsetFromCanvasTopPx: Float = 0f,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }
        if (placeables.isEmpty()) return@Layout layout(0, 0) {}

        val buttonSize = placeables.first().width
        val halfButton = buttonSize / 2f
        val gap = 100.dp.roundToPx()
        val totalWidth = gap * 2 + buttonSize

        if (canvasHeightPx > 0f && selectorOffsetFromCanvasTopPx > 0f) {
            val R = 303.dp.roundToPx().toFloat()
            val cy = canvasHeightPx * 0.26f
            val gapF = gap.toFloat()

            val ySLCanvas = cy + sqrt(R * R - gapF * gapF)
            val yMCanvas = cy + R

            val ySLPlace = (ySLCanvas - selectorOffsetFromCanvasTopPx - halfButton).toInt()
            val yMPlace = (yMCanvas - selectorOffsetFromCanvasTopPx - halfButton).toInt()

            val topPad = maxOf(0, -ySLPlace, -yMPlace)
            val finalYSL = ySLPlace + topPad
            val finalYM = yMPlace + topPad
            val totalHeight = maxOf(finalYSL, finalYM) + buttonSize

            layout(totalWidth, totalHeight) {
                placeables.getOrNull(0)?.placeRelative(0, finalYSL)
                placeables.getOrNull(1)?.placeRelative(gap, finalYM)
                placeables.getOrNull(2)?.placeRelative(gap * 2, finalYSL)
            }
        } else {
            val verticalDrop = 33.dp.roundToPx()
            val sideOffset = 12.dp.roundToPx()
            val totalHeight = buttonSize + verticalDrop
            layout(totalWidth, totalHeight) {
                placeables.getOrNull(0)?.placeRelative(0, sideOffset)
                placeables.getOrNull(1)?.placeRelative(gap, verticalDrop)
                placeables.getOrNull(2)?.placeRelative(gap * 2, sideOffset)
            }
        }
    }
}

@Composable
private fun PizzaOrderBar(
    quantity: Int,
    totalPrice: Double,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box {
            Surface(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(50.dp),
                color = PizzaBackground,
            ) {}
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shadowElevation = 2.dp,
                    shape = CircleShape,
                    color = PizzaCardBg,
                    onClick = onDecrease,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_remove),
                            contentDescription = "Decrease",
                            tint = PizzaTextPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = PizzaTextPrimary,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center,
                )

                Surface(
                    modifier = Modifier.size(44.dp),
                    shadowElevation = 2.dp,
                    shape = CircleShape,
                    color = PizzaCardBg,
                    onClick = onIncrease,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Increase",
                            tint = PizzaTextPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "$%.2f".format(totalPrice),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PizzaTextPrimary,
        )

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = PizzaAccent),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.height(48.dp),
        ) {
            Text(
                text = "Add",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
    }
}
