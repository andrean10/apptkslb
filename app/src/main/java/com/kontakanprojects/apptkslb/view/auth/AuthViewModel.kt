package com.kontakanprojects.apptkslb.view.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.ResponseAuth
import com.kontakanprojects.apptkslb.model.kelas.ResponseKelas
import com.kontakanprojects.apptkslb.model.mapel.ResponseMapel
import com.kontakanprojects.apptkslb.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private var _login = MutableLiveData<ResponseAuth>()
    private var _register: MutableLiveData<ResponseAuth>? = null

    private var _kelas: MutableLiveData<ResponseKelas>? = null
    private var _mapel: MutableLiveData<ResponseMapel>? = null

    private val TAG = AuthViewModel::class.simpleName

    fun login(params: HashMap<String, Any>): LiveData<ResponseAuth> {
        _login = getLogin(params)
        return _login
    }

    fun register(params: HashMap<String, Any>): LiveData<ResponseAuth> {
        _register = MutableLiveData<ResponseAuth>()
        getRegister(params)
        return _register as MutableLiveData<ResponseAuth>
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

    private fun getLogin(params: HashMap<String, Any>): MutableLiveData<ResponseAuth> {
        val client = ApiConfig.getApiService().login(params)
        client.enqueue(object : Callback<ResponseAuth> {
            override fun onResponse(call: Call<ResponseAuth>, response: Response<ResponseAuth>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _login.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseAuth = ResponseAuth(message = message, status = status)
                    _login.postValue(responseAuth)

                    Log.e(TAG, "onFailure: $responseAuth")
                }
            }

            override fun onFailure(call: Call<ResponseAuth>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _login
    }

    private fun getRegister(params: HashMap<String, Any>) {
        val client = ApiConfig.getApiService().register(params)
        client.enqueue(object : Callback<ResponseAuth> {
            override fun onResponse(call: Call<ResponseAuth>, response: Response<ResponseAuth>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _register?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseAuth = ResponseAuth(message = message, status = status)
                    _register?.postValue(responseAuth)

                    Log.e(TAG, "onFailure: $responseAuth")
                }
            }

            override fun onFailure(call: Call<ResponseAuth>, t: Throwable) {
                _register?.postValue(null)
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
}