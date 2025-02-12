package com.herbarium.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.herbarium.data.model.Plant
import com.herbarium.ui.navigation.PlantDetailDestination
import com.herbarium.ui.theme.HerbariumTheme
import com.herbarium.viewmodel.PlantListViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PlantListViewModel = hiltViewModel(),
) {
    val plantList = viewModel.plantList.collectAsState(initial = listOf()).value
    val isLoading by viewModel.isLoading.collectAsState(initial = false)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Plants") },
                actions = {
                    IconButton(onClick = { navController.navigate("sign_up") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Login"
                        )
                    }

                    IconButton(onClick = { viewModel.getPlants() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_plant") },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Plant"
                )
            }
        }
    ) { innerPadding ->
        if (plantList?.isEmpty() == true) {
            EmptyStateUI(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = plantList.orEmpty(),
                    key = { _, plant -> plant.name }
                ) { _, plant ->
                    PlantListItem(
                        plant = plant,
                        onEditClick = {
                            navController.navigate(
                                PlantDetailDestination.createRouteWithParam(
                                    plantId = plant.id,
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlantListItem(
    plant: Plant,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            plant.photo?.let {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .data(plant.photo)
                        .build(),
                    contentDescription = "Plant image",
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.DarkGray),
                    contentScale = ContentScale.Fit
                )
            } ?: run {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "No Photo",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
            }

            Text(
                text = plant.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Plant Details",
                )
            }
        }
    }
}

@Composable
private fun AddPlantButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
        )
    }
}

@Composable
fun EmptyStateUI(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No plants found!\nTap + to add a new plant",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PlantDetailsButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
fun PreviewPlantListItem() {
    val plant = Plant(
        id = "1",
        userId = "user1",
        name = "Test Plant",
        description = "A test plant description.",
        photo = ByteArray(128) { Random.nextBytes(1)[0] },
        location = null
    )
    HerbariumTheme {
        PlantListItem(
            plant = plant,
            onEditClick = {}
        )
    }
}

@Preview
@Composable
fun PreviewAddPlantButton() {
    HerbariumTheme {
        AddPlantButton(onClick = {})
    }
}

@Preview
@Composable
fun PreviewEmptyStateUI() {
    HerbariumTheme {
        EmptyStateUI()
    }
}