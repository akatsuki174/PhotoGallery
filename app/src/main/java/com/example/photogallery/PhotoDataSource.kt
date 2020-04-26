package com.example.photogallery

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

class PhotoDataSource(private val context: Context) {
    companion object {
        private val PHOTO_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        private const val PHOTO_ID = MediaStore.Images.ImageColumns._ID
        private const val PHOTO_DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN
        private const val PHOTO_BUCKET_ID = MediaStore.Images.ImageColumns.BUCKET_ID // フォルダの識別子
        private const val PHOTO_FOLDER_NAME = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
        private val PHOTO_PROJECTION = arrayOf(
            PHOTO_BUCKET_ID,
            PHOTO_FOLDER_NAME,
            PHOTO_ID,
            PHOTO_DATE_TAKEN
        )

        private const val PHOTO_SORT = "$PHOTO_DATE_TAKEN DESC, $PHOTO_ID ASC"
    }

    fun fetch(): ArrayList<FolderHolderData> {
        val holders = arrayListOf<FolderHolderData>()
        val bucketIds = arrayListOf<String>()

        context.contentResolver.query(
            PHOTO_URI,
            PHOTO_PROJECTION,
            null,
            null,
            PHOTO_SORT
        ).use { cursor ->
            if (cursor == null) {
                return@use
            }
            val idIndex = cursor.getColumnIndexOrThrow(PHOTO_ID)
            val dateTakenIndex = cursor.getColumnIndexOrThrow(PHOTO_DATE_TAKEN)
            val bucketIdIndex = cursor.getColumnIndexOrThrow(PHOTO_BUCKET_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(PHOTO_FOLDER_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val dateTaken = cursor.getLong(dateTakenIndex)
                val bucketId = cursor.getString(bucketIdIndex)
                val name = cursor.getString(nameIndex)
                if (bucketIds.contains(bucketId)) {
                    continue
                } else {
                    holders.add((FolderHolderData(bucketId = bucketId,
                        uri = ContentUris.withAppendedId(PHOTO_URI, id),
                        name = name,
                        dateTaken = dateTaken)))
                    bucketIds.add(bucketId)
                }
            }
        }

        // 「すべて」フォルダのURI
        val uri = if (holders.count() > 0) holders[0].uri else null
        // 「すべて」フォルダを用意
        holders.add(0, FolderHolderData(uri = uri,
            name = context.getString(R.string.folder_all),
            dateTaken = System.currentTimeMillis()))

        return holders
    }

}
