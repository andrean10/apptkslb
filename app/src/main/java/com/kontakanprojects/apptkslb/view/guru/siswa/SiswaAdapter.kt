package com.kontakanprojects.apptkslb.view.guru.siswa

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.ItemsSiswaBinding
import com.kontakanprojects.apptkslb.model.guru.ResultsSiswa

class SiswaAdapter : RecyclerView.Adapter<SiswaAdapter.SiswaViewHolder>() {

    private val listSiswa = ArrayList<ResultsSiswa>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    private val TAG = SiswaAdapter::class.simpleName

    fun setData(resultsSiswa: List<ResultsSiswa>?) {
        if (resultsSiswa == null) return
        listSiswa.clear()
        listSiswa.addAll(resultsSiswa)
        notifyDataSetChanged()

        Log.d(TAG, "setData: $listSiswa")
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiswaViewHolder {
        val binding =
            ItemsSiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SiswaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SiswaViewHolder, position: Int) {
        holder.bind(listSiswa[position])
    }

    override fun getItemCount() = listSiswa.size

    inner class SiswaViewHolder(private val binding: ItemsSiswaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultsSiswa: ResultsSiswa) {
            with(binding) {
                tvNamaSiswa.text = resultsSiswa.nama

                Glide.with(itemView.context)
                    .load(resultsSiswa.fotoProfile)
                    .error(R.drawable.no_profile_images)
                    .into(imgSiswa)

                itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsSiswa) }
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsSiswa: ResultsSiswa)
    }
}