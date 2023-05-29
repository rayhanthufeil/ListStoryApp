package com.example.storyapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.UserModel
import com.example.storyapp.data.UserPreference
import kotlinx.coroutines.launch

class ViewModelLogin(private val pref: UserPreference) : ViewModel() {


    fun SignIn() {
        viewModelScope.launch {
            pref.login()
        }
    }

    fun Simpan(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}