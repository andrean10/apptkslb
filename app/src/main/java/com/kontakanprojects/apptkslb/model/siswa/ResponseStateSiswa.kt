package com.kontakanprojects.apptkslb.model.siswa

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ResponseStateSiswa(

    @field:SerializedName("results")
    val result: List<ResultStateSiswa>? = null,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Int
)

@Parcelize
data class ResultStateSiswa(

    @field:SerializedName("level")
    val level: String? = null,

    @field:SerializedName("id_level")
    val idLevel: Int? = null,

    @field:SerializedName("nama_chapter")
    val namaChapter: String? = null,

    @field:SerializedName("id_chapter_level_siswa")
    val idChapterLevelSiswa: Int? = null,

    @field:SerializedName("id_siswa")
    val idSiswa: Int? = null,

    @field:SerializedName("id_chapter")
    val idChapter: Int? = null,

    @field:SerializedName("is_reset")
    val isReset: String? = null

) : Parcelable
