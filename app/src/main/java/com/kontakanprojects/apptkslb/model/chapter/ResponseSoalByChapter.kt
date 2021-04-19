package com.kontakanprojects.apptkslb.model.chapter

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ResponseSoalByChapter(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsSoalByChapter>? = null,

    @field:SerializedName("status")
    val status: Int
)

@Parcelize
data class ResultsSoalByChapter(

    @field:SerializedName("id_soal")
    val idSoal: Int,

    @field:SerializedName("id_level")
    val idLevel: Int,

    @field:SerializedName("video")
    val video: String? = null,

    @field:SerializedName("gambar")
    val gambar: String? = null,

    @field:SerializedName("kunci_jawaban")
    val kunciJawaban: Int,

    @field:SerializedName("soal_suara")
    val soalSuara: String? = null,

    @field:SerializedName("id_chapter_level")
    val idChapterLevel: Int,

    @field:SerializedName("opsiA")
    val opsiA: String,

    @field:SerializedName("opsiB")
    val opsiB: String,

    @field:SerializedName("opsiC")
    val opsiC: String,

    @field:SerializedName("opsiD")
    val opsiD: String,

    @field:SerializedName("id_chapter")
    val idChapter: Int,

    @field:SerializedName("level")
    val level: String
) : Parcelable
