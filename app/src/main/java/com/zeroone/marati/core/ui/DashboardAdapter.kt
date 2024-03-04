package com.zeroone.marati.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeroone.marati.R
import com.zeroone.marati.core.data.source.remote.response.DataItem

class DashboardAdapter(private var list:List<DataItem>?) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val deleteProject: ImageButton = itemView.findViewById(R.id.deleteProject)
        val title: TextView = itemView.findViewById(R.id.title)
        val created_date: TextView = itemView.findViewById(R.id.created_date)
        val server: TextView = itemView.findViewById(R.id.server)
        val description: TextView = itemView.findViewById(R.id.description)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

//    fun setList(newList:List<DataItem>?){
//        list = newList
//        notifyDataSetChanged()
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemViewPosition = list?.get(position)
        holder.title.text = itemViewPosition?.name
        holder.created_date.text = itemViewPosition?.createdAt
        holder.server.text = itemViewPosition?.server
        holder.description.text = itemViewPosition?.description

    }
}