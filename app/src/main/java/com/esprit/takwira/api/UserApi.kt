package com.esprit.takwira.api

import com.esprit.takwira.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


interface UserApi {
    @POST("login")
    fun login(@Body user: User): Call<User>

    @POST("users")
    fun register(@Body user: User): Call<ResponseBody>

    @PATCH("users/{id}")
    fun update(@Body user: User): Call <User>

    @GET("users/{id}")
    fun getProfile(@Path("id") id : String):Call<User>

    @Multipart
    @PUT("users/{id}")
    fun upload(@Path("id")id: String,  @Part image: MultipartBody.Part): Call<User>


    @GET("stades")
    fun getStade():Call<List<Stade>>

    @POST("stades")
    fun addStade(@Body stade: Stade): Call<ResponseBody>

    @POST("match")
    fun createMatchAndJoin(@Body match : Match): Call<ResponseBody>

    @GET("match/{stadeid}")
    fun findmatchwithstadeid(@Path("stadeid") stadeid : String): Call<MatchResponseModel>

    @PUT("match/{match_id}")
    fun joinmatch(@Path("match_id") match_id : String,@Body jointeammodel: joinTeamModel): Call<ResponseBody>

    @PATCH("match/{match_id}")
    fun quitmatch(@Path("match_id") match_id : String,@Body jointeammodel: joinTeamModel): Call<ResponseBody>
    
    @Multipart
    @PUT("stades/{id}")
    fun uploadstadepic(@Path("id")id: String,  @Part image: MultipartBody.Part): Call<Stade>
}