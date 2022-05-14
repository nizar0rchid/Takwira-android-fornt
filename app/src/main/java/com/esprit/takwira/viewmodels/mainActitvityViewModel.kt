package com.esprit.takwira.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.models.Stade
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class mainActitvityViewModel: ViewModel() {

    lateinit var lifeDataList: MutableLiveData<List<Stade>>
    init {
        lifeDataList = MutableLiveData()

    }
    fun getLiveDataObserver():MutableLiveData<List<Stade>>{
        return lifeDataList
    }

    fun makeApiCall(context: Context?){
        val apiInterface = RetrofitInstance.api(context)
        //sharedPreferences = getApplication(this).getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // apiInterface.getStade(sharedPreferences.getString("token",null)!!).enqueue(object : Callback<List<Stade>> {
        apiInterface.getStade().enqueue(object : Callback<List<Stade>> {
            override fun onFailure(call: Call<List<Stade>>, t: Throwable) {
                lifeDataList.postValue(null)
            }

            override fun onResponse(
                call: Call<List<Stade>>,
                response: Response<List<Stade>>

            ) {
                Log.i(
                    "response",
                    response.body().toString()
                )
                lifeDataList.postValue(response.body())
            }
        })

    }
}