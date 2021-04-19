package com.kontakanprojects.apptkslb.model

import com.google.gson.annotations.SerializedName

data class ResponseAuth(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: Results? = null,

    @field:SerializedName("status")
    val status: Int
)

data class Results(

    @field:SerializedName("id_role")
    val idRole: Int,

    @field:SerializedName("id_user")
    val id: Int,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("foto_profile")
    val fotoProfile: String
)
