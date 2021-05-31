package com.kontakanprojects.apptkslb.view.guru.siswa

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.guru.ResponseSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SiswaViewModel : ViewModel() {

    private var _siswa: MutableLiveData<ResponseSiswa>? = null
    private val TAG = SiswaViewModel::class.simpleName

    fun getSiswa(idKelas: Int, idMapel: Int): LiveData<ResponseSiswa> {
        _siswa = MutableLiveData<ResponseSiswa>()
        siswa(idKelas, idMapel)
        return _siswa as MutableLiveData<ResponseSiswa>
    }

    private fun siswa(idKelas: Int, idMapel: Int) {
        val client = ApiConfig.getApiService().getSiswa(idKelas, idMapel)
        client.enqueue(object : Callback<ResponseSiswa> {
            override fun onResponse(call: Call<ResponseSiswa>, response: Response<ResponseSiswa>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _siswa?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseSiswa = ResponseSiswa(message = message, status = status)
                    _siswa?.postValue(responseSiswa)

                    Log.e(TAG, "onFailure: $responseSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseSiswa>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}