package com.esprit.takwira.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.esprit.takwira.MainActivity
import com.esprit.takwira.R
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.databinding.ActivityProfilePicUploadBinding
import com.esprit.takwira.models.Stade
import com.esprit.takwira.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StadePicUpload : AppCompatActivity() {
    companion object {
        private val PERMISSION_CODE = 1_000
    }

    lateinit var stadeid : String
    private var selectedImageUri: Uri? = null
    var ivImage: ImageView?=null
    lateinit var cvImage: MaterialCardView
    lateinit var btnUpload: MaterialButton
    lateinit var pbLoading: LottieAnimationView

    lateinit var binding: ActivityProfilePicUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stade_pic_upload)

        stadeid = intent.getStringExtra("stadeid").toString()
        cvImage = findViewById(R.id.cvImage1)
        ivImage = findViewById(R.id.ivImage1)
        btnUpload = findViewById(R.id.btnUploadstadepic)
        pbLoading = findViewById(R.id.pbLoading1)
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

            apiInterface.uploadstadepic(stadeid, image).enqueue(object : Callback<Stade> {
                override fun onResponse(
                    call: Call<Stade>,
                    response: Response<Stade>
                ) {
                    Log.w("Url",response.raw().request.url.toString())
                    if (response.isSuccessful) {
                        Log.i("onResponse goooood", response.body().toString())
                        pbLoading.visibility = View.GONE
                        Toast.makeText(this@StadePicUpload, "Done !", Toast.LENGTH_SHORT).show()
                        navigate()


                    } else {
                        Log.i("OnResponse not good", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<Stade>, t: Throwable) {
                    //progress_bar.progress = 0
                    Toast.makeText(this@StadePicUpload, "Connexion error!", Toast.LENGTH_SHORT).show()
                    println("noooooooooooooooooo")
                }

            })
        }
        else{
            Toast.makeText(this@StadePicUpload, "Pick an image !", Toast.LENGTH_SHORT).show()
        }


    }

    private fun navigate(){
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }
}