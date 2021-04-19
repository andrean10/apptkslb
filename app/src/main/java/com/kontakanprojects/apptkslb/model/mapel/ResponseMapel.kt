package com.kontakanprojects.apptkslb.model.mapel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResponseMapel(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsMapel>? = null,

    @field:SerializedName("status")
    val status: Int
)

@Parcelize
data class ResultsMapel(

    @field:SerializedName("nama_mapel")
    val namaMapel: String? = null,

    @field:SerializedName("id_kelas")
    val idKelas: Int? = null,

    @field:SerializedName("id_mapel")
    val idMapel: Int? = null,

    @field:SerializedName("id_chapter")
    val idChapter: Int? = null,

    @field:SerializedName("nama_chapter")
    val namaChapter: Int? = null

) : Parcelable {
    override fun toString(): String {
        return namaMapel.toString()
    }
}
