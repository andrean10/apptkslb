package com.kontakanprojects.apptkslb.view.guru.kelas.mapel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.mapel.ResponseMapel
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapelViewModel : ViewModel() {

    private val _mapel = MutableLiveData<ResponseMapel?>()
    private val _isLoading = MutableLiveData<Boolean>()

    private val TAG = MapelViewModel::class.simpleName

    val mapel: LiveData<ResponseMapel?> = _mapel

    fun isLoading(): LiveData<Boolean> = _isLoading

    fun getMapels(idKelas: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().mapelByKelas(idKelas)
        client.enqueue(object : Callback<ResponseMapel> {
            override fun onResponse(
                call: Call<ResponseMapel>,
                response: Response<ResponseMapel>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _mapel.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseMapel = ResponseMapel(message = message, status = status)
                    _mapel.postValue(responseMapel)

                    Log.e(TAG, "onFailure: $responseMapel")
                }
            }

            override fun onFailure(call: Call<ResponseMapel>, t: Throwable) {
                _isLoading.value = false
                _mapel.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}