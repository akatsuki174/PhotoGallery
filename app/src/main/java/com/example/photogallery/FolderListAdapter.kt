package com.example.photogallery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.list_item_folder.view.*

class FolderListAdapter : BaseAdapter() {
    private var folders = mutableListOf<String>()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_folder, parent, false)
        val item = getItem(position)
        view.name.text = item
        return view
    }

    override fun getItem(position: Int): String {
        return folders[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return folders.size
    }

    fun setItem(items: List<String>) {
        folders = items.toMutableList()
        notifyDataSetChanged()
    }
}
