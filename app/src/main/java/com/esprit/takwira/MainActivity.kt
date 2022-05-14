package com.esprit.takwira

import android.content.SharedPreferences
import android.os.Bundle
import android.provider.UserDictionary.Words

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration

import androidx.navigation.ui.setupWithNavController
import com.esprit.takwira.databinding.ActivityMainBinding




import android.provider.UserDictionary.Words.APP_ID
import android.util.Log
import android.widget.Toast
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.models.User
import com.esprit.takwira.ui.PREF_NAME
import com.sendbird.android.*

import com.sendbird.uikit.interfaces.UserInfo

import com.sendbird.uikit.adapter.SendbirdUIKitAdapter

import com.sendbird.uikit.SendbirdUIKit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.sendbird.android.SendBirdException

import com.sendbird.android.handlers.InitResultHandler

import android.provider.UserDictionary.Words.APP_ID

import com.sendbird.android.SendBird

import com.sendbird.android.constant.StringSet.core






class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mSharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        val userid = mSharedPref.getString("_ID",null)
        SendBird.init("3EB3B0C5-F80E-4DFA-B252-FAC0D44892AF", this@MainActivity, true, object : InitResultHandler {
            override fun onMigrationStarted() {
                Log.i("Application", "Called when there's an update in Sendbird server.")
            }

            override fun onInitFailed(e: SendBirdException) {
                Log.i(
                    "Application",
                    "Called when initialize failed. SDK will still operate properly as if useLocalCaching is set to false."
                )
            }

            override fun onInitSucceed() {
                Log.i("Application", "Called when initialization is completed.")
                SendBird.connect(userid) { user, e ->
                    if (user != null) {
                        if (e != null) {
                            // Proceed in offline mode with the data stored in the local database.
                            // Later, connection will be made automatically
                            // and can be notified through the ConnectionHandler.onReconnectSucceeded().
                        } else {
                            // Proceed in online mode.
                        }
                    } else {
                        // Handle error.
                    }
                }
            }
        })




        val apiInterface = RetrofitInstance.api(this@MainActivity)
        var user = User()
        apiInterface.getProfile(userid!!).enqueue(object : Callback<User> {

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Connexion Problem", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<User>, response: Response<User>) {



                if (response.isSuccessful){

                    user = response.body()!!
                    val image = user.profilePic?.substringAfter("upload\\images\\")
                    user.profilePic = "http://"+ RetrofitInstance.ip +":3000/$image"
                }
            }

        })

        SendbirdUIKit.init(object : SendbirdUIKitAdapter {
            override fun getAppId(): String {
                return "3EB3B0C5-F80E-4DFA-B252-FAC0D44892AF" // Specify your Sendbird application ID.
            }

            override fun getAccessToken(): String? {
                return ""
            }

            override fun getUserInfo(): UserInfo {
                return object : UserInfo {
                    override fun getUserId(): String {
                        return userid.toString() // Specify your user ID.
                    }

                    override fun getNickname(): String? {
                        return user.firstName+" "+user.lastName // Specify your user nickname.
                    }

                    override fun getProfileUrl(): String? {
                        return user.profilePic.toString()
                    }
                }
            }

            override fun getInitResultHandler(): InitResultHandler {
                return object : InitResultHandler {
                    override fun onMigrationStarted() {
                        // DB migration has started.
                    }

                    override fun onInitFailed(e: SendBirdException) {
                        // If DB migration fails, this method is called.
                    }

                    override fun onInitSucceed() {
                        // If DB migration is successful, this method is called and you can proceed to the next step.
                        // In the sample app, the `LiveData` class notifies you on the initialization progress
                        // And observes the `MutableLiveData<InitState> initState` value in `SplashActivity()`.
                        // If successful, the `LoginActivity` screen
                        // Or the `HomeActivity` screen will show.
                    }
                }
            }
        }, this)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }





}