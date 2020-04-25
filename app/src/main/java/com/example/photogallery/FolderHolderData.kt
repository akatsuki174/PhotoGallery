package com.example.photogallery

import android.net.Uri

data class FolderHolderData (
    val bucketId: String? = null,
    val uri: Uri? = null,
    val name: String,
    val dateTaken: Long
)
