package com.zeroone.marati.core.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeroone.marati.edit.EditActivity
import com.zeroone.marati.home.HomeViewModel
import com.zeroone.marati.R
import com.zeroone.marati.core.data.source.remote.response.DashboardItem
import com.zeroone.marati.core.utils.Utils

class DashboardAdapter(
    private val context : Context,
    private var list:List<DashboardItem>?,
    private val viewModel:HomeViewModel) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() { var listener : rvClickListener?= null
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
            if(itemViewPosition?.name?.length!! >= 15){
                holder.title.text = itemViewPosition?.name?.substring(0,15) + "..."
            } else{
                holder.title.text = itemViewPosition?.name
            }

            if (itemViewPosition?.server?.length!! >= 15){
                holder.server.text = itemViewPosition?.server?.substring(6,19) + "..."
            }else{
                holder.server.text = itemViewPosition?.server
            }

            if(itemViewPosition?.description?.length!! >= 50){
                holder.description.text = itemViewPosition?.description?.substring(0,50) + "..."
            }else{
                holder.description.text = itemViewPosition?.description
            }

            holder.created_date.text = Utils.getLocalFormat(itemViewPosition?.createdAt.toString())

        } catch (e:Exception){
            e.printStackTrace()
        }

        holder.itemView.setOnClickListener {
            Log.d("Dashboardadapter","haha")
            val intent = Intent(holder.itemView.context,EditActivity::class.java)
            intent.putExtra("id",itemViewPosition?.id)
            holder.itemView.context.startActivity(intent)
            list?.get(position)?.let { it1 -> listener?.onItemClicked(it, it1) }
        }

        holder.deleteProject.setOnClickListener{

            Utils.showDialogConfirmation(context,"Delete dashboard","are you sure to delete this dashboard ?", onPositiveClick = {
                if(itemViewPosition?.id != null && itemViewPosition.ownerId != null){
                    viewModel.deleteDashboard(itemViewPosition.id,itemViewPosition.ownerId)
                }
            }, onNegativeClick = {
                return@showDialogConfirmation
            })
        }
    }

    interface rvClickListener{
        fun onItemClicked(view:View,data:DashboardItem)
    }
}