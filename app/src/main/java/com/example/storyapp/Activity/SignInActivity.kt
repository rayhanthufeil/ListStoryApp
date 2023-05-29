package com.example.storyapp.Activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.*
import com.example.storyapp.ViewModel.ViewModelLogin
import com.example.storyapp.ViewModel.ViewModelFactory
import com.example.storyapp.data.User
import com.example.storyapp.data.UserModel
import com.example.storyapp.data.UserPreference
import com.example.storyapp.databinding.ActivitySigninBinding
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.loginDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var modelUser: UserModel
    private lateinit var custPass: EditText
    private lateinit var btLgn: Button
    private lateinit var etEmail: EditText


    val TOKEN = "token"
    private lateinit var sPR: SharedPreferences
    private lateinit var vmLogin: ViewModelLogin
    private lateinit var binding: ActivitySigninBinding




    private fun cekEmail(str: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()
    }

    private fun cekPass(str: String): Boolean {
        return str.length >= 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        sPR = getSharedPreferences(TOKEN, Context.MODE_PRIVATE)

        val intent = Intent(this, RegisterActivity::class.java)
        setupViewModel()

        btLgn = binding.btnSignIn
        custPass = binding.passSignIn
        etEmail = binding.emailSignIn

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                btnEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        custPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                btnEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        signin()

        binding.hvntAcc.setOnClickListener {
            startActivity(intent)
        }
        Animasi()
    }

    private fun setupViewModel() {
        vmLogin = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(loginDataStore), this))[ViewModelLogin::class.java]


    }
    private fun btnEnable() {
        val email = etEmail.text
        val pwd = custPass.text
        btLgn.isEnabled =
            pwd != null && cekPass(pwd.toString()) && email != null && cekEmail(email.toString())
    }
    private fun signin() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.emailSignIn.text.toString()
            val password = binding.passSignIn.text.toString()
            val SignInService = ApiConfig.getApiService().login(User(email, password))
            val load = binding.loading
            load.visibility = View.VISIBLE
            val editor: SharedPreferences.Editor = sPR.edit()
            if (cekEmail(email)) {
                if (cekPass(password)) {
                    SignInService.enqueue(object : Callback<LoginResponse> {
                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            load.visibility = View.INVISIBLE
                            Toast.makeText(this@LoginActivity,t.message,  Toast.LENGTH_SHORT).show()
                            println("GAGAL")
                        }

                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                load.visibility = View.INVISIBLE
                                Toast.makeText(  this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT)
                                    .show()
                                val main = Intent(this@LoginActivity, MainActivity::class.java)
                                val responseBody = response.body()
                                val jsonObj = JSONTokener(responseBody?.loginResult.toString()).nextValue() as JSONObject
                                val token = jsonObj.getString("token")
                                vmLogin.SignIn()
                                vmLogin.Simpan(UserModel(token, isLogin = true))
                                startActivity(main)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Login failed!", Toast.LENGTH_SHORT).show()
                                load.visibility = View.INVISIBLE
                                println("GAGAL")
                            }
                        }
                    })
                } else {
                    load.visibility = View.INVISIBLE
                    Toast.makeText(this@LoginActivity, "Password harus 6 Karakter", Toast.LENGTH_SHORT).show()
                }
            } else {
                load.visibility = View.INVISIBLE
                Toast.makeText(this@LoginActivity,"Email tidak valid", Toast.LENGTH_SHORT ).show()
            }
        }
    }



    private fun Animasi() {
        ObjectAnimator.ofFloat(binding.logoSignIn, View.TRANSLATION_X, -38f, 38f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

    }
}