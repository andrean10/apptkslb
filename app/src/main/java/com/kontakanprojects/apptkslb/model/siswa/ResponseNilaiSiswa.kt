package com.kontakanprojects.apptkslb.model.siswa

import com.google.gson.annotations.SerializedName

data class ResponseNilaiSiswa(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsNilaiSiswa>? = null,

    @field:SerializedName("status")
    val status: Int
)

data class ResultsNilaiSiswa(

    @field:SerializedName("id_nilai")
    val idNilai: Int? = null,

    @field:SerializedName("nilai")
    val nilai: Float? = null,

    @field:SerializedName("total_soal")
    val totalSoal: Int? = null,

    @field:SerializedName("total_salah")
    val totalSalah: Int? = null,

    @field:SerializedName("nama_chapter")
    val namaChapter: String? = null,

    @field:SerializedName("id_mapel")
    val idMapel: Int? = null,

    @field:SerializedName("total_benar")
    val totalBenar: Int? = null,

    @field:SerializedName("id_chapter")
    val idChapter: Int? = null,

    @field:SerializedName("id_siswa")
    val idSiswa: Int? = null
)
