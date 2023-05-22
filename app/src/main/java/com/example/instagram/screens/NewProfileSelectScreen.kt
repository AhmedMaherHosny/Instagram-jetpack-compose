package com.example.instagram.screens

import android.content.Context
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.instagram.destinations.NewPostScreenDestination
import com.example.instagram.other.MediaPermissions
import com.example.instagram.other.imageBitmapTemp
import com.example.instagram.other.imageBitmapTempProfile
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.OvalCropShape
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.model.aspectRatios
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.settings.CropType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewProfileSelectWidget(navigator: DestinationsNavigator) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Black)
    MediaPermissions {
        val modalSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
            skipHalfExpanded = false
        )
        val foldersPath = getFoldersWithImages(LocalContext.current)
        var images by remember { mutableStateOf(getImagePaths(foldersPath[0])) }
        val dirList by remember { mutableStateOf(extractLastWord(foldersPath)) }
        var selectedText by remember { mutableStateOf(dirList.first()) }
        var selectedImage by remember { mutableStateOf(images.last()) }
        val crop = remember { mutableStateOf(false) }
        val croppedImage = remember { mutableStateOf<ImageBitmap?>(null) }
        var showDialog = remember { mutableStateOf(false) }
        val lazyVerticalGridScrollState = rememberLazyGridState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(croppedImage.value) {
            if (croppedImage.value != null) {
                navigator.popBackStack()
            }
        }

        ModalBottomSheetLayout(
            sheetState = modalSheetState,
            sheetContent = {
                BackHandler(enabled = modalSheetState.isVisible) {
                    scope.launch {
                        modalSheetState.hide()
                    }
                }
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    items(dirList.size) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    images = getImagePaths(foldersPath[it])
                                    selectedImage = images.last()
                                    selectedText = dirList[it]
                                    scope.launch {
                                        lazyVerticalGridScrollState.scrollToItem(0)
                                        modalSheetState.hide()
                                    }
                                }
                        ) {
                            Column() {
                                Text(text = dirList[it])
                                Spacer(modifier = Modifier.height(15.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                    }
                }
            },
        ) {
            Scaffold(
                Modifier
                    .fillMaxSize()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    ToolBarP(navigator = navigator, crop)
                    ImageCropperWidgetP(selectedImage, crop, croppedImage, showDialog)
                    GridOfImagesP(
                        selectedText,
                        modalSheetState,
                        scope,
                        images,
                        lazyVerticalGridScrollState
                    ) { selectedImagePath ->
                        selectedImage = selectedImagePath
                    }
                }
            }
        }
    }

}

@Composable
fun ToolBarP(
    navigator: DestinationsNavigator,
    crop: MutableState<Boolean>,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = { navigator.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "back",
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "New profile picture",
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(
            onClick = {
                crop.value = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "go forward",
                tint = Color.Blue,
                modifier = Modifier.size(35.dp)
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ImageCropperWidgetP(
    selectedImage: String,
    crop: MutableState<Boolean>,
    croppedImage: MutableState<ImageBitmap?>,
    showDialog: MutableState<Boolean>,
) {
    var imageBitmapLarge: ImageBitmap? by remember { mutableStateOf(null) }
    var imageBitmap by remember { mutableStateOf(imageBitmapLarge) }
    val isLoading = imageBitmapLarge == null
    val loadingTimeout = remember { 10_000L }
    LaunchedEffect(selectedImage) {
        withTimeout(loadingTimeout) {
            imageBitmapLarge = getImageBitmap(selectedImage)
            imageBitmap = imageBitmapLarge
        }
    }
    var isCropping by remember { mutableStateOf(false) }
    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }
    val cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                CropType.Static,
                handleSize.dp,
                4f,
                aspectRatios[3].aspectRatio,
                ContentScale.Fit,
                CropOutlineProperty(OutlineType.Oval, OvalCropShape(0, "Oval")),
                true, true, true, false
            )
        )
    }
    val cropStyle by remember { mutableStateOf(CropDefaults.style()) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.52f),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (imageBitmap != null) {
                ImageCropper(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    imageBitmap = imageBitmap!!,
                    contentDescription = "Image Cropper",
                    cropStyle = cropStyle,
                    cropProperties = cropProperties,
                    crop = crop.value,
                    onCropStart = {
                        isCropping = true
                    }
                ) {
                    croppedImage.value = it
                    isCropping = false
                    crop.value = false
                    showDialog.value = true
                }
            } else if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load image",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            if (showDialog.value) {
                croppedImage.value?.let {
                    imageBitmapTempProfile = it
                    showDialog.value = !showDialog.value
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GridOfImagesP(
    selectedText: String,
    modalSheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    images: List<String>,
    lazyVerticalGridScrollState: LazyGridState,
    onItemClick: (String) -> Unit
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilterMenu(selectedText, modalSheetState, scope)
            Icon(Icons.Default.Face, contentDescription = "camera")
        }
        LazyVerticalGrid(
            state = lazyVerticalGridScrollState,
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .scale(1.01f)
        ) {
            items(images.reversed()) {
                AsyncImage(
                    model = it,
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(
                            width = 1.dp,
                            color = Color.White
                        )
                        .clickable { onItemClick(it) }
                )
            }
        }
    }
}

