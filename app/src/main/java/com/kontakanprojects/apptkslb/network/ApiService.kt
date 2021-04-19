package com.kontakanprojects.apptkslb.network

import com.kontakanprojects.apptkslb.model.ResponseAuth
import com.kontakanprojects.apptkslb.model.chapter.*
import com.kontakanprojects.apptkslb.model.guru.ResponseDetailGuru
import com.kontakanprojects.apptkslb.model.guru.ResponseSiswa
import com.kontakanprojects.apptkslb.model.kelas.ResponseKelas
import com.kontakanprojects.apptkslb.model.mapel.ResponseMapel
import com.kontakanprojects.apptkslb.model.siswa.ResponseDetailSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseNilaiSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseRataRataNilaiSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseStateSiswa
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    // <---- ROUTE AUTH
    @FormUrlEncoded
    @POST("auth/login")
    fun login(@FieldMap params: HashMap<String, Any>): Call<ResponseAuth>

    @FormUrlEncoded
    @POST("auth/register")
    fun register(@FieldMap params: HashMap<String, Any>): Call<ResponseAuth>

    // <-- ROUTE ROLE SISWA --->
    @GET("siswa/{id}")
    fun detailSiswa(@Path("id") id: Int): Call<ResponseDetailSiswa>

    @FormUrlEncoded
    @PUT("siswa/{id}")
    fun editDetailSiswa(
        @Path("id") id: Int,
        @FieldMap params: HashMap<String, String>?
    ): Call<ResponseDetailSiswa>

    @Multipart
    @PUT("siswa/{id}/picture")
    fun changeFoto(
        @Path("id") idSiswa: Int,
        @Part foto: MultipartBody.Part
    ): Call<ResponseDetailSiswa>

    @GET("chapter/{id}/max-level")
    fun getMaxLevel(@Path("id") idChapter: Int): Call<ResponseMaxLevel>

    @GET("mapel/{id}/chapter")
    fun chapter(@Path("id") idMapel: Int): Call<ResponseChapter>

    @FormUrlEncoded
    @POST("mapel/{id}/chapter")
    fun addChapter(
        @Path("id") idMapel: Int,
        @Field("nama_chapter") namaChapter: String
    ): Call<ResponseChapter>

    @FormUrlEncoded
    @PUT("mapel/{id}/chapter/{idChapter}")
    fun editChapter(
        @Path("id") idMapel: Int,
        @Path("idChapter") idChapter: Int,
        @Field("nama_chapter") namaChapter: String
    ): Call<ResponseChapter>

    @DELETE("mapel/{id}/chapter/{idChapter}")
    fun deleteChapter(
        @Path("id") idMapel: Int,
        @Path("idChapter") idChapter: Int
    ): Call<ResponseChapter>

    // get semua soal
    @GET("chapter/{id}/soal")
    fun soal(@Path("id") id: Int): Call<ResponseSoalByChapter>

    // tambah soal
    @Multipart
    @POST("chapter/{id}/soal")
    fun addSoal(
        @Path("id") id: Int,
        @Part video: MultipartBody.Part?,
        @Part image: MultipartBody.Part?,
        @Part soalSuara: MultipartBody.Part,
        @PartMap params: HashMap<String, RequestBody>
    ): Call<ResponseSoalByChapter>

    @Multipart
    @PUT("chapter/{id}/soal/{idSoal}")
    fun editSoal(
        @Path("id") idChapter: Int,
        @Path("idSoal") idSoal: Int,
        @Part video: MultipartBody.Part,
        @Part soalSuara: MultipartBody.Part,
        @PartMap params: HashMap<String, RequestBody>
    ): Call<ResponseSoalByChapter>

    @DELETE("chapter/{id}/soal/{idSoal}")
    fun deleteSoal(
        @Path("id") idChapter: Int,
        @Path("idSoal") idSoal: Int
    ): Call<ResponseSoalByChapter>

    // get chapter and level siswa by mata pelajaran
    @GET("mapel/{id}/siswa/{idSiswa}/chapterlevel")
    fun getChapterAndLevelSiswaByMapel(
        @Path("id") idMapel: Int,
        @Path("idSiswa") idSiswa: Int
    ): Call<ResponseStateSiswa>

    // get chapter and level siswa
    @GET("siswa/{id}/chapterlevel")
    fun getChapterAndLevelSiswa(@Path("id") idSiswa: Int): Call<ResponseStateSiswa>

    // get detail chapter and level siswa
    @GET("siswa/{id}/chapterlevel/{idChapterLevel}")
    fun getDetailStateLevelSiswa(
        @Path("id") idSiswa: Int,
        @Path("idChapterLevel") idChapterLevel: Int
    ): Call<ResponseStateSiswa>

    @FormUrlEncoded
    @POST("siswa/{id}/chapterlevel")
    fun addStateLevelSiswa(
        @Path("id") idSiswa: Int,
        @FieldMap params: HashMap<String, Int>
    ): Call<ResponseStateSiswa>

    @FormUrlEncoded
    @PUT("siswa/{id}/chapterlevel/{idChapterLevel}")
    fun updateStateLevelSiswa(
        @Path("id") idSiswa: Int,
        @Path("idChapterLevel") idChapterLevel: Int,
        @FieldMap params: HashMap<String, Int>
    ): Call<ResponseStateSiswa>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "siswa/{id}/chapterlevel/{idChapterLevel}", hasBody = true)
    fun resetStateLevelSiswa(
        @Path("id") idSiswa: Int,
        @Path("idChapterLevel") idChapterLevel: Int,
        @Field("idChapter") idChapter: Int
    ): Call<ResponseStateSiswa>

    // get nilai
    @GET("siswa/{id}/nilai")
    fun getNilaiSiswa(@Path("id") idSiswa: Int): Call<ResponseNilaiSiswa>

    // get nilai siswa
    @GET("mapel/{id}/siswa/{idSiswa}/nilai")
    fun getNilaiSiswaByMapel(
        @Path("id") idMapel: Int,
        @Path("idSiswa") idSiswa: Int
    ): Call<ResponseNilaiSiswa>

    @GET("siswa/{id}/nilai/{idNilai}")
    fun getDetailNilaiSiswa(
        @Path("id") idSiswa: Int,
        @Path("idNilai") idNilai: Int
    ): Call<ResponseNilaiSiswa>

    // menginput nilai berdasarkan chapter
    @FormUrlEncoded
    @POST("siswa/{id}/nilai")
    fun storeNilai(
        @Path("id") idSiswa: Int,
        @Field("idChapter") idChapter: Int
    ): Call<ResponseNilaiSiswa>

    @FormUrlEncoded
    @PUT("siswa/{id}/nilai/{idNilai}")
    fun updateNilai(
        @Path("id") idSiswa: Int,
        @Path("idNilai") idNilai: Int,
        @Field("idChapter") idChapter: Int,
        @Field("idChapterLevel") idChapterLevel: Int
    ): Call<ResponseNilaiSiswa>

    @GET("mapel/{id}/siswa/{idSiswa}/nilai_rata_rata")
    fun nilaiRataRata(
        @Path("id") idMapel: Int,
        @Path("idSiswa") idSiswa: Int
    ): Call<ResponseRataRataNilaiSiswa>

    // <----- ROUTE ROLE GURU DAN SISWA
    // Intro Soal By Chapter
    @GET("chapter/{id}/introsoal")
    fun getIntroSoalByChapter(@Path("id") id: Int): Call<ResponseIntroSoal>

    @Multipart
    @POST("chapter/{id}/introsoal")
    fun addIntroSoalByChapter(
        @Path("id") id: Int,
        @Part video: MultipartBody.Part,
    ): Call<ResponseIntroSoal>

    @Multipart
    @PUT("chapter/{id}/introsoal/{idIntro}")
    fun editIntroSoalByChapter(
        @Path("id") id: Int,
        @Path("idIntro") idIntro: Int,
        @Part video: MultipartBody.Part,
    ): Call<ResponseIntroSoal>

    @GET("chapter/{id}/jawaban")
    fun getJawabanChapterLevelSatu(@Path("id") idChapter: Int): Call<ResponseSoal>

    // <---- ROUTE GURU ---->
    @GET("guru/{id}")
    fun detailGuru(@Path("id") idGuru: Int): Call<ResponseDetailGuru>

    @GET("siswa")
    fun getSiswa(): Call<ResponseSiswa>

    // lihat siswa berdasarkan mata pelajaran dari guru pengampu
    @GET("kelas/{id}/mapel/{idMapel}/siswa")
    fun getMySiswa(
        @Path("id") idKelas: Int,
        @Path("idMapel") idMapel: Int
    ): Call<ResponseSiswa>

    @DELETE("siswa/{id}")
    fun deleteSiswa(@Path("id") idSiswa: Int): Call<ResponseDetailSiswa>

    @FormUrlEncoded
    @PATCH("guru/{id}")
    fun editDetailGuru(
        @Path("id") id: Int,
        @FieldMap params: HashMap<String, String>
    ): Call<ResponseDetailGuru>

    @Multipart
    @PUT("guru/{id}/picture")
    fun changePictureGuru(
        @Path("id") id: Int,
        @Part foto: MultipartBody.Part
    ): Call<ResponseDetailGuru>

    // tampilkan kelas
    @GET("kelas")
    fun kelas(): Call<ResponseKelas>

    // tampilkan mata-pelajaran
    @GET("mapel")
    fun mapel(): Call<ResponseMapel>

    @GET("kelas/{id}/mapel")
    fun mapelByKelas(@Path("id") idKelas: Int): Call<ResponseMapel>

    @FormUrlEncoded
    @POST("kelas/{id}/mapel")
    fun addMapelByKelas(
        @Path("id") idKelas: Int,
        @Field("nama_mapel") namaMapel: String
    ): Call<ResponseMapel>

    @FormUrlEncoded
    @PUT("kelas/{id}/mapel/{idMapel}")
    fun editMapelByKelas(
        @Path("id") idKelas: Int,
        @Path("idMapel") idMapel: Int,
        @Field("nama_mapel") namaMapel: String
    ): Call<ResponseMapel>

    @DELETE("kelas/{id}/mapel/{idMapel}")
    fun deleteMapelByKelas(
        @Path("id") idKelas: Int,
        @Path("idMapel") idMapel: Int
    ): Call<ResponseMapel>

    @FormUrlEncoded
    @POST("siswa/{id}/mapel")
    fun addRiwayatMapel(
        @Path("id") idSiswa: Int,
        @Field("id_mapel") idMapel: Int
    ): Call<ResponseMapel>
}