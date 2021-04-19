package com.kontakanprojects.apptkslb.model.level

import com.google.gson.annotations.SerializedName

data class ResponseLevel(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("results")
    val results: List<ResultsItem?>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class ResultsItem(

    @field:SerializedName("level")
    val level: String? = null,

    @field:SerializedName("id_level")
    val idLevel: Int? = null
)
