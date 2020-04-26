package com.example.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class FolderListViewModel(application: Application) : AndroidViewModel(application) {

    fun loadFolderData(): ArrayList<FolderHolderData> {
        val dataSource = PhotoDataSource(getApplication())
        return dataSource.fetch()
    }
}
