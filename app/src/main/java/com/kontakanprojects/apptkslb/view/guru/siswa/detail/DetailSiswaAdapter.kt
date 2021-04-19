package com.kontakanprojects.apptkslb.view.guru.siswa.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.databinding.ItemsNilaiSiswaBinding
import com.kontakanprojects.apptkslb.model.siswa.ResultsNilaiSiswa
import kotlin.math.roundToInt

class DetailSiswaAdapter : RecyclerView.Adapter<DetailSiswaAdapter.DetailSiswaViewHolder>() {

    private val listNilaiSiswa = ArrayList<ResultsNilaiSiswa>()
    private val TAG = DetailSiswaAdapter::class.simpleName

    fun setNilaiSiswa(resultsNilaiSiswa: List<ResultsNilaiSiswa>?) {
        if (resultsNilaiSiswa == null) return
        listNilaiSiswa.clear()
        listNilaiSiswa.addAll(resultsNilaiSiswa)
        notifyDataSetChanged()

        Log.d(TAG, "setNilaiSiswa: $listNilaiSiswa")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailSiswaViewHolder {
        val binding =
            ItemsNilaiSiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailSiswaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailSiswaViewHolder, position: Int) {
        holder.bind(listNilaiSiswa[position])
    }

    override fun getItemCount() = listNilaiSiswa.size

    inner class DetailSiswaViewHolder(private val binding: ItemsNilaiSiswaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultNilaiSiswa: ResultsNilaiSiswa) {
            with(binding) {
                tvChapter.text = resultNilaiSiswa.namaChapter
                pbNilaiSiswa.progress = resultNilaiSiswa.nilai?.roundToInt()!!
                tvNilai.text = resultNilaiSiswa.nilai.toString()
            }
        }
    }
}