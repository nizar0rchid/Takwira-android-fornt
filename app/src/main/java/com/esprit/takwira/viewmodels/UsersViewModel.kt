package com.esprit.takwira.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.models.MatchResponseModel
import com.esprit.takwira.models.Stade
import com.esprit.takwira.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class  UsersViewModel(val teamchoice:Int,
                      val stadeid:String
):ViewModel()
{


    lateinit var lifeDataList: MutableLiveData<List<User>>
    var userslist = ArrayList<User>()
    init {
        lifeDataList = MutableLiveData()
        lifeDataList.value = null
    }
    fun getLiveDataObserver():MutableLiveData<List<User>>{
        return lifeDataList
    }

    fun makeApiCall(context: Context?) {
        val apiInterface = RetrofitInstance.api(context)
        apiInterface.findmatchwithstadeid(stadeid).enqueue(object : Callback<MatchResponseModel> {
            override fun onResponse(
                call: Call<MatchResponseModel>,
                response: Response<MatchResponseModel>
            ) {

                val matchresponse = response.body()!!


                    getusersA(context, matchresponse)



            }

            override fun onFailure(call: Call<MatchResponseModel>, t: Throwable) {
                Toast.makeText(context, "Error fetching match Response Model", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    fun makeApiCall2(context: Context?) {
        val apiInterface = RetrofitInstance.api(context)
        apiInterface.findmatchwithstadeid(stadeid).enqueue(object : Callback<MatchResponseModel> {
            override fun onResponse(
                call: Call<MatchResponseModel>,
                response: Response<MatchResponseModel>
            ) {

                val matchresponse = response.body()!!


                getusersB(context, matchresponse)



            }

            override fun onFailure(call: Call<MatchResponseModel>, t: Throwable) {
                Toast.makeText(context, "Error fetching match Response Model", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }


    fun getusersA(context: Context?, matchresponse: MatchResponseModel ){
        val apiInterface = RetrofitInstance.api(context)

        var choice = matchresponse.teamA!!

        Log.w("viewmodel",choice.toString())
        for (userid in choice) {

            if (userid != null) {
                apiInterface.getProfile(userid).enqueue(object : Callback<User> {

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(context, "Connexion Problem", Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<User>, response: Response<User>) {

                        val user : User? = response.body()

                        userslist.add(user!!)


                        lifeDataList.value = (userslist)

                    }


                })
            }

        }





    }

    fun getusersB(context: Context?, matchresponse: MatchResponseModel ){
        val apiInterface = RetrofitInstance.api(context)

        var choice = matchresponse.teamB!!

        Log.w("viewmodel",choice.toString())
        for (userid in choice) {

            if (userid != null) {
                apiInterface.getProfile(userid).enqueue(object : Callback<User> {

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(context, "Connexion Problem", Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<User>, response: Response<User>) {

                        val user : User? = response.body()

                        userslist.add(user!!)


                        lifeDataList.value = (userslist)

                    }


                })
            }

        }





    }



}