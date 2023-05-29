package com.example.storyapp.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.ApiConfig
import com.example.storyapp.RegisterResponse
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.data.UserBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {


    private val _liststoryreg = MutableLiveData<List<ListStoryItem>>()
    val liststoryreg: LiveData<List<ListStoryItem>> = _liststoryreg
    private val _isLod = MutableLiveData<Boolean>()
    val isLod: LiveData<Boolean> = _isLod
    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun Register(name:String,email:String,pass:String) {
        _isLod.value = true
        val retIn = ApiConfig.getApiService()
        val datareg = UserBody(name, email, pass)
        retIn.register(datareg).enqueue(object :
            Callback<RegisterResponse> {
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLod.value = false
                _success.value = false
                Log.d("y", "gasabi")
            }
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.code() == 201) {
                    _isLod.value = false
                    _success.value = true
                    Log.d("y", "sabi")

                } else {
                    _isLod.value = false
                    _success.value = false
                    Log.d("y", "gasabi")
                }
            }
        })
    }
}