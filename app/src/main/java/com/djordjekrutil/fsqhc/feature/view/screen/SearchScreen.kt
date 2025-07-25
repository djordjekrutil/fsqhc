package com.djordjekrutil.fsqhc.feature.view.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.djordjekrutil.fsqhc.R
import com.djordjekrutil.fsqhc.feature.viewmodel.PlacesError
import com.djordjekrutil.fsqhc.feature.viewmodel.PlacesScreenState
import com.djordjekrutil.fsqhc.feature.viewmodel.PlacesViewModel
import com.djordjekrutil.fsqhc.ui.component.CenteredContent
import com.djordjekrutil.fsqhc.ui.component.ExitAppDialog
import com.djordjekrutil.fsqhc.ui.component.LoadingItem
import com.djordjekrutil.fsqhc.ui.component.LoadingPlaceItem
import com.djordjekrutil.fsqhc.ui.component.NoSearchResultsState
import com.djordjekrutil.fsqhc.ui.component.PlaceItem
import com.djordjekrutil.fsqhc.ui.component.SearchHintState
import com.google.accompanist.permissions.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    onItemClick: (String) -> Unit,
    viewModel: PlacesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val hasNextPage by viewModel.hasNextPage.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val debouncePeriod = 500L
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        ExitAppDialog(
            onConfirmExit = { (context as? Activity)?.finish() },
            onDismiss = { showExitDialog = false }
        )
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                val totalItems = lazyListState.layoutInfo.totalItemsCount

                if (lastVisibleItem != null && lastVisibleItem.index == totalItems - 1) {
                    if (hasNextPage) {
                        viewModel.loadNextPage()
                    }
                }
            }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    focusManager.clearFocus()
                }
            }
    }

    LaunchedEffect(locationPermissionState.status) {
        when (locationPermissionState.status) {
            PermissionStatus.Granted -> viewModel.onPermissionGranted()
            is PermissionStatus.Denied -> {
                if (locationPermissionState.status.shouldShowRationale) {
                    viewModel.onPermissionDenied()
                } else {
                    viewModel.onPermissionPermanentlyDenied()
                }
            }
        }
    }

    LaunchedEffect(key1 = state is PlacesScreenState.PermissionRequired) {
        if (state is PlacesScreenState.PermissionRequired) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    val inputStyle = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLeadingIconColor = Color.Gray,
        unfocusedLeadingIconColor = Color.Gray,
        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(0.5.dp, Color.LightGray),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
        ) {
            val inputModifier = Modifier.fillMaxWidth()
            when (state) {
                is PlacesScreenState.PermissionRequired,
                is PlacesScreenState.PermissionDenied,
                is PlacesScreenState.PermissionPermanentlyDenied,
                is PlacesScreenState.LoadingLocation -> {
                    Box(
                        modifier = inputModifier
                            .clickable(
                                enabled = state is PlacesScreenState.PermissionRequired ||
                                    state is PlacesScreenState.PermissionDenied
                            ) {
                                locationPermissionState.launchPermissionRequest()
                            }
                            .padding(16.dp)
                    ) {
                        Row {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                getPlaceholderText(state),
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                else -> {
                    val coroutineScope = rememberCoroutineScope()
                    var searchJob by remember { mutableStateOf<Job?>(null) }

                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            searchJob?.cancel()
                            searchJob = coroutineScope.launch {
                                delay(debouncePeriod)
                                viewModel.search(it)
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        placeholder = { Text(text = stringResource(R.string.search_venues)) },
                        modifier = inputModifier,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = inputStyle
                    )
                }
            }
        }

        Crossfade(targetState = state, label = "search-screen-crossfade") { screenState ->
            when (screenState) {
                is PlacesScreenState.Initial -> {
                    CenteredContent { CircularProgressIndicator() }
                }

                is PlacesScreenState.PermissionRequired -> {
                    PermissionRequestContent(
                        message = stringResource(R.string.location_access_is_required),
                        buttonText = stringResource(R.string.enable_location)
                    ) { locationPermissionState.launchPermissionRequest() }
                }

                is PlacesScreenState.PermissionDenied -> {
                    PermissionRequestContent(
                        message = stringResource(R.string.please_allow_location_access),
                        buttonText = stringResource(R.string.try_again)
                    ) { locationPermissionState.launchPermissionRequest() }
                }

                is PlacesScreenState.PermissionPermanentlyDenied -> {
                    CenteredContent {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.location_access_is_required),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.go_to_settings_permission),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { openAppSettings(context) }) {
                                Text(text = stringResource(R.string.open_settings))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { locationPermissionState.launchPermissionRequest() }) {
                                Text(text = stringResource(R.string.already_changed_settings))
                            }
                        }
                    }
                }

                is PlacesScreenState.LoadingLocation -> {
                    CenteredContent {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = stringResource(R.string.getting_your_location))
                        }
                    }
                }

                is PlacesScreenState.ReadyForSearch -> {
                    SearchHintState()
                }

                is PlacesScreenState.Error -> {
                    CenteredContent {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = getLocationErrorText(screenState.error),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.retryLocation() }) {
                                Text(text = stringResource(R.string.try_again))
                            }
                        }
                    }
                }

                is PlacesScreenState.Searching -> {
                    LazyColumn {
                        items(5) { LoadingPlaceItem() }
                    }
                }

                is PlacesScreenState.Content -> {
                    val places by screenState.places.collectAsState(initial = emptyList())
                    if (places.isEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item { NoSearchResultsState() }
                        }
                    } else {
                        LazyColumn(state = lazyListState) {
                            items(places, key = { place -> place.fsqId }) { place ->
                                PlaceItem(
                                    place = place,
                                    onClick = { onItemClick(place.fsqId) },
                                    onFavoritesClick = viewModel::toggleFavorite
                                )
                            }
                            item {
                                if (hasNextPage) {
                                    LoadingItem()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRequestContent(message: String, buttonText: String, onClick: () -> Unit) {
    CenteredContent {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClick) { Text(buttonText) }
        }
    }
}

@Composable
private fun getPlaceholderText(state: PlacesScreenState): String = when (state) {
    is PlacesScreenState.PermissionRequired, is PlacesScreenState.PermissionDenied -> stringResource(
        R.string.tap_to_enable_location
    )

    is PlacesScreenState.PermissionPermanentlyDenied -> stringResource(R.string.enable_location_in_settings)
    is PlacesScreenState.LoadingLocation -> stringResource(R.string.getting_location)
    else -> "Search..."
}

@Composable
private fun getLocationErrorText(state: PlacesError): String = when (state) {
    PlacesError.Location.FailedToGetLocation -> stringResource(R.string.failed_to_get_location)
    PlacesError.Location.NotAvailable -> stringResource(R.string.location_not_available)
    PlacesError.SearchFailed -> stringResource(R.string.search_failed)
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
