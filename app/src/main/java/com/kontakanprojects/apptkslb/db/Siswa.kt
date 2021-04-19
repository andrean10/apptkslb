package com.kontakanprojects.apptkslb.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Siswa(
    val id: Int,
    val nama: String? = null,
    val kelas: String? = null,
    val fotoProfile: String? = null,
    val idMapel: Int? = null
) : Parcelable