package com.kontakanprojects.apptkslb.model.guru

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResponseSiswa(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsSiswa>? = null,

    @field:SerializedName("status")
    val status: Int
)

@Parcelize
data class ResultsSiswa(

    @field:SerializedName("nama")
    val nama: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("id_role")
    val idRole: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("foto_profile")
    val fotoProfile: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("id_kelas")
    val idKelas: Int? = null,

    @field:SerializedName("nama_kelas")
    val namaKelas: String? = null
) : Parcelable
