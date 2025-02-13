package com.herbarium.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.herbarium.ui.composables.RequestLocationPermission
import com.herbarium.viewmodel.AddPlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AddPlantViewModel = hiltViewModel(),
) {
//    var plantName by rememberSaveable { mutableStateOf("") }
//    var plantDescription by rememberSaveable { mutableStateOf("") }
//    var longitude by rememberSaveable { mutableStateOf("") }
//    var latitude by rememberSaveable { mutableStateOf("") }
//    //var plantImageUri by rememberSaveable(stateSaver = UriSaver) { mutableStateOf(null) }
    var plantImageUri by rememberSaveable(stateSaver = UriSaver) { mutableStateOf<Uri?>(null) }
    LaunchedEffect(navController.currentBackStackEntry) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<String>("photoUri")
            ?.let { uriString ->
                plantImageUri = Uri.parse(uriString)
            }
    }
    val plantName by viewModel.plantName.collectAsState("")
    val plantDescription by viewModel.plantDescription.collectAsState("")
    val longitude by viewModel.longitude.collectAsState("")
    val latitude by viewModel.latitude.collectAsState("")

    //var plantImageUri by rememberSaveable(stateSaver = UriSaver) { mutableStateOf(null) }
    LaunchedEffect(plantImageUri) {
        plantImageUri?.let { uri ->
            viewModel.onPlantPhotoChange(uri)
        }
    }

    var showError by remember { mutableStateOf(false) }

    var galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { plantImageUri = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add a new Plant",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                            navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (plantName.isNotBlank()) {
                                viewModel.onCreatePlant(
                                    name = plantName,
                                    photo = plantImageUri,
                                    description = plantDescription,
                                    latitude = latitude,
                                    longitude = longitude
                                )
                                navController.popBackStack()
                            } else {
                                showError = true
                            }
                        }
                    ) {
                        Text("Save", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Plant Photo",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp)
//                            .clip(MaterialTheme.shapes.medium)
//                            .background(MaterialTheme.colorScheme.surfaceVariant)
//                            .clickable { galleryLauncher.launch("image/*") }
//                            .align(Alignment.CenterHorizontally)
//                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { galleryLauncher.launch("image/*") }) {
                            Text("Choose from Gallery")
                        }

                        Button(onClick = { navController.navigate("camera") }) {
                            Text("Take Photo")
                        }
                    }
//                        {
//                        if (plantImageUri != null) {
//                            // Display selected image
//                            AsyncImage(
//                                model = plantImageUri,
//                                contentDescription = "Selected plant image",
//                                modifier = Modifier.fillMaxSize(),
//                                contentScale = ContentScale.Crop
//                            )
//                        } else {
//                            Text(
//                                text = "Tap to add photo",
//                                modifier = Modifier.padding(10.dp),
//                                style = MaterialTheme.typography.bodySmall
//                            )
//                        }
//                    }
                }
            }

            item {
                PlantInputField(
                    label = "Plant Name",
                    value = plantName,
                    onValueChange = viewModel::onPlantNameChange,
                    isError = showError && plantName.isBlank(),
                    errorMessage = "Plant name is required"
                )
            }

            item {
                PlantInputField(
                    label = "Description",
                    value = plantDescription,
                    onValueChange = viewModel::onPlantDescriptionChange,
                )
            }

            item {
                // Location Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    RequestLocationPermission(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Latitude and Longitude Fields
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = latitude,
                            onValueChange = viewModel::onLatitudeChange,
                            label = { Text("Latitude") },
                            modifier = Modifier.weight(1f),
                        )

                        OutlinedTextField(
                            value = longitude,
                            onValueChange = viewModel::onLongitudeChange,
                            label = { Text("Longitude") },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlantInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = modifier
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            singleLine = singleLine,
            isError = isError,
            keyboardOptions = keyboardOptions,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.small
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = modifier.padding(start = 4.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

private val UriSaver = listSaver<Uri?, Any>(
    save = { listOf(listOf(it?.toString())) },
    restore = { it.firstOrNull()?.let { uriString -> Uri.parse(uriString.toString()) } }
)