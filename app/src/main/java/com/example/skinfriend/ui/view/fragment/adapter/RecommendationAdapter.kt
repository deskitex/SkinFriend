package com.example.skinfriend.ui.view.fragment.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skinfriend.R
import com.example.skinfriend.data.local.entity.FavoriteEntity
import com.example.skinfriend.data.remote.response.RecommendationsItem
import com.example.skinfriend.databinding.ListRecommendationBinding

class RecommendationAdapter(private val onButtonClickListener: OnButtonClickListener) :
    ListAdapter<RecommendationsItem, RecommendationAdapter.MyViewHolders>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolders {
        val binding = ListRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolders(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolders, position: Int) {
        val recommendation = getItem(position)
        holder.bind(recommendation)
    }

    inner class MyViewHolders(val binding: ListRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecommendationsItem) {
            with(binding){
                scName.text = item.productName
                scPrice.text = item.price
                Glide.with(itemView.context)
                    .load(item.pictureSrc)
                    .into(scImg)

                val effect = item.notableEffects.split(", ")
                val effectViews = listOf(scEffect1, scEffect2, scEffect3)

                // Iterasi untuk masing-masing TextView sesuai dengan efek yang ada
                effectViews.reversed().forEachIndexed { index, view ->
                    if (index < effect.size) {
                        view.text = effect[effect.size - 1 - index]
                        view.visibility = View.VISIBLE
                    } else {
                        view.visibility = View.GONE
                    }
                }

                scFavorite.setOnClickListener {
                    onButtonClickListener.onButtonClicked(item)
                }

                scFavorite.setImageDrawable(
                    if (item.isFavorite)ContextCompat.getDrawable(itemView.context, R.drawable.icon_fluent_favorite)
                    else ContextCompat.getDrawable(itemView.context, R.drawable.icon_outline_favorite)
                )

                scBuy.setOnClickListener{
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(item.productHref)
                        }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecommendationsItem>() {
            override fun areItemsTheSame(oldItem: RecommendationsItem, newItem: RecommendationsItem): Boolean {
                return oldItem.productName == newItem.productName
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: RecommendationsItem, newItem: RecommendationsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnButtonClickListener {
        fun onButtonClicked(item: RecommendationsItem)
    }
}