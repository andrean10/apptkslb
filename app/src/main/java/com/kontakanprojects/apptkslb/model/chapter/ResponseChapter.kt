package com.kontakanprojects.apptkslb.model.chapter

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResponseChapter(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultChapter>? = null,

    @field:SerializedName("status")
    val status: Int
)

@Parcelize
data class ResultChapter(

    @field:SerializedName("nama_chapter")
    val namaChapter: String,

    @field:SerializedName("id_chapter")
    val idChapter: Int,

    @field:SerializedName("id_mapel")
    val idMapel: Int,

    @field:SerializedName("nama_mapel")
    val namaMapel: String,

    @field:SerializedName("id_kelas")
    val idKelas: Int
) : Parcelable
