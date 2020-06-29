package com.app.covidanalysis.ui.home.news

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.covidanalysis.R
import com.app.covidanalysis.model.NewsResponse
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_news.view.*
import javax.inject.Inject

class NewsRecyclerAdapter @Inject constructor() :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @Inject lateinit var glideInstance:RequestManager

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsResponse.Article>() {

        override fun areItemsTheSame(oldItem: NewsResponse.Article, newItem: NewsResponse.Article): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NewsResponse.Article, newItem: NewsResponse.Article): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return BlogPostViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_news,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BlogPostViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<NewsResponse.Article>) {
        differ.submitList(list)
    }

    inner class BlogPostViewHolder
    constructor(
            itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: NewsResponse.Article) = with(itemView) {
            itemView.apply {
                tvTitle.text = item.title
                tvDesc.text = item.description
                glideInstance.load(item.urlToImage).into(img)
                tvWebsite.text = item.url
                tvWebsite.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(item.url)
                    context.startActivity(intent)
                }

            }
        }
    }
}