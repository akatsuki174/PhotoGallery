package com.example.photogallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 1
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private val adapter = FolderListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permission: String = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            load()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_PERMISSION)
        }
        listView.adapter = adapter
    }

    private fun load() {
        adapter.setItem(viewModel.loadFolderData())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION && permissions.isNotEmpty() && grantResults.isNotEmpty()) {
            val permission = permissions[0]
            val grantResult = grantResults[0]
            val requirePermission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (permission == requirePermission && grantResult == PackageManager.PERMISSION_GRANTED) {
                load()
            } else if (permission == requirePermission && grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                    this, "許可してね", Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
