package com.kontakanprojects.apptkslb.view.guru.uploadmateri.soal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.chapter.ResponseSoalByChapter
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SoalLevelViewModel : ViewModel() {

    private var _soalLevel = MutableLiveData<ResponseSoalByChapter>()
    private val TAG = SoalLevelViewModel::class.simpleName

    fun getSoalLevel(idChapter: Int): LiveData<ResponseSoalByChapter> {
        soalLevel(idChapter)
        return _soalLevel
    }

    private fun soalLevel(idChapter: Int) {
        val client = ApiConfig.getApiService().soal(idChapter)
        client.enqueue(object : Callback<ResponseSoalByChapter> {
            override fun onResponse(
                call: Call<ResponseSoalByChapter>,
                response: Response<ResponseSoalByChapter>
            ) {
                if (response.isSuccessful) {
                    _soalLevel.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseSoalByChapter =
                        ResponseSoalByChapter(message = message, status = status)
                    _soalLevel.postValue(responseSoalByChapter)

                    Log.e(TAG, "onFailure: $responseSoalByChapter")
                }
            }

            override fun onFailure(call: Call<ResponseSoalByChapter>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}