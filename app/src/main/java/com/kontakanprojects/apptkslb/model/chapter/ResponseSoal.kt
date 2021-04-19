package com.kontakanprojects.apptkslb.model.chapter

import com.google.gson.annotations.SerializedName

data class ResponseSoal(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: Results? = null,

    @field:SerializedName("status")
    val status: Int
)

data class Results(

    @field:SerializedName("jawabanBenar")
    val jawabanBenar: String? = null,

    @field:SerializedName("soal_suara")
    val soalSuara: String? = null,

    @field:SerializedName("level")
    val level: Int? = null,

    @field:SerializedName("id_soal")
    val idSoal: Int? = null,

    @field:SerializedName("soal_teks")
    val soalTeks: Any? = null,

    @field:SerializedName("jawabanD")
    val jawabanD: String? = null,

    @field:SerializedName("jawabanB")
    val jawabanB: String? = null,

    @field:SerializedName("video")
    val video: String? = null,

    @field:SerializedName("jawabanC")
    val jawabanC: String? = null,


    @field:SerializedName("jawabanA")
    val jawabanA: String? = null,

    @field:SerializedName("id_chapter")
    val idChapter: Int? = null
)
