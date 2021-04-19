package com.kontakanprojects.apptkslb.model.kelas

import com.google.gson.annotations.SerializedName

data class ResponseKelas(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsKelas>? = null,

    @field:SerializedName("status")
    val status: Int
)

data class ResultsKelas(

    @field:SerializedName("nama_kelas")
    val namaKelas: String? = null,

    @field:SerializedName("id_kelas")
    val idKelas: Int? = null

) {
    override fun toString(): String {
        return namaKelas.toString()
    }
}
