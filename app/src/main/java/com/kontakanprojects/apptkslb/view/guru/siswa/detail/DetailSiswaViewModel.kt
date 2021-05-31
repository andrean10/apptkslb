package com.kontakanprojects.apptkslb.view.guru.siswa.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.chapter.ResponseMaxLevel
import com.kontakanprojects.apptkslb.model.siswa.ResponseDetailSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseNilaiSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseRataRataNilaiSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseStateSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailSiswaViewModel : ViewModel() {

    private var _siswa: MutableLiveData<ResponseDetailSiswa?>? = null
    private var _nilaiSiswa: MutableLiveData<ResponseNilaiSiswa?>? = null
    private var _maxLevel: MutableLiveData<ResponseMaxLevel?>? = null
    private var _nilaiRataRata: MutableLiveData<ResponseRataRataNilaiSiswa?>? = null
    private var _stateLevelSiswa: MutableLiveData<ResponseStateSiswa?>? = null

    private val TAG = DetailSiswaViewModel::class.simpleName

    fun detailSiswa(idSiswa: Int): LiveData<ResponseDetailSiswa?> {
        _siswa = MutableLiveData<ResponseDetailSiswa?>()
        getDetailSiswa(idSiswa)
        return _siswa as MutableLiveData<ResponseDetailSiswa?>
    }

    fun nilaiSiswa(idMapel: Int, idSiswa: Int): LiveData<ResponseNilaiSiswa?> {
        if (_nilaiSiswa == null) {
            _nilaiSiswa = MutableLiveData<ResponseNilaiSiswa?>()
            getNilaiSiswa(idMapel, idSiswa)
        }
        return _nilaiSiswa as MutableLiveData<ResponseNilaiSiswa?>
    }

    fun maxLevel(idChapter: Int): LiveData<ResponseMaxLevel?> {
        if (_maxLevel == null) {
            _maxLevel = MutableLiveData<ResponseMaxLevel?>()
            getMaxLevel(idChapter)
        }
        return _maxLevel as MutableLiveData<ResponseMaxLevel?>
    }

    fun stateLevelSiswa(idMapel: Int, idSiswa: Int): LiveData<ResponseStateSiswa?> {
        if (_stateLevelSiswa == null) {
            _stateLevelSiswa = MutableLiveData<ResponseStateSiswa?>()
            getStateLevelSiswa(idMapel, idSiswa)
        }
        return _stateLevelSiswa as MutableLiveData<ResponseStateSiswa?>
    }

    fun nilaiRataRata(idSiswa: Int, idMapel: Int): LiveData<ResponseRataRataNilaiSiswa?> {
        _nilaiRataRata = MutableLiveData<ResponseRataRataNilaiSiswa?>()
        getNilaiRataRata(idSiswa, idMapel)
        return _nilaiRataRata as MutableLiveData<ResponseRataRataNilaiSiswa?>
    }

    fun deleteSiswa(idSiswa: Int): LiveData<ResponseDetailSiswa?> {
        _siswa = MutableLiveData<ResponseDetailSiswa?>()
        delSiswa(idSiswa)
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

    private fun getNilaiSiswa(idMapel: Int, idSiswa: Int) {
        val client = ApiConfig.getApiService().getNilaiSiswaByMapel(idMapel, idSiswa)
        client.enqueue(object : Callback<ResponseNilaiSiswa> {
            override fun onResponse(
                call: Call<ResponseNilaiSiswa>,
                response: Response<ResponseNilaiSiswa>
            ) {
                if (response.isSuccessful) {
                    _nilaiSiswa?.postValue(response.body())
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

    private fun getMaxLevel(idChapter: Int) {
        val client = ApiConfig.getApiService().getMaxLevel(idChapter)
        client.enqueue(object : Callback<ResponseMaxLevel> {
            override fun onResponse(
                call: Call<ResponseMaxLevel>,
                response: Response<ResponseMaxLevel>
            ) {
                if (response.isSuccessful) {
                    _maxLevel?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseMaxLevel = ResponseMaxLevel(message = message, status = status)
                    _maxLevel?.postValue(responseMaxLevel)
                    Log.e(TAG, "onFailure: $responseMaxLevel")
                }
            }

            override fun onFailure(call: Call<ResponseMaxLevel>, t: Throwable) {
                _maxLevel?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getStateLevelSiswa(idMapel: Int, idSiswa: Int) {
        val client = ApiConfig.getApiService().getChapterAndLevelSiswaByMapel(idMapel, idSiswa)
        client.enqueue(object : Callback<ResponseStateSiswa> {
            override fun onResponse(
                call: Call<ResponseStateSiswa>,
                response: Response<ResponseStateSiswa>
            ) {
                if (response.isSuccessful) {
                    _stateLevelSiswa?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseSiswa = ResponseStateSiswa(message = message, status = status)
                    _stateLevelSiswa?.postValue(responseSiswa)
                    Log.e(TAG, "onFailure: $responseSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseStateSiswa>, t: Throwable) {
                _stateLevelSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getNilaiRataRata(idSiswa: Int, idMapel: Int) {
        val client = ApiConfig.getApiService().nilaiRataRata(idMapel, idSiswa)
        client.enqueue(object : Callback<ResponseRataRataNilaiSiswa> {
            override fun onResponse(
                call: Call<ResponseRataRataNilaiSiswa>,
                response: Response<ResponseRataRataNilaiSiswa>
            ) {
                if (response.isSuccessful) {
                    _nilaiRataRata?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseRataRataNilaiSiswa =
                        ResponseRataRataNilaiSiswa(message = message, status = status)
                    _nilaiRataRata?.postValue(responseRataRataNilaiSiswa)
                    Log.e(TAG, "onFailure: $responseRataRataNilaiSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseRataRataNilaiSiswa>, t: Throwable) {
                _nilaiRataRata?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun delSiswa(idSiswa: Int) {
        val client = ApiConfig.getApiService().deleteSiswa(idSiswa)
        client.enqueue(object : Callback<ResponseDetailSiswa> {
            override fun onResponse(
                call: Call<ResponseDetailSiswa>,
                response: Response<ResponseDetailSiswa>
            ) {
                if (response.isSuccessful) {
                    _siswa?.postValue(response.body())
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