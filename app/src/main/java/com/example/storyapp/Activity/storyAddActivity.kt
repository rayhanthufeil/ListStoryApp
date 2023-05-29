package com.example.storyapp.Activity

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.ApiConfig
import com.example.storyapp.ViewModel.ViewModelFactory
import com.example.storyapp.databinding.ActivityStoryAddBinding
import com.example.storyapp.ViewModel.MainViewModel
import com.example.storyapp.addPhoto.reduceFileImage
import com.example.storyapp.addPhoto.rotateBitmap
import com.example.storyapp.addPhoto.uriToFile
import com.example.storyapp.response.StoryUploadResponse
import com.example.storyapp.data.UserPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

val Context.storyAdd: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddPhotoActivity : AppCompatActivity() {
    override fun onRequestPermissionsResult(
        reqCode: Int,
        resPermissions: Array<String>,
        getResults: IntArray
    ) {
        super.onRequestPermissionsResult(reqCode, resPermissions, getResults)
        if (reqCode == REQUEST_CODE_PERMISSIONS) {
            if (!permissionSucces()) {
                Toast.makeText(this, " permission gagal.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun permissionSucces() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }



    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var locClient: FusedLocationProviderClient
    private val mVm: MainViewModel by viewModels { ViewModelFactory(UserPreference.getInstance(storyAdd), this)}
    private lateinit var binding: ActivityStoryAddBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locClient = LocationServices.getFusedLocationProviderClient(this)

        if (!permissionSucces()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        binding.btnUpload.setOnClickListener { mVm.cariUser().observe(this) { ceklast(it.token) } }
        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
    }

    private fun startCameraX() {
        val camX = Intent(this, CameraActivity::class.java)
        IntentCameraX.launch(camX)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        IntentGallery.launch(chooser)
    }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun ceklast(token: String) {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            locClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    addImg(token, location.latitude, location.longitude)
                } else {
                    Toast.makeText(this@AddPhotoActivity, "Location gagal", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestLocPermission.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }


    private fun addImg(token: String, lat: Double, lon: Double) {
        if (getImgFile != null) {
            val Imgfile = reduceFileImage(getImgFile as File)
            val desc = binding.addDesc.text
            val description = "$desc".toRequestBody("text/plain".toMediaType())
            val requestImageFile = Imgfile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val ImgMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", Imgfile.name, requestImageFile
            )
            binding.loading.visibility = View.VISIBLE
            val service = ApiConfig.getApiService()
            service.uploadImage(ImgMultipart, description, lat, lon, "Bearer $token")
                .enqueue(object :
                    Callback<StoryUploadResponse> {
                    override fun onResponse(
                        call: Call<StoryUploadResponse>,
                        response: Response<StoryUploadResponse>
                    ) {
                        if (response.isSuccessful) {
                            binding.loading.visibility = View.INVISIBLE
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                Toast.makeText(this@AddPhotoActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@AddPhotoActivity, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            binding.loading.visibility = View.INVISIBLE
                            Toast.makeText(this@AddPhotoActivity, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<StoryUploadResponse>, t: Throwable) {
                        binding.loading.visibility = View.INVISIBLE
                        Toast.makeText(this@AddPhotoActivity, "Retrofit Gagal ", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(
                this, "Masukan Gmambar.", Toast.LENGTH_SHORT).show()
        }
    }

    private var getImgFile: File? = null
    private val IntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getImgFile = myFile
            val result = rotateBitmap(BitmapFactory.decodeFile(getImgFile?.path), isBackCamera
            )

            binding.viewImage.setImageBitmap(result)
        }


    }

    private val IntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) { result -> if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this)

            getImgFile = myFile

            binding.viewImage.setImageURI(selectedImg)
        }
    }


    private val requestLocPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    mVm.cariUser().observe(this) { ceklast(it.token)
                    }
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    mVm.cariUser().observe(this) { ceklast(it.token)
                    }
                }
            }
        }


}