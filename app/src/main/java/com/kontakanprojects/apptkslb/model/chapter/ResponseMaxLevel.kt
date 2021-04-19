package com.kontakanprojects.apptkslb.model.chapter

import com.google.gson.annotations.SerializedName

data class ResponseMaxLevel(

    @field:SerializedName("result")
    val result: Result? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class Result(

    @field:SerializedName("level")
    val level: String? = null,

    @field:SerializedName("id_level")
    val idLevel: Int? = null,

    @field:SerializedName("nama_chapter")
    val namaChapter: String? = null,

    @field:SerializedName("id_chapter_level")
    val idChapterLevel: Int? = null,

    @field:SerializedName("id_chapter")
    val idChapter: Int? = null
)
