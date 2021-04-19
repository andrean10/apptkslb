package com.kontakanprojects.apptkslb.view.guru.kelas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.databinding.ItemsKelasGuruBinding
import com.kontakanprojects.apptkslb.model.kelas.ResultsKelas

class KelasAdapter : RecyclerView.Adapter<KelasAdapter.KelasAdapterViewHolder>() {

    private val listKelas = ArrayList<ResultsKelas>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(kelas: List<ResultsKelas>?) {
        if (kelas == null) return
        listKelas.clear()
        listKelas.addAll(kelas)
        notifyDataSetChanged()
    }

    fun getData(position: Int) = listKelas[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasAdapterViewHolder {
        val binding =
            ItemsKelasGuruBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KelasAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: KelasAdapterViewHolder, position: Int) {
        holder.bind(listKelas[position])
    }

    override fun getItemCount() = listKelas.size

    inner class KelasAdapterViewHolder(private val binding: ItemsKelasGuruBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultsKelas: ResultsKelas) {
            binding.tvKelas.text = resultsKelas.namaKelas

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsKelas) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsKelas: ResultsKelas)
    }
}