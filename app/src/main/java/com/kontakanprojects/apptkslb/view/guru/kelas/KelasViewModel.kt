package com.kontakanprojects.apptkslb.view.guru.kelas

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.kelas.ResponseKelas
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KelasViewModel : ViewModel() {

    private var _kelas: MutableLiveData<ResponseKelas?>? = null
    private val _isLoading = MutableLiveData<Boolean>()

    private val TAG = KelasViewModel::class.simpleName

    fun isLoading(): LiveData<Boolean> = _isLoading

    fun getKelas(): LiveData<ResponseKelas?> {
        if (_kelas == null) {
            _kelas = MutableLiveData<ResponseKelas?>()
            kelas()
        }
        return _kelas as MutableLiveData<ResponseKelas?>
    }

    private fun kelas() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().kelas()
        client.enqueue(object : Callback<ResponseKelas> {
            override fun onResponse(
                call: Call<ResponseKelas>,
                response: Response<ResponseKelas>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _kelas?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseKelas = ResponseKelas(message = message, status = status)
                    _kelas?.postValue(responseKelas)

                    Log.e(TAG, "onFailure: $responseKelas")
                }
            }

            override fun onFailure(call: Call<ResponseKelas>, t: Throwable) {
                _isLoading.value = false
                _kelas?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}