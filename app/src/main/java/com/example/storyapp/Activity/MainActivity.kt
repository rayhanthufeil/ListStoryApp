package com.example.storyapp.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.ViewModel.ViewModelFactory
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.Adapter.PagingAdapter
import com.example.storyapp.ViewModel.MainViewModel
import com.example.storyapp.data.UserModel
import com.example.storyapp.data.UserPreference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var modelUser: UserModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getViewModel()
        binding.rvSatu.layoutManager =LinearLayoutManager(this)

        vm.cariUser().observe(this){}

        ambildata()
    }




    private fun getViewModel() {
        vm = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore),this)
        )[MainViewModel::class.java]

        vm.cariUser().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                this.modelUser = user
            }
        }
    }

    private fun ambildata() {
        showLoading()
        val adapter = PagingAdapter()
        binding.rvSatu.adapter = adapter
        vm.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }

    }

    private fun showLoading() {
        vm.isLoading.observe(this) { binding?.apply {
            if (it) {
                rvSatu.visibility = View.INVISIBLE
                load.visibility = View.VISIBLE

            } else {
                rvSatu.visibility = View.VISIBLE
                load.visibility = View.GONE

            }
        }
        }
    }
    private fun logout() {
        vm.SignOut()
        vm.cariUser().observe(this) {
            println("Token: " + it.token)
        }
        finish()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.location -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
            R.id.logout -> {
                logout()
            }
            R.id.setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.addStory -> {
                startActivity(Intent(this@MainActivity, AddPhotoActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}