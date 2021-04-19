package com.kontakanprojects.apptkslb.view.siswa.result

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.siswa.ResponseNilaiSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseStateSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultViewModel : ViewModel() {

    private val TAG = ResultViewModel::class.simpleName

    private var _nilaiSiswa: MutableLiveData<ResponseNilaiSiswa?>? = null
    private var _stateSiswa: MutableLiveData<ResponseStateSiswa?>? = null

    fun nilaiSiswa(idSiswa: Int, idNilai: Int): LiveData<ResponseNilaiSiswa?> {
        _nilaiSiswa = MutableLiveData<ResponseNilaiSiswa?>()
        getNilaiSiswa(idSiswa, idNilai)
        return _nilaiSiswa as MutableLiveData<ResponseNilaiSiswa?>
    }

    fun resetLevelSiswa(
        idSiswa: Int,
        idChapterLevel: Int,
        idChapter: Int
    ): LiveData<ResponseStateSiswa?> {
        _stateSiswa = MutableLiveData<ResponseStateSiswa?>()
        resetSiswa(idSiswa, idChapterLevel, idChapter)
        return _stateSiswa as MutableLiveData<ResponseStateSiswa?>
    }

    private fun getNilaiSiswa(idSiswa: Int, idNilai: Int) {
        val client = ApiConfig.getApiService().getDetailNilaiSiswa(idSiswa, idNilai)
        client.enqueue(object : Callback<ResponseNilaiSiswa> {
            override fun onResponse(
                call: Call<ResponseNilaiSiswa>,
                response: Response<ResponseNilaiSiswa>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _nilaiSiswa?.postValue(result)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseNilaiSiswa = ResponseNilaiSiswa(message = message, status = status)
                    _nilaiSiswa?.postValue(responseNilaiSiswa)

                    Log.e(TAG, "onFailure: $responseNilaiSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseNilaiSiswa>, t: Throwable) {
                _nilaiSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun resetSiswa(idSiswa: Int, idChapterLevel: Int, idChapter: Int) {
        val client = ApiConfig.getApiService()
            .resetStateLevelSiswa(idSiswa, idChapterLevel, idChapter)
        client.enqueue(object : Callback<ResponseStateSiswa> {
            override fun onResponse(
                call: Call<ResponseStateSiswa>,
                response: Response<ResponseStateSiswa>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _stateSiswa?.postValue(result)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseStateSiswa = ResponseStateSiswa(message = message, status = status)
                    _stateSiswa?.postValue(responseStateSiswa)

                    Log.e(TAG, "onFailure: $responseStateSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseStateSiswa>, t: Throwable) {
                _stateSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}