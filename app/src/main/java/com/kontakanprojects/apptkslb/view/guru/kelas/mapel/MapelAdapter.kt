package com.kontakanprojects.apptkslb.view.guru.kelas.mapel

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.databinding.ItemsMapelGuruBinding
import com.kontakanprojects.apptkslb.model.mapel.ResultsMapel

class MapelAdapter : RecyclerView.Adapter<MapelAdapter.MapelAdapterViewHolder>() {

    private val listMapel = ArrayList<ResultsMapel>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    private val TAG = MapelAdapter::class.simpleName

    fun setData(kelas: List<ResultsMapel>?) {
        if (kelas == null) return
        listMapel.clear()
        listMapel.addAll(kelas)
        notifyDataSetChanged()

        Log.d(TAG, "setData: $kelas")
    }

    fun getData(position: Int) = listMapel[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapelAdapterViewHolder {
        val binding =
            ItemsMapelGuruBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MapelAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: MapelAdapterViewHolder, position: Int) {
        holder.bind(listMapel[position])
    }

    override fun getItemCount() = listMapel.size

    inner class MapelAdapterViewHolder(private val binding: ItemsMapelGuruBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultsMapel: ResultsMapel) {
            binding.tvMapel.text = resultsMapel.namaMapel

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsMapel) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsMapel: ResultsMapel)
    }
}