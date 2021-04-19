package com.kontakanprojects.apptkslb.view.guru.uploadmateri.soal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.databinding.ItemsLevelGuruBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultsSoalByChapter

class SoalLevelAdapter : RecyclerView.Adapter<SoalLevelAdapter.SoalLevelViewHolder>() {

    private val listSoalLevel = ArrayList<ResultsSoalByChapter>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(responseSoalByChapter: List<ResultsSoalByChapter>?) {
        if (responseSoalByChapter == null) return
        listSoalLevel.clear()
        listSoalLevel.addAll(responseSoalByChapter)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoalLevelViewHolder {
        val binding =
            ItemsLevelGuruBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoalLevelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoalLevelViewHolder, position: Int) {
        holder.bind(listSoalLevel[position])
    }

    override fun getItemCount() = listSoalLevel.size

    inner class SoalLevelViewHolder(private val binding: ItemsLevelGuruBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultsSoalByChapter: ResultsSoalByChapter) {
            binding.tvLevel.text = resultsSoalByChapter.level

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsSoalByChapter) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsSoalByChapter: ResultsSoalByChapter)
    }
}