package com.esprit.takwira.ui


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.esprit.takwira.api.RetrofitInstance
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.esprit.takwira.R
import com.esprit.takwira.databinding.ActivityProfilePicUploadBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import okhttp3.MultipartBody
import com.airbnb.lottie.LottieAnimationView
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import android.widget.Toast
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import okhttp3.MediaType
import okhttp3.RequestBody
import java.text.DecimalFormat
import kotlin.math.pow
import androidx.activity.viewModels
import com.esprit.takwira.MainActivity
import com.esprit.takwira.models.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Thread.sleep


class ProfilePicUpload : AppCompatActivity()  {



    lateinit var mSharedPref: SharedPreferences

    companion object {
        private val PERMISSION_CODE = 1_000
    }
    private var selectedImageUri: Uri? = null
    var ivImage: ImageView?=null
    lateinit var cvImage: MaterialCardView
    lateinit var btnUpload: MaterialButton
    lateinit var pbLoading: LottieAnimationView

    lateinit var binding: ActivityProfilePicUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        binding = ActivityProfilePicUploadBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_profile_pic_upload)

        cvImage = findViewById(R.id.cvImage)
        ivImage = findViewById(R.id.ivImage)
        btnUpload = findViewById(R.id.btnUpload)
        pbLoading = findViewById(R.id.pbLoading)
        pbLoading.visibility = View.GONE


        cvImage.setOnClickListener {

            pickImageFromGallery()

        }
        btnUpload.setOnClickListener {

            startUpload()
        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PERMISSION_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data

            ivImage?.setImageURI(selectedImageUri)

        }
    }

    override fun onBackPressed() {
        //disable back button
    }
    //The compressing process is starting here.





    private fun startUpload() {
        pbLoading.visibility = View.VISIBLE
        if (selectedImageUri == null) {
            println("image null")

            return
        }


        val stream = contentResolver.openInputStream(selectedImageUri!!)
        println("-------------------------------------" + stream)
        val request =
            stream?.let {
                RequestBody.create(
                    "image/jpg".toMediaTypeOrNull(),
                    it.readBytes()
                )
            } // read all bytes using kotlin extension
        val image = request?.let {
            MultipartBody.Part.createFormData(
                "image",
                "image.jpg",
                it
            )
        }
        val apiInterface = RetrofitInstance.api(this)

        if (image?.body != null) {

            println("++++++++++++++++++++++++++++++++++++" + image)
            val userid = mSharedPref.getString("_ID",null)
            if (userid != null) {
                Log.w("passed id", userid)
            }

            apiInterface.upload(userid!!, image).enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    Log.w("Url",response.raw().request.url.toString())
                    if (response.isSuccessful) {
                        Log.i("onResponse goooood", response.body().toString())
                        pbLoading.visibility = View.GONE
                        Toast.makeText(this@ProfilePicUpload, "Please proceed to login", Toast.LENGTH_SHORT).show()
                        navigate()


                    } else {
                        Log.i("OnResponse not good", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    //progress_bar.progress = 0
                    Toast.makeText(this@ProfilePicUpload, "Connexion error!", Toast.LENGTH_SHORT).show()
                    println("noooooooooooooooooo")
                }

            })
        }
        else{
            Toast.makeText(this@ProfilePicUpload, "Choisir une image!", Toast.LENGTH_SHORT).show()
        }


    }

    private fun navigate(){
        val mainIntent = Intent(this, LoginActivity::class.java)
        startActivity(mainIntent)
    }



}