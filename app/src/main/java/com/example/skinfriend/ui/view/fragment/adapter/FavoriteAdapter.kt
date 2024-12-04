package com.example.skinfriend.ui.view.fragment.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skinfriend.data.local.entity.SkincareEntity
import com.example.skinfriend.databinding.ListFavoriteBinding
import com.example.skinfriend.ui.view.ResultActivity

class FavoriteAdapter :
    ListAdapter<SkincareEntity, FavoriteAdapter.MyViewHolders>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolders {
        val binding = ListFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolders(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolders, position: Int) {
        val favourite = getItem(position)
        holder.bind(favourite)
        /*holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, ResultActivity::class.java).apply{
                putExtra(ResultActivity.EXTRA_RESULT, favourite.title)
                putExtra(ResultActivity.EXTRA_IMAGE_URI, favourite.mediaCover)
                putExtra(ResultActivity.EXTRA_INTERFERENCE_TIME, favourite.inference)
                putExtra(ResultActivity.EXTRA_DATE, favourite.date)
                putExtra(ResultActivity.EXTRA_FROM_HISTORY, true)
            }
            holder.itemView.context.startActivity(intentDetail)
        }*/
    }

    inner class MyViewHolders(private val binding: ListFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cancer: SkincareEntity) {
//            with(binding){
//
//            }
        }
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SkincareEntity>() {
            override fun areItemsTheSame(oldItem: SkincareEntity, newItem: SkincareEntity): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: SkincareEntity, newItem: SkincareEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}

