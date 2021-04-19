package com.kontakanprojects.apptkslb.view.siswa.chapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.databinding.ItemsChapterBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultChapter
import com.kontakanprojects.apptkslb.model.siswa.ResultStateSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResultsNilaiSiswa

class ChapterSiswaAdapter : RecyclerView.Adapter<ChapterSiswaAdapter.ChapterSiswaViewHolder>() {

    private val listChapter = ArrayList<ResultChapter>()
    private var listChapterAndLevel: ArrayList<ResultStateSiswa>? = null
    private var listNilaiSiswa: ArrayList<ResultsNilaiSiswa>? = null
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(chapter: List<ResultChapter>?) {
        if (chapter == null) return
        listChapter.clear()
        listChapter.addAll(chapter)
        notifyDataSetChanged()
    }

    fun setChapterAndLevelSiswa(chapterAndLevelSiswa: List<ResultStateSiswa>?) {
        if (chapterAndLevelSiswa == null) return
        listChapterAndLevel = ArrayList()
        listChapterAndLevel!!.addAll(chapterAndLevelSiswa)
        notifyDataSetChanged()
    }

    fun setNilaiSiswa(resultsNilaiSiswa: List<ResultsNilaiSiswa>?) {
        if (resultsNilaiSiswa == null) return
        listNilaiSiswa = ArrayList()
        listNilaiSiswa!!.addAll(resultsNilaiSiswa)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterSiswaViewHolder {
        val binding =
            ItemsChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChapterSiswaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChapterSiswaViewHolder, position: Int) {
        holder.bind(listChapter[position], position)
    }

    override fun getItemCount() = listChapter.size

    inner class ChapterSiswaViewHolder(private val binding: ItemsChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultChapter: ResultChapter, position: Int) {
            with(binding) {
                tvChapter.text = resultChapter.namaChapter

                // logic lock and unlock chapter
                // jika terdapat nilai siswa pada chapter 1
                // maka chapter 2 terbuka
                if (listNilaiSiswa != null) { // nilai ada
                    if (listChapterAndLevel != null) { // user sudah mengerjakan beberapa chapter dan level
                        // cek jika user sudah menyelesaikan chapter atau belum berdasarkan chapter terbaru
                        if (position < listNilaiSiswa!!.last().idChapter!! + 1) {
                            chapterLock.visibility = View.GONE
                            imgBackgroundLock.visibility = View.GONE

                            itemView.setOnClickListener {
                                val conditionNilaiSiswa = if (position < listNilaiSiswa!!.size) {
                                    listNilaiSiswa!![position]
                                } else {
                                    null
                                }

                                val conditionListChapter =
                                    if (position < listChapterAndLevel!!.size) {
                                        listChapterAndLevel!![position]
                                    } else {
                                        null
                                    }

                                onItemClickCallBack!!.onItemClicked(
                                    resultChapter,
                                    conditionListChapter, conditionNilaiSiswa
                                )
                            }
                        }
                    }
                } else { // nilai belum ada
                    if (position == 0) { // set chapter 1 unlock
                        chapterLock.visibility = View.GONE
                        imgBackgroundLock.visibility = View.GONE

                        itemView.setOnClickListener {
                            onItemClickCallBack?.onItemClicked(
                                resultChapter,
                                listChapterAndLevel?.get(position)
                            )
                        }
                    }
                }
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(
            resultChapter: ResultChapter,
            listChapterAndLevel: ResultStateSiswa? = null,
            resultsNilaiSiswa: ResultsNilaiSiswa? = null
        )
    }
}