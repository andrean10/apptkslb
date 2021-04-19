package com.kontakanprojects.apptkslb.view.siswa.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.mapel.ResponseMapel
import com.kontakanprojects.apptkslb.model.siswa.ResponseDetailSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeSiswaViewModel : ViewModel() {

    private var _siswa: MutableLiveData<ResponseDetailSiswa?>? = null
    private var _mapel: MutableLiveData<ResponseMapel>? = null

    private val TAG = HomeSiswaViewModel::class.simpleName

    fun detailSiswa(id: Int): LiveData<ResponseDetailSiswa?> {
        _siswa = MutableLiveData<ResponseDetailSiswa?>()
        getDetailSiswa(id)
        return _siswa as MutableLiveData<ResponseDetailSiswa?>
    }

    fun mapel(idKelas: Int): LiveData<ResponseMapel> {
        _mapel = MutableLiveData<ResponseMapel>()
        getMapel(idKelas)
        return _mapel as MutableLiveData<ResponseMapel>
    }

    private fun getDetailSiswa(id: Int) {
        val client = ApiConfig.getApiService().detailSiswa(id)
        client.enqueue(object : Callback<ResponseDetailSiswa> {
            override fun onResponse(
                call: Call<ResponseDetailSiswa>,
                response: Response<ResponseDetailSiswa>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _siswa?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseDetailSiswa =
                        ResponseDetailSiswa(message = message, status = status)
                    _siswa?.postValue(responseDetailSiswa)

                    Log.e(TAG, "onFailure: $responseDetailSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseDetailSiswa>, t: Throwable) {
                _siswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getMapel(idKelas: Int) {
        val client = ApiConfig.getApiService().mapelByKelas(idKelas)
        client.enqueue(object : Callback<ResponseMapel> {
            override fun onResponse(
                call: Call<ResponseMapel>,
                response: Response<ResponseMapel>
            ) {
                if (response.isSuccessful) {
                    _mapel?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseMapel = ResponseMapel(message = message, status = status)
                    _mapel?.postValue(responseMapel)

                    Log.e(TAG, "onFailure: $responseMapel")
                }
            }

            override fun onFailure(call: Call<ResponseMapel>, t: Throwable) {
                _mapel?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}