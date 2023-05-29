package com.example.storyapp.Activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.ViewModel.RegisterViewModel
import com.example.storyapp.ViewModel.ViewModelFactory
import com.example.storyapp.custom.CustomPassword
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.data.UserPreference

private val Context.RegisterDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var etName: EditText
    private lateinit var regBtn: Button
    private lateinit var etEmail: EditText
    private lateinit var custPass: CustomPassword
    private lateinit var vmReg: RegisterViewModel

    private fun cekEmail(str: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()
    }

    private fun cekPass(str: String): Boolean {
        return str.length >= 6
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        getVm()
        regBtn = binding.btnRegister
        custPass = binding.passReg
        etEmail = binding.emailReg
        etName = binding.nameReg

        vmReg.isLod.observe(this) {
            showLoading(it)
        }
        binding.btnRegister.setOnClickListener {
            val regEmail = binding.emailReg.text.toString()
            val regName = binding.nameReg.text.toString()
            val regPass = binding.passReg.text.toString()
            vmReg.Register(regName, regEmail, regPass)
            vmReg.success.observe(this){
                if (it){
                    Toast.makeText(this,"REGISTRATION SUCCESS",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this,"ACCOUNT ALREADY EXIST",Toast.LENGTH_SHORT).show()
                }
            }
        }

        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                btnEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
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

        playAnimation()
    }



    private fun playAnimation() {
        val register = ObjectAnimator.ofFloat(binding.tvReg, View.ALPHA, 1f).setDuration(200)
        val name =
            ObjectAnimator.ofFloat(binding.nameReg, View.ALPHA, 1f).setDuration(200)
        val email = ObjectAnimator.ofFloat(binding.emailReg, View.ALPHA, 1f)
            .setDuration(200)
        val password =
            ObjectAnimator.ofFloat(binding.passReg, View.ALPHA, 1f).setDuration(200)
        val registerButton =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(register, name, email, password, registerButton)
            start()
        }
    }
    private fun getVm() {
        vmReg = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(RegisterDataStore), this)
        )[RegisterViewModel::class.java]

    }

    private fun btnEnable() {
        val name = etName.text
        val email = etEmail.text
        val pwd = custPass.text
        regBtn.isEnabled =
            pwd != null && cekPass(pwd.toString()) && email != null && cekEmail(email.toString()) && name.isNotEmpty()
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loading.visibility = View.VISIBLE
        } else {
            binding.loading.visibility = View.GONE
        }
    }
}