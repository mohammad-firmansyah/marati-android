package com.zeroone.marati.core.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeroone.marati.Home.HomeViewModel
import com.zeroone.marati.R
import com.zeroone.marati.core.data.source.remote.response.DataItem
import com.zeroone.marati.core.utils.Utils

class DashboardAdapter(
    private val context : Context,
    private var list:List<DataItem>?,
    private val viewModel:HomeViewModel) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemViewPosition = list?.get(position)
        try {
            holder.title.text = itemViewPosition?.name?.substring(0,10) + "..."
            holder.created_date.text = Utils.getLocalFormat(itemViewPosition?.createdAt.toString())
            holder.server.text = itemViewPosition?.server?.substring(6,19) + "..."
            holder.description.text = itemViewPosition?.description?.substring(0,50) + "..."
        } catch (e:Exception){
            e.printStackTrace()
        }

        holder.deleteProject.setOnClickListener{

            Utils.showDialogConfirmation(context,"Delete dashboard","are you sure to delete this dashboard ?", onPositiveClick = {
                itemViewPosition?.id?.let { it1 -> viewModel.deleteDashboard(it1) }
            }, onNegativeClick = {
                return@showDialogConfirmation
            })
        }
    }
}