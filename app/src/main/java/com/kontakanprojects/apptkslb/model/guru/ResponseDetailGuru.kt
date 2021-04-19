package com.kontakanprojects.apptkslb.model.guru

import com.google.gson.annotations.SerializedName

data class ResponseDetailGuru(

    @field:SerializedName("result")
    val result: ResultDetailGuru? = null,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Int
)

data class ResultDetailGuru(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("nama")
    val nama: String? = null,

    @field:SerializedName("nama_kelas")
    val namaKelas: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("nama_mapel")
    val namaMapel: String? = null,

    @field:SerializedName("id_role")
    val idRole: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id_kelas")
    val idKelas: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("id_mapel")
    val idMapel: Int? = null,

    @field:SerializedName("foto_profile")
    var fotoProfile: String? = null,

    @field:SerializedName("username")
    val username: String? = null
)
