package com.kontakanprojects.apptkslb.model.chapter

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ResponseIntroSoal(

    @field:SerializedName("result")
    val result: ResultIntroSoal? = null,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Int
)

@Parcelize
data class ResultIntroSoal(

    @field:SerializedName("id_intro")
    val idIntro: Int? = null,

    @field:SerializedName("video")
    val video: String? = null,

    @field:SerializedName("id_chapter")
    val idChapter: Int? = null,

    @field:SerializedName("nama_chapter")
    val namaChapter: String? = null
) : Parcelable
