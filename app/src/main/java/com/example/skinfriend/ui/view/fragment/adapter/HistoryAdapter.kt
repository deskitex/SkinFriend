package com.example.skinfriend.ui.view.fragment.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skinfriend.R
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.databinding.ListHistoryBinding
import com.example.skinfriend.ui.view.ResultActivity

class HistoryAdapter :
    ListAdapter<HistoryEntity, HistoryAdapter.MyViewHolders>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolders {
        val binding = ListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolders(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolders, position: Int) {
        val history = getItem(position)
        holder.bind(history)
        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, ResultActivity::class.java).apply{
                putExtra(ResultActivity.IMAGE_RESULT, history.imageUri)
                putExtra(ResultActivity.EXTRA_HISTORY, history)
            }
            holder.itemView.context.startActivity(intentDetail)
        }
    }

    inner class MyViewHolders(private val binding: ListHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            with(binding){
                Glide.with(itemView.context)
                    .load(history.imageUri)
                    .into(imageView2)
                textView.text = history.date
                textView2.text = history.skintype
            }
        }
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}