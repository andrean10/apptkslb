package com.kontakanprojects.apptkslb.view.guru.chapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.databinding.ItemsChapterGuruBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultChapter

class ChaptersAdapter : RecyclerView.Adapter<ChaptersAdapter.ChaptersAdapterViewHolder>() {

    private val listChapter = ArrayList<ResultChapter>()
    private var onItemClickCallBack: OnItemClickCallBack? = null
    private val TAG = ChaptersAdapter::class.simpleName

    fun setData(chapter: List<ResultChapter>?) {
        if (chapter == null) return
        listChapter.clear()
        listChapter.addAll(chapter)
        notifyDataSetChanged()
    }

    fun getData(position: Int) = listChapter[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChaptersAdapterViewHolder {
        val binding =
            ItemsChapterGuruBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChaptersAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: ChaptersAdapterViewHolder, position: Int) {
        holder.bind(listChapter[position])
    }

    override fun getItemCount() = listChapter.size

    inner class ChaptersAdapterViewHolder(private val binding: ItemsChapterGuruBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultChapter: ResultChapter) {
            binding.tvChapter.text = resultChapter.namaChapter

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultChapter) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultChapter: ResultChapter)
    }
}