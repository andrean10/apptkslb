package com.kontakanprojects.apptkslb.view.guru.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.guru.ResponseDetailGuru
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeGuruViewModel : ViewModel() {

    private var _guru: MutableLiveData<ResponseDetailGuru?>? = null

    private val TAG = HomeGuruViewModel::class.simpleName

    fun detailGuru(idGuru: Int): LiveData<ResponseDetailGuru?> {
        _guru = MutableLiveData<ResponseDetailGuru?>()
        getDetailGuru(idGuru)
        return _guru as MutableLiveData<ResponseDetailGuru?>
    }

    private fun getDetailGuru(idGuru: Int) {
        val client = ApiConfig.getApiService().detailGuru(idGuru)
        client.enqueue(object : Callback<ResponseDetailGuru> {
            override fun onResponse(
                call: Call<ResponseDetailGuru>,
                response: Response<ResponseDetailGuru>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _guru?.postValue(result)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responsDetailGuru =
                        ResponseDetailGuru(message = message, status = status)
                    _guru?.postValue(responsDetailGuru)

                    Log.e(TAG, "onFailure: $responsDetailGuru")
                }
            }

            override fun onFailure(call: Call<ResponseDetailGuru>, t: Throwable) {
                _guru?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}