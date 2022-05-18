package com.esprit.takwira.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.esprit.takwira.R
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.api.RetrofitInstance.ip
import com.esprit.takwira.models.User
import com.esprit.takwira.ui.ID
import com.esprit.takwira.ui.PREF_NAME
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    lateinit var profilename : TextView
    lateinit var email :  TextView
    lateinit var phone :  TextView
    lateinit var location :  TextView
    lateinit var imageView: ImageView
    lateinit var mSharedPref : SharedPreferences
    lateinit var logoutbtn: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        profilename = rootView.findViewById(R.id.user_profile_name)
        email = rootView.findViewById(R.id.profileEmail)
        phone = rootView.findViewById(R.id.profilePhone)
        location = rootView.findViewById(R.id.profileLocation)
        imageView = rootView.findViewById(R.id.user_profile_photo)
        logoutbtn = rootView.findViewById(R.id.logoutBtn)

        mSharedPref = requireActivity().getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)
        val idUser: String = mSharedPref.getString(ID, null).toString()

        val apiInterface = RetrofitInstance.api(context)
        apiInterface.getProfile(idUser).enqueue(object : Callback<User> {

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, "Connexion Problem", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.w("Url",response.raw().request.url.toString())
                Log.w("body",response.body().toString())


                if (response.isSuccessful){
                    val user : User = response.body()!!
                    Log.w("image url", user.firstName!!)
                    profilename.text = user.firstName+" "+user.lastName
                    email.text = user.email
                    phone.text = user.phone
                    location.text = user.location
                    val image = user.profilePic?.substringAfter("upload\\images\\")
                    Log.w("image url", user.profilePic!!)
                    Glide.with(this@ProfileFragment).load("http://"+ip+":3000/$image").into(imageView)
                }
            }


        })

        logoutbtn.setOnClickListener {
            requireActivity().getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE).edit().clear().apply()
            requireActivity().finish()
        }

        return rootView
    }


}