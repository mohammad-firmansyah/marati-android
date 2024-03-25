package com.zeroone.marati.core.ui

import android.content.Context
import android.content.Intent
import android.graphics.ColorSpace.Model
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
import com.zeroone.marati.core.data.source.remote.response.ModelItem
import com.zeroone.marati.core.utils.Utils
import com.zeroone.marati.home.HomeActivity
import com.zeroone.marati.home.fragments.EditModelFragment
import org.json.JSONObject

class ModelAdapter(
    private val context : Context,
    private var list:List<ModelItem>?,
    private val viewModel:HomeViewModel) : RecyclerView.Adapter<ModelAdapter.ViewHolder>() { var listener : rvClickListener?= null
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val deleteProject: ImageButton = itemView.findViewById(R.id.deleteProject)
        val title: TextView = itemView.findViewById(R.id.title)
        val created_date: TextView = itemView.findViewById(R.id.created_date)
        val description: TextView = itemView.findViewById(R.id.description)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.model_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: ModelAdapter.ViewHolder, position: Int) {

        val itemViewPosition = list?.get(position)
        try {
            if(itemViewPosition?.name?.length!! >= 15){
                holder.title.text = itemViewPosition?.name?.substring(0,15) + "..."
            } else{
                holder.title.text = itemViewPosition?.name
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
            val parent = context as HomeActivity
            val input = JSONObject()
            input.put("name",itemViewPosition?.input?.name)
            input.put("type",itemViewPosition?.input?.type)

            val output = JSONObject()
            output.put("name",itemViewPosition?.output?.name)
            output.put("type",itemViewPosition?.output?.type)

            parent.addFragment(EditModelFragment.newInstance(id=itemViewPosition?.id, name = itemViewPosition?.name,
                description = itemViewPosition?.description,
                owner_id = itemViewPosition?.ownerId,
                category = itemViewPosition?.category,
                input = input.toString(),
                output = output.toString(),
                file = itemViewPosition?.filename))
        }

        holder.deleteProject.setOnClickListener{
 
            Utils.showDialogConfirmation(context,"Delete model","are you sure to delete this model ?", onPositiveClick = {
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