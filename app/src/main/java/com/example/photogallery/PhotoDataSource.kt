package com.example.photogallery

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.text.TextUtils

class PhotoDataSource(private val context: Context) {
    companion object {
        private val PHOTO_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        private const val PHOTO_ID = MediaStore.Images.ImageColumns._ID
        private const val PHOTO_DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN
        private const val PHOTO_FILE_PATH = MediaStore.Images.ImageColumns.DATA
        private const val PHOTO_BUCKET_ID = MediaStore.Images.ImageColumns.BUCKET_ID // フォルダの識別子
        private const val PHOTO_FOLDER_NAME = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
        private val PHOTO_PROJECTION = arrayOf(
            PHOTO_BUCKET_ID,
            PHOTO_FOLDER_NAME,
            PHOTO_ID,
            PHOTO_DATE_TAKEN,
            PHOTO_FILE_PATH
        )

        private const val PHOTO_SORT = "$PHOTO_DATE_TAKEN DESC, $PHOTO_ID ASC"
        private const val GROUP_BY = "1) GROUP BY 1,(2"
    }

    fun fetch(): ArrayList<FolderHolderData> {
        val holders = arrayListOf<FolderHolderData>()

        context.contentResolver.query(
            PHOTO_URI,
            PHOTO_PROJECTION,
            GROUP_BY,
            null,
            PHOTO_SORT
        ).use { cursor ->
            if (cursor == null) {
                return@use
            }
            val idIndex = cursor.getColumnIndexOrThrow(PHOTO_ID)
            val dateTakenIndex = cursor.getColumnIndexOrThrow(PHOTO_DATE_TAKEN)
            val filePathIndex = cursor.getColumnIndexOrThrow(PHOTO_FILE_PATH)
            val bucketIdIndex = cursor.getColumnIndexOrThrow(PHOTO_BUCKET_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(PHOTO_FOLDER_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val dateTaken = cursor.getLong(dateTakenIndex)
                val filePath = cursor.getString(filePathIndex)
                val bucketId = cursor.getString(bucketIdIndex)
                val name = cursor.getString(nameIndex)
                if (TextUtils.isEmpty(filePath)) {
                    // ファイルパスが取得できないケースがある
                     continue
                }
                holders.add((FolderHolderData(bucketId = bucketId,
                    uri = ContentUris.withAppendedId(PHOTO_URI, id),
                    name = name,
                    dateTaken = dateTaken)))
            }
        }

        // 日付が新しい順にソート
        holders.sortWith(Comparator { t1, t2 ->
            return@Comparator when {
                t1.dateTaken < t2.dateTaken -> 1
                t1.dateTaken > t2.dateTaken -> -1
                else -> 0
            }
        })

        // 「すべて」フォルダのサムネイルを準備
        val uri = if (holders.count() > 0) holders[0].uri else null

        // 「すべて」フォルダを用意
        holders.add(0, FolderHolderData(uri = uri,
            name = context.getString(R.string.folder_all),
            dateTaken = System.currentTimeMillis()))

        return holders
    }

}
