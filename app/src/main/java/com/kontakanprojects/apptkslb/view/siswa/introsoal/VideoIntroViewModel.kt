package com.kontakanprojects.apptkslb.view.siswa.introsoal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.chapter.ResponseIntroSoal
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.view.siswa.soal.SoalViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoIntroViewModel : ViewModel() {

    private var _introSoal = MutableLiveData<ResponseIntroSoal?>()
    private val TAG = SoalViewModel::class.simpleName

    fun introSoal(idChapter: Int): LiveData<ResponseIntroSoal?> {
        getIntroSoal(idChapter)
        return _introSoal
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
                    _introSoal.postValue(responseIntroSoal)
                    Log.e(TAG, "onFailure: $responseIntroSoal")
                }
            }

            override fun onFailure(call: Call<ResponseIntroSoal>, t: Throwable) {
                _introSoal.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}