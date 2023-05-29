package com.example.storyapp.ViewModel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.ApiConfig
import com.example.storyapp.paging.StoryRepository
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.data.UserModel
import com.example.storyapp.data.UserPreference
import kotlinx.coroutines.launch


class MainViewModel(private val Mainpref: UserPreference, pagingStory: StoryRepository) : ViewModel() {

    private val _liststory = MutableLiveData<List<ListStoryItem>>()
    val liststory: LiveData<List<ListStoryItem>> = _liststory
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val userpref = Mainpref.getUser()
    val mUser = userpref.asLiveData()

    val stories: LiveData<PagingData<ListStoryItem>> =
        mUser.switchMap {
        pagingStory.getListStory("Bearer "+it.token)
            .cachedIn(viewModelScope)
        }

    suspend fun getLocation(token: String){
        _isLoading.value = true
        val responseData = ApiConfig.getApiService().getAllStory(20,1,token = "Bearer $token").listStory
        _liststory.value = responseData
    }

    fun cariUser(): LiveData<UserModel> {
        return Mainpref.getUser().asLiveData()
    }

    fun SignOut() {
        viewModelScope.launch {
            Mainpref.logout()
        }
    }
}