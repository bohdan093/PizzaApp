package com.core.pizzaapp.feature.pizzalist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.core.pizzaapp.feature.pizzalist.domain.Pizza
import com.core.pizzaapp.feature.pizzalist.domain.PizzaVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaListScreen(viewModel: PizzaListViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PizzaListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Pizza Menu") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        PizzaListContent(
            state = state,
            modifier = Modifier.padding(padding),
            onRetry = { viewModel.dispatch(PizzaListIntent.Retry) },
        )
    }
}

@Composable
private fun PizzaListContent(
    state: PizzaListState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.error != null && state.pizzas.isEmpty() -> {
                ErrorContent(
                    message = state.error,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.pizzas, key = { it.id }) { pizza ->
                        PizzaCard(pizza = pizza)
                    }
                }
            }
        }
    }
}

@Composable
private fun PizzaCard(pizza: Pizza, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = pizza.imageUrl,
                contentDescription = pizza.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = pizza.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pizza.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
                PizzaSizeRow(variants = pizza.variants, defaultSize = pizza.defaultSize)
            }
        }
    }
}

@Composable
private fun PizzaSizeRow(variants: List<PizzaVariant>, defaultSize: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        variants.forEach { variant ->
            PizzaSizeChip(
                size = variant.size,
                price = variant.price,
                isSelected = variant.size == defaultSize,
            )
        }
    }
}

@Composable
private fun PizzaSizeChip(size: String, price: Double, isSelected: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                       else MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(52.dp)
                .padding(vertical = 4.dp),
        ) {
            Text(text = size, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(text = "$$price", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Button(onClick = onRetry) { Text("Retry") }
    }
}
