package com.kontakanprojects.apptkslb.model.siswa

import com.google.gson.annotations.SerializedName

data class ResponseRataRataNilaiSiswa(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("results")
    val results: Double? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
