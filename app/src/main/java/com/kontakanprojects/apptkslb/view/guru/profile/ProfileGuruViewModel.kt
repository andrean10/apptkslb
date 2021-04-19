package com.kontakanprojects.apptkslb.view.guru.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.guru.ResponseDetailGuru
import com.kontakanprojects.apptkslb.model.kelas.ResponseKelas
import com.kontakanprojects.apptkslb.model.mapel.ResponseMapel
import com.kontakanprojects.apptkslb.network.ApiConfig
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileGuruViewModel : ViewModel() {

    private var _guru: MutableLiveData<ResponseDetailGuru?>? = null
    private var _kelas: MutableLiveData<ResponseKelas>? = null
    private var _mapel: MutableLiveData<ResponseMapel>? = null

    private val TAG = ProfileGuruViewModel::class.simpleName

    fun detailGuru(idGuru: Int): LiveData<ResponseDetailGuru?> {
        _guru = MutableLiveData<ResponseDetailGuru?>()
        getDetailGuru(idGuru)
        return _guru as MutableLiveData<ResponseDetailGuru?>
    }

    fun kelas(): LiveData<ResponseKelas> {
        _kelas = MutableLiveData<ResponseKelas>()
        getKelas()
        return _kelas as MutableLiveData<ResponseKelas>
    }

    fun mapel(): LiveData<ResponseMapel> {
        _mapel = MutableLiveData<ResponseMapel>()
        getMapel()
        return _mapel as MutableLiveData<ResponseMapel>
    }

    fun changeFotoProfile(idSiswa: Int, foto: MultipartBody.Part): LiveData<ResponseDetailGuru?> {
        _guru = MutableLiveData<ResponseDetailGuru?>()
        editFotoGuru(idSiswa, foto)
        return _guru as MutableLiveData<ResponseDetailGuru?>
    }

    fun changeDetailProfile(
        idSiswa: Int,
        newData: HashMap<String, String>
    ): LiveData<ResponseDetailGuru?> {
        _guru = MutableLiveData<ResponseDetailGuru?>()
        editDetailGuru(idSiswa, newData)
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

    private fun getKelas() {
        val client = ApiConfig.getApiService().kelas()
        client.enqueue(object : Callback<ResponseKelas> {
            override fun onResponse(call: Call<ResponseKelas>, response: Response<ResponseKelas>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _kelas?.postValue(result!!)
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
                _kelas?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getMapel() {
        val client = ApiConfig.getApiService().mapel()
        client.enqueue(object : Callback<ResponseMapel> {
            override fun onResponse(call: Call<ResponseMapel>, response: Response<ResponseMapel>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _mapel?.postValue(result!!)
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

    private fun editFotoGuru(idSiswa: Int, newFoto: MultipartBody.Part) {
        val client = ApiConfig.getApiService().changePictureGuru(idSiswa, newFoto)
        client.enqueue(object : Callback<ResponseDetailGuru> {
            override fun onResponse(
                call: Call<ResponseDetailGuru>,
                response: Response<ResponseDetailGuru>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _guru?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseDetailGuru =
                        ResponseDetailGuru(message = message, status = status)
                    _guru?.postValue(responseDetailGuru)

                    Log.e(TAG, "onFailure: $responseDetailGuru")
                }
            }

            override fun onFailure(call: Call<ResponseDetailGuru>, t: Throwable) {
                _guru?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun editDetailGuru(idGuru: Int, newData: HashMap<String, String>) {
        val client = ApiConfig.getApiService().editDetailGuru(idGuru, newData)
        client.enqueue(object : Callback<ResponseDetailGuru> {
            override fun onResponse(
                call: Call<ResponseDetailGuru>,
                response: Response<ResponseDetailGuru>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _guru?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseDetailGuru =
                        ResponseDetailGuru(message = message, status = status)
                    _guru?.postValue(responseDetailGuru)

                    Log.e(TAG, "onFailure: $responseDetailGuru")
                }
            }

            override fun onFailure(call: Call<ResponseDetailGuru>, t: Throwable) {
                _guru?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}