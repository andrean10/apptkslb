package com.kontakanprojects.apptkslb.view.guru.kelas.mapel.chapter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.chapter.ResponseChapter
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.view.guru.chapter.ChapterFragment
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChapterKelasViewModel : ViewModel() {

    private val _chapter = MutableLiveData<ResponseChapter?>()
    private var _isLoading = MutableLiveData<Boolean>()

    private val TAG = ChapterFragment::class.simpleName

    fun isLoading(): LiveData<Boolean> = _isLoading

    val chapters: LiveData<ResponseChapter?> = _chapter

    fun addChapters(namaChapter: String, idMapel: Int): LiveData<ResponseChapter?> {
        tambahChapter(namaChapter, idMapel)
        return _chapter
    }

    fun editChapters(idChapter: Int, namaChapter: String, idMapel: Int): LiveData<ResponseChapter?> {
        ubahChapter(idChapter, namaChapter, idMapel)
        return _chapter
    }

    fun deleteChapters(idChapter: Int, idMapel: Int): LiveData<ResponseChapter?> {
        hapusChapter(idChapter, idMapel)
        return _chapter
    }

    fun getChapters(idMapel: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().chapter(idMapel)
        client.enqueue(object : Callback<ResponseChapter> {
            override fun onResponse(
                call: Call<ResponseChapter>,
                response: Response<ResponseChapter>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _chapter.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseChapter = ResponseChapter(message = message, status = status)
                    _chapter.postValue(responseChapter)

                    Log.e(TAG, "onFailure: $responseChapter")
                }
            }

            override fun onFailure(call: Call<ResponseChapter>, t: Throwable) {
                _isLoading.value = false
                _chapter.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun tambahChapter(namaChapter: String, idMapel: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().addChapter(idMapel, namaChapter)
        client.enqueue(object : Callback<ResponseChapter> {
            override fun onResponse(
                call: Call<ResponseChapter>,
                response: Response<ResponseChapter>
            ) {
                if (response.isSuccessful) {
                    _chapter.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseChapter = ResponseChapter(message = message, status = status)
                    _chapter.postValue(responseChapter)

                    Log.e(TAG, "onFailure: $responseChapter")
                }
            }

            override fun onFailure(call: Call<ResponseChapter>, t: Throwable) {
                _chapter.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun ubahChapter(idChapter: Int, namaChapter: String, idMapel: Int) {
        val client = ApiConfig.getApiService().editChapter(idMapel, idChapter, namaChapter)
        client.enqueue(object : Callback<ResponseChapter> {
            override fun onResponse(
                call: Call<ResponseChapter>,
                response: Response<ResponseChapter>
            ) {
                if (response.isSuccessful) {
                    _chapter.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseChapter = ResponseChapter(message = message, status = status)
                    _chapter.postValue(responseChapter)

                    Log.e(TAG, "onFailure: $responseChapter")
                }
            }

            override fun onFailure(call: Call<ResponseChapter>, t: Throwable) {
                _chapter.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun hapusChapter(idChapter: Int, idMapel: Int) {
        val client = ApiConfig.getApiService().deleteChapter(idMapel, idChapter)
        client.enqueue(object : Callback<ResponseChapter> {
            override fun onResponse(
                call: Call<ResponseChapter>,
                response: Response<ResponseChapter>
            ) {
                if (response.isSuccessful) {
                    _chapter.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseChapter = ResponseChapter(message = message, status = status)
                    _chapter.postValue(responseChapter)

                    Log.e(TAG, "onFailure: $responseChapter")
                }
            }

            override fun onFailure(call: Call<ResponseChapter>, t: Throwable) {
                _chapter.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}