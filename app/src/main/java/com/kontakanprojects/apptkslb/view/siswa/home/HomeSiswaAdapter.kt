package com.kontakanprojects.apptkslb.view.siswa.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.databinding.ItemsMapelBinding
import com.kontakanprojects.apptkslb.model.mapel.ResultsMapel

class HomeSiswaAdapter : RecyclerView.Adapter<HomeSiswaAdapter.HomeSiswaViewHolder>() {

    private val listMapel = ArrayList<ResultsMapel>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(mapel: List<ResultsMapel>?) {
        if (mapel == null) return
        listMapel.clear()
        listMapel.addAll(mapel)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSiswaViewHolder {
        val binding =
            ItemsMapelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeSiswaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeSiswaViewHolder, position: Int) {
        holder.bind(listMapel[position])
    }

    override fun getItemCount() = listMapel.size

    inner class HomeSiswaViewHolder(private val binding: ItemsMapelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultMapel: ResultsMapel) {
            with(binding) {
                tvMapel.text = resultMapel.namaMapel

                itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultMapel) }
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultMapel: ResultsMapel)
    }
}