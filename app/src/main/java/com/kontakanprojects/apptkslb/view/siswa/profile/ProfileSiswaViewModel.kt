package com.kontakanprojects.apptkslb.view.siswa.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.siswa.ResponseDetailSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileSiswaViewModel : ViewModel() {

    private var _siswa: MutableLiveData<ResponseDetailSiswa?>? = null

    private val TAG = ProfileSiswaFragment::class.simpleName

    fun detailSiswa(idSiswa: Int): LiveData<ResponseDetailSiswa?> {
        _siswa = MutableLiveData<ResponseDetailSiswa?>()
        getDetailSiswa(idSiswa)
        return _siswa as MutableLiveData<ResponseDetailSiswa?>
    }

    fun changeFotoProfile(idSiswa: Int, foto: MultipartBody.Part): LiveData<ResponseDetailSiswa?> {
        _siswa = MutableLiveData<ResponseDetailSiswa?>()
        editFotoSiswa(idSiswa, foto)
        return _siswa as MutableLiveData<ResponseDetailSiswa?>
    }

    fun changeDetailProfile(
        idSiswa: Int,
        newData: HashMap<String, String>?
    ): LiveData<ResponseDetailSiswa?> {
        _siswa = MutableLiveData<ResponseDetailSiswa?>()
        editDetailProfile(idSiswa, newData)
        return _siswa as MutableLiveData<ResponseDetailSiswa?>
    }

    private fun getDetailSiswa(idSiswa: Int) {
        val client = ApiConfig.getApiService().detailSiswa(idSiswa)
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

    private fun editFotoSiswa(idSiswa: Int, newFoto: MultipartBody.Part) {
        val client = ApiConfig.getApiService().changeFoto(idSiswa, newFoto)
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

    private fun editDetailProfile(idSiswa: Int, newData: HashMap<String, String>?) {
        val client = ApiConfig.getApiService().editDetailSiswa(idSiswa, newData)
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
}