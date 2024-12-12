package com.example.skinfriend.ui.view.fragment.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skinfriend.data.local.entity.FavoriteEntity
import com.example.skinfriend.data.remote.response.RecommendationsItem
import com.example.skinfriend.databinding.ListFavoriteBinding
import com.example.skinfriend.ui.view.fragment.adapter.RecommendationAdapter.OnButtonClickListener

class FavoriteAdapter(private val onButtonClickListener: OnButtonClickListener) :
    ListAdapter<FavoriteEntity, FavoriteAdapter.MyViewHolders>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolders {
        val binding = ListFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolders(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolders, position: Int) {
        val favourite = getItem(position)
        holder.bind(favourite)
    }

    inner class MyViewHolders(private val binding: ListFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavoriteEntity) {
            with(binding){
                scName.text = item.productName
                scPrice.text = item.price

                Glide.with(itemView.context)
                    .load(item.pictureSrc)
                    .into(scImg)

                scEffect1.text = item.notableEffects1

                if (item.notableEffects2.isEmpty()) {
                    scEffect2.visibility = View.GONE
                } else {
                    scEffect2.visibility = View.VISIBLE
                    scEffect2.text = item.notableEffects2
                }

                if (item.notableEffects3.isEmpty()) {
                    scEffect3.visibility = View.GONE
                } else {
                    scEffect3.visibility = View.VISIBLE
                    scEffect3.text = item.notableEffects3
                }

                scBtnBuy.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(item.productHref)
                    }
                    itemView.context.startActivity(intent)
                }
                scCloseFavorite.setOnClickListener {
                    onButtonClickListener.onButtonClicked(item)
                }
            }
        }
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteEntity>() {
            override fun areItemsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
                return oldItem.productName == newItem.productName
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
    interface OnButtonClickListener {
        fun onButtonClicked(item: FavoriteEntity)
    }
}

