package com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadintrosoal

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.chapter.ResponseIntroSoal
import com.kontakanprojects.apptkslb.network.ApiConfig
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel : ViewModel(), LifecycleObserver {

    private var _introSoal = MutableLiveData<ResponseIntroSoal>()
    private var _crudIntroSoal: MutableLiveData<ResponseIntroSoal>? = null

    //    private var _levelByChapter: MutableLiveData<ResponseLevel>? = null
    private val TAG = UploadViewModel::class.simpleName

    fun introSoal(idChapter: Int): LiveData<ResponseIntroSoal> {
        _introSoal = MutableLiveData<ResponseIntroSoal>()
        getIntroSoal(idChapter)
        return _introSoal
    }

    fun uploadIntroSoal(idChapter: Int, video: MultipartBody.Part): LiveData<ResponseIntroSoal> {
        _crudIntroSoal = MutableLiveData<ResponseIntroSoal>()
        addIntroSoal(idChapter, video)
        return _crudIntroSoal as MutableLiveData<ResponseIntroSoal>
    }

    fun editIntroSoal(
        idIntro: Int,
        idChapter: Int,
        video: MultipartBody.Part
    ): LiveData<ResponseIntroSoal> {
        _crudIntroSoal = MutableLiveData<ResponseIntroSoal>()
        edtIntroSoal(idIntro, idChapter, video)
        return _crudIntroSoal as MutableLiveData<ResponseIntroSoal>
    }

    private fun edtIntroSoal(idIntro: Int, idChapter: Int, video: MultipartBody.Part) {
        val client = ApiConfig.getApiService().editIntroSoalByChapter(idChapter, idIntro, video)
        client.enqueue(object : Callback<ResponseIntroSoal> {
            override fun onResponse(
                call: Call<ResponseIntroSoal>,
                response: Response<ResponseIntroSoal>
            ) {
                if (response.isSuccessful) {
                    _crudIntroSoal?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseIntroSoal = ResponseIntroSoal(message = message, status = status)
                    _crudIntroSoal?.postValue(responseIntroSoal)

                    Log.e(TAG, "onFailure: $responseIntroSoal")
                }
            }

            override fun onFailure(call: Call<ResponseIntroSoal>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getIntroSoal(idChapter: Int) {
        val client = ApiConfig.getApiService().getIntroSoalByChapter(idChapter)
        client.enqueue(object : Callback<ResponseIntroSoal> {
            override fun onResponse(
                call: Call<ResponseIntroSoal>,
                response: Response<ResponseIntroSoal>
            ) {
                if (response.isSuccessful) {
                    _introSoal.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseIntroSoal = ResponseIntroSoal(message = message, status = status)
                    _introSoal?.postValue(responseIntroSoal)

                    Log.e(TAG, "onFailure: $responseIntroSoal")
                }
            }

            override fun onFailure(call: Call<ResponseIntroSoal>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun addIntroSoal(idChapter: Int, video: MultipartBody.Part) {
        val client = ApiConfig.getApiService().addIntroSoalByChapter(idChapter, video)
        client.enqueue(object : Callback<ResponseIntroSoal> {
            override fun onResponse(
                call: Call<ResponseIntroSoal>,
                response: Response<ResponseIntroSoal>
            ) {
                if (response.isSuccessful) {
                    _crudIntroSoal?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseIntroSoal = ResponseIntroSoal(message = message, status = status)
                    _crudIntroSoal?.postValue(responseIntroSoal)

                    Log.e(TAG, "onFailure: $responseIntroSoal")
                }
            }

            override fun onFailure(call: Call<ResponseIntroSoal>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}