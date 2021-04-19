package com.kontakanprojects.apptkslb.view.siswa.chapter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.chapter.ResponseChapter
import com.kontakanprojects.apptkslb.model.siswa.ResponseNilaiSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseStateSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChapterSiswaViewModel : ViewModel() {

    private var _chapter: MutableLiveData<ResponseChapter?>? = null
    private var _stateChapterAndLevel: MutableLiveData<ResponseStateSiswa?>? = null
    private var _nilaiSiswa: MutableLiveData<ResponseNilaiSiswa?>? = null

    private val TAG = ChapterSiswaViewModel::class.simpleName

    fun chapters(idMapel: Int): LiveData<ResponseChapter?> { // ambil semua chapter
        _chapter = MutableLiveData<ResponseChapter?>()
        getChapters(idMapel)
        return _chapter as MutableLiveData<ResponseChapter?>
    }

    fun chapterAndLevelSiswa(idSiswa: Int): LiveData<ResponseStateSiswa?> { // ambil chapter siswa yang sudah dikerjakan
        _stateChapterAndLevel = MutableLiveData<ResponseStateSiswa?>()
        getChapterAndLevelSiswa(idSiswa)
        return _stateChapterAndLevel as MutableLiveData<ResponseStateSiswa?>
    }

    fun nilaiSiswa(idSiswa: Int): LiveData<ResponseNilaiSiswa?> { // ambil nilai siswa
        _nilaiSiswa = MutableLiveData<ResponseNilaiSiswa?>()
        getNilaiSiswa(idSiswa)
        return _nilaiSiswa as MutableLiveData<ResponseNilaiSiswa?>
    }

    private fun getNilaiSiswa(idSiswa: Int) {
        val client = ApiConfig.getApiService().getNilaiSiswa(idSiswa)
        client.enqueue(object : Callback<ResponseNilaiSiswa> {
            override fun onResponse(
                call: Call<ResponseNilaiSiswa>,
                response: Response<ResponseNilaiSiswa>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _nilaiSiswa?.postValue(result)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseNilaiSiswa = ResponseNilaiSiswa(message = message, status = status)
                    _nilaiSiswa?.postValue(responseNilaiSiswa)

                    Log.e(TAG, "onFailure: $responseNilaiSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseNilaiSiswa>, t: Throwable) {
                _nilaiSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getChapters(idMapel: Int) {
        val client = ApiConfig.getApiService().chapter(idMapel)
        client.enqueue(object : Callback<ResponseChapter> {
            override fun onResponse(
                call: Call<ResponseChapter>,
                response: Response<ResponseChapter>
            ) {
                if (response.isSuccessful) {
                    _chapter?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseChapter = ResponseChapter(message = message, status = status)
                    _chapter?.postValue(responseChapter)

                    Log.e(TAG, "onFailure: $responseChapter")
                }
            }

            override fun onFailure(call: Call<ResponseChapter>, t: Throwable) {
                _chapter?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getChapterAndLevelSiswa(idSiswa: Int) {
        val client = ApiConfig.getApiService().getChapterAndLevelSiswa(idSiswa)
        client.enqueue(object : Callback<ResponseStateSiswa> {
            override fun onResponse(
                call: Call<ResponseStateSiswa>,
                response: Response<ResponseStateSiswa>
            ) {
                if (response.isSuccessful) {
                    _stateChapterAndLevel?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseChapterSiswa =
                        ResponseStateSiswa(message = message, status = status)
                    _stateChapterAndLevel?.postValue(responseChapterSiswa)

                    Log.e(TAG, "onFailure: $responseChapterSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseStateSiswa>, t: Throwable) {
                _stateChapterAndLevel?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}