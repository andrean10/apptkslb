package com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadsoal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontakanprojects.apptkslb.model.chapter.ResponseSoalByChapter
import com.kontakanprojects.apptkslb.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadSoalViewModel : ViewModel() {

    private var _manageSoal = MutableLiveData<ResponseSoalByChapter?>()

    private val TAG = UploadSoalViewModel::class.simpleName

    fun uploadSoal(idChapter: Int, video: MultipartBody.Part?, image: MultipartBody.Part?,
                   soalSuara: MultipartBody.Part, params: HashMap<String, RequestBody>):
            LiveData<ResponseSoalByChapter?> {
        upload(idChapter, video, image, soalSuara, params)
        return _manageSoal
    }

    fun editSoal(idChapter: Int, idSoal: Int, video: MultipartBody.Part, image: MultipartBody.Part?,
                 soalSuara: MultipartBody.Part, params: HashMap<String, RequestBody>):
            LiveData<ResponseSoalByChapter?> {
        ubahSoal(idChapter, idSoal, video, soalSuara, params)
        return _manageSoal
    }

    fun deleteSoal(idChapter: Int, idSoal: Int): LiveData<ResponseSoalByChapter?> {
        hapusSoal(idChapter, idSoal)
        return _manageSoal
    }

    private fun upload(
        idChapter: Int, video: MultipartBody.Part?, image: MultipartBody.Part?,
        soalSuara: MultipartBody.Part, params: HashMap<String, RequestBody>) {
        val client = ApiConfig.getApiService().addSoal(idChapter, video,
            image, soalSuara, params)
        client.enqueue(object : Callback<ResponseSoalByChapter> {
            override fun onResponse(
                call: Call<ResponseSoalByChapter>,
                response: Response<ResponseSoalByChapter>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _manageSoal.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseSoalByChapter =
                        ResponseSoalByChapter(message = message, status = status)
                    _manageSoal.postValue(responseSoalByChapter)

                    Log.e(TAG, "onFailure: $responseSoalByChapter")
                }
            }

            override fun onFailure(call: Call<ResponseSoalByChapter>, t: Throwable) {
                _manageSoal.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun ubahSoal(
        idChapter: Int,
        idSoal: Int,
        video: MultipartBody.Part,
        soalSuara: MultipartBody.Part,
        params: HashMap<String, RequestBody>
    ) {
        val client = ApiConfig.getApiService().editSoal(
            idChapter, idSoal,
            video, soalSuara, params
        )
        client.enqueue(object : Callback<ResponseSoalByChapter> {
            override fun onResponse(
                call: Call<ResponseSoalByChapter>,
                response: Response<ResponseSoalByChapter>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _manageSoal.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseSoalByChapter =
                        ResponseSoalByChapter(message = message, status = status)
                    _manageSoal.postValue(responseSoalByChapter)

                    Log.e(TAG, "onFailure: $responseSoalByChapter")
                }
            }

            override fun onFailure(call: Call<ResponseSoalByChapter>, t: Throwable) {
                _manageSoal.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun hapusSoal(idChapter: Int, idSoal: Int) {
        val client = ApiConfig.getApiService().deleteSoal(idChapter, idSoal)
        client.enqueue(object : Callback<ResponseSoalByChapter> {
            override fun onResponse(
                call: Call<ResponseSoalByChapter>,
                response: Response<ResponseSoalByChapter>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _manageSoal.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseSoalByChapter =
                        ResponseSoalByChapter(message = message, status = status)
                    _manageSoal.postValue(responseSoalByChapter)

                    Log.e(TAG, "onFailure: $responseSoalByChapter")
                }
            }

            override fun onFailure(call: Call<ResponseSoalByChapter>, t: Throwable) {
                _manageSoal.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}
