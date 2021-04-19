package com.kontakanprojects.apptkslb.view.siswa.soal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.chapter.ResponseSoal
import com.kontakanprojects.apptkslb.model.chapter.ResponseSoalByChapter
import com.kontakanprojects.apptkslb.model.mapel.ResponseMapel
import com.kontakanprojects.apptkslb.model.siswa.ResponseNilaiSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResponseStateSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SoalViewModel : ViewModel() {

    private var _soal = MutableLiveData<ResponseSoalByChapter?>()
    private var _stateLevelSiswa: MutableLiveData<ResponseStateSiswa?>? = null
    private var _storeNilaiSiswa: MutableLiveData<ResponseNilaiSiswa?>? = null
    private var _jawabanLevelSatu: MutableLiveData<ResponseSoal?>? = null
    private var _riwayatMapel: MutableLiveData<ResponseMapel?>? = null

    private val TAG = SoalViewModel::class.simpleName

    fun soal(idChapter: Int): LiveData<ResponseSoalByChapter?> {
        getSoal(idChapter)
        return _soal
    }

    fun chapterAndLevelSiswa(
        idSiswa: Int,
        idChapterLevelSiswa: Int
    ): LiveData<ResponseStateSiswa?> { // ambil chapter siswa yang sudah dikerjakan
        _stateLevelSiswa = MutableLiveData<ResponseStateSiswa?>()
        getChapterAndLevelSiswa(idSiswa, idChapterLevelSiswa)
        return _stateLevelSiswa as MutableLiveData<ResponseStateSiswa?>
    }

    fun stateLevelSiswa(idSiswa: Int, params: HashMap<String, Int>): LiveData<ResponseStateSiswa?> {
        _stateLevelSiswa = MutableLiveData<ResponseStateSiswa?>()
        addStateLevelSiswa(idSiswa, params)
        return _stateLevelSiswa as MutableLiveData<ResponseStateSiswa?>
    }

    fun updateStateLevelSiswa(
        idSiswa: Int,
        idChapterLevelSiswa: Int,
        params: HashMap<String, Int>
    ): LiveData<ResponseStateSiswa?> {
        _stateLevelSiswa = MutableLiveData<ResponseStateSiswa?>()
        updateLevelSiswa(idSiswa, idChapterLevelSiswa, params)
        return _stateLevelSiswa as MutableLiveData<ResponseStateSiswa?>
    }

    fun storeNilaiSiswa(idSiswa: Int, idChapter: Int): LiveData<ResponseNilaiSiswa?> {
        _storeNilaiSiswa = MutableLiveData<ResponseNilaiSiswa?>()
        addNilaiSiswa(idSiswa, idChapter)
        return _storeNilaiSiswa as MutableLiveData<ResponseNilaiSiswa?>
    }

    fun updateNilaiSiswa(
        idSiswa: Int,
        idNilai: Int,
        idChapter: Int,
        idChapterLevelSiswa: Int
    ): LiveData<ResponseNilaiSiswa?> {
        _storeNilaiSiswa = MutableLiveData<ResponseNilaiSiswa?>()
        editNilaiSiswa(idSiswa, idNilai, idChapter, idChapterLevelSiswa)
        return _storeNilaiSiswa as MutableLiveData<ResponseNilaiSiswa?>
    }

    fun jawabanChapterLevelSatu(idChapter: Int): LiveData<ResponseSoal?> {
        _jawabanLevelSatu = MutableLiveData<ResponseSoal?>()
        getJawabanChapterLevelSatu(idChapter)
        return _jawabanLevelSatu as MutableLiveData<ResponseSoal?>
    }

    fun addRiwayatMapelSiswa(idSiswa: Int, idMapel: Int): LiveData<ResponseMapel?> {
        _riwayatMapel = MutableLiveData<ResponseMapel?>()
        riwayatMapel(idSiswa, idMapel)
        return _riwayatMapel as MutableLiveData<ResponseMapel?>
    }

    private fun getSoal(idChapter: Int) {
        val client = ApiConfig.getApiService().soal(idChapter)
        client.enqueue(object : Callback<ResponseSoalByChapter> {
            override fun onResponse(
                call: Call<ResponseSoalByChapter>,
                response: Response<ResponseSoalByChapter>
            ) {
                if (response.isSuccessful) {
                    _soal.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseSoalByChapter =
                        ResponseSoalByChapter(message = message, status = status)
                    _soal.postValue(responseSoalByChapter)

                    Log.e(TAG, "onFailure: $responseSoalByChapter")
                }
            }

            override fun onFailure(call: Call<ResponseSoalByChapter>, t: Throwable) {
                _soal.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getChapterAndLevelSiswa(idSiswa: Int, idChapterLevelSiswa: Int) {
        val client =
            ApiConfig.getApiService().getDetailStateLevelSiswa(idSiswa, idChapterLevelSiswa)
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
                    val responseChapterSiswa =
                        ResponseStateSiswa(message = message, status = status)
                    _stateLevelSiswa?.postValue(responseChapterSiswa)

                    Log.e(TAG, "onFailure: $responseChapterSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseStateSiswa>, t: Throwable) {
                _stateLevelSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun addStateLevelSiswa(idSiswa: Int, params: HashMap<String, Int>) {
        val client = ApiConfig.getApiService().addStateLevelSiswa(idSiswa, params)
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
                    val responseChapterAndLevelSiswa =
                        ResponseStateSiswa(message = message, status = status)
                    _stateLevelSiswa?.postValue(responseChapterAndLevelSiswa)
                    Log.e(TAG, "onFailure: $responseChapterAndLevelSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseStateSiswa>, t: Throwable) {
                _stateLevelSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun updateLevelSiswa(
        idSiswa: Int,
        idChapterLevelSiswa: Int,
        params: HashMap<String, Int>
    ) {
        val client =
            ApiConfig.getApiService().updateStateLevelSiswa(idSiswa, idChapterLevelSiswa, params)
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
                    val responseChapterAndLevelSiswa =
                        ResponseStateSiswa(message = message, status = status)
                    _stateLevelSiswa?.postValue(responseChapterAndLevelSiswa)

                    Log.e(TAG, "onFailure: $responseChapterAndLevelSiswa")
                }
            }

            override fun onFailure(call: Call<ResponseStateSiswa>, t: Throwable) {
                _stateLevelSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun addNilaiSiswa(idSiswa: Int, idChapter: Int) {
        val client = ApiConfig.getApiService().storeNilai(idSiswa, idChapter)
        client.enqueue(object : Callback<ResponseNilaiSiswa> {
            override fun onResponse(
                call: Call<ResponseNilaiSiswa>,
                response: Response<ResponseNilaiSiswa>
            ) {
                if (response.isSuccessful) {
                    _storeNilaiSiswa?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseScoreAndTotalCorrect =
                        ResponseNilaiSiswa(message = message, status = status)
                    _storeNilaiSiswa?.postValue(responseScoreAndTotalCorrect)

                    Log.e(TAG, "onFailure: $responseScoreAndTotalCorrect")
                }
            }

            override fun onFailure(call: Call<ResponseNilaiSiswa>, t: Throwable) {
                _storeNilaiSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun editNilaiSiswa(
        idSiswa: Int,
        idNilai: Int,
        idChapter: Int,
        idChapterLevelSiswa: Int
    ) {
        val client = ApiConfig.getApiService().updateNilai(
            idSiswa, idNilai, idChapter, idChapterLevelSiswa
        )
        client.enqueue(object : Callback<ResponseNilaiSiswa> {
            override fun onResponse(
                call: Call<ResponseNilaiSiswa>,
                response: Response<ResponseNilaiSiswa>
            ) {
                if (response.isSuccessful) {
                    _storeNilaiSiswa?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseScoreAndTotalCorrect =
                        ResponseNilaiSiswa(message = message, status = status)
                    _storeNilaiSiswa?.postValue(responseScoreAndTotalCorrect)

                    Log.e(TAG, "onFailure: $responseScoreAndTotalCorrect")
                }
            }

            override fun onFailure(call: Call<ResponseNilaiSiswa>, t: Throwable) {
                _storeNilaiSiswa?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getJawabanChapterLevelSatu(idChapter: Int) {
        val client = ApiConfig.getApiService().getJawabanChapterLevelSatu(idChapter)
        client.enqueue(object : Callback<ResponseSoal> {
            override fun onResponse(
                call: Call<ResponseSoal>,
                response: Response<ResponseSoal>
            ) {
                if (response.isSuccessful) {
                    _jawabanLevelSatu?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseSoal =
                        ResponseSoal(message = message, status = status)
                    _jawabanLevelSatu?.postValue(responseSoal)

                    Log.e(TAG, "onFailure: $responseSoal")
                }
            }

            override fun onFailure(call: Call<ResponseSoal>, t: Throwable) {
                _jawabanLevelSatu?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun riwayatMapel(idSiswa: Int, idMapel: Int) {
        val client = ApiConfig.getApiService().addRiwayatMapel(idSiswa, idMapel)
        client.enqueue(object : Callback<ResponseMapel> {
            override fun onResponse(
                call: Call<ResponseMapel>,
                response: Response<ResponseMapel>
            ) {
                if (response.isSuccessful) {
                    _riwayatMapel?.postValue(response.body())
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseMapel = ResponseMapel(message = message, status = status)
                    _riwayatMapel?.postValue(responseMapel)

                    Log.e(TAG, "onFailure: $responseMapel")
                }
            }

            override fun onFailure(call: Call<ResponseMapel>, t: Throwable) {
                _riwayatMapel?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}