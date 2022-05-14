package com.esprit.takwira.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esprit.takwira.MainActivity
import com.esprit.takwira.R
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.api.RetrofitInstance.ip
import com.esprit.takwira.models.MatchResponseModel
import com.esprit.takwira.models.Stade
import com.esprit.takwira.models.User
import com.esprit.takwira.models.joinTeamModel
import com.esprit.takwira.ui.adapters.StadeAdapter
import com.esprit.takwira.ui.adapters.UsersAdapter
import com.esprit.takwira.ui.home.adapter
import com.esprit.takwira.ui.home.id_Stade
import com.esprit.takwira.ui.home.recyclerViewStade
import com.esprit.takwira.utis.ClickHandler
import com.esprit.takwira.viewmodels.UsersViewModel
import com.esprit.takwira.viewmodels.mainActitvityViewModel
import com.sendbird.android.*
import com.sendbird.android.SendBird.ConnectHandler
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.core
import com.sendbird.uikit.activities.ChannelActivity
import com.sendbird.android.constant.StringSet.core
import com.sendbird.uikit.fragments.OpenChannelFragment

import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.core
import com.sendbird.uikit.activities.OpenChannelSettingsActivity
import com.sendbird.android.constant.StringSet.core
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.activities.CreateChannelActivity
import com.sendbird.android.SendBirdException

import com.sendbird.android.SendBird

import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.users

import com.sendbird.android.GroupChannelParams

import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.core




















class MatchDetails : AppCompatActivity() , ClickHandler {
    lateinit var adapter: UsersAdapter
    lateinit var Name : TextView
    lateinit var Date : TextView
    lateinit var Location : TextView
    lateinit var StadePic: ImageView
    lateinit var recyclerViewUsers: RecyclerView
    lateinit var stade: Stade
    lateinit var teambtn: Button
    lateinit var teambbtn: Button
    lateinit var joinbtn: Button
    lateinit var chatbtn: Button
    var choice : String ="A"
    lateinit var mSharedPref: SharedPreferences
    var isjoined: Boolean = false

    var matchresponse = MatchResponseModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_match_details)

        stade = (intent.getSerializableExtra("stade object") as? Stade)!!

        StadePic = findViewById(R.id.StadePic)
        Name = findViewById(R.id.StadeName)
        Date = findViewById(R.id.StadeDate)
        Location = findViewById(R.id.StadeLocation)
        teambtn = findViewById(R.id.teamChoice)
        teambbtn = findViewById(R.id.teamChoice2)
        chatbtn = findViewById(R.id.btnchat)

        Name.text = stade?.name
        Date.text = stade?.DateTime
        Location.text = stade?.location
        joinbtn = findViewById(R.id.joinbtn)

        recyclerViewUsers = findViewById(R.id.usersRecycler)
        adapter = UsersAdapter(this@MatchDetails, this@MatchDetails)
        recyclerViewUsers.layoutManager = LinearLayoutManager(this@MatchDetails,
            LinearLayoutManager.HORIZONTAL,false)

        recyclerViewUsers.adapter = adapter
        val image0 = stade?.image
        val image1 = image0?.replace("\\", "/");
        val image = image1?.substringAfter("upload/images/")
        if (image != null) {
            Log.w("image url", image)
        }

        Glide.with(this@MatchDetails).load("http://"+ip+":3000/$image").centerCrop().into(StadePic)
        initViewModel(2)

        teambtn.setOnClickListener {
            choice = "A"
            initViewModel(1)

        }
        teambbtn.setOnClickListener {
            choice = "B"
            initViewModel2(1)
        }
        if (isjoined == true) {
            joinbtn.visibility = View.GONE;
        }
        chatbtn.setOnClickListener { chat() }
        joinbtn.setOnClickListener {
            if (choice.equals("A")) {
                getMatch(this@MatchDetails, stade)
                startActivity(getIntent())

            } else if (choice.equals("B")) {
                getMatch(this@MatchDetails, stade)
                startActivity(getIntent())
            }
        }

    }


    private fun initViewModel(teamchoice: Int) {
        val userid = mSharedPref.getString("_ID",null)
        val stadeid = stade._id
        adapter.emptyUsersList()
        var viewModel: UsersViewModel = ViewModelProviders.of(this@MatchDetails,viewModelFactory { UsersViewModel(teamchoice,stade._id!!) }).get(UsersViewModel::class.java)
        viewModel.getLiveDataObserver().observe(this, Observer {it as MutableList<User>?
            if(it != null) {
                val userslist = adapter.getusersList()
                if (userslist != null) {
                    for (user in userslist) {
                        if (user._id.equals(userid)) {
                            isjoined = true
                            joinbtn.text = "Quit Team"
                            joinbtn.setOnClickListener {
                                quimatch(this@MatchDetails, stade)
                                startActivity(getIntent())
                            }
                        }
                    }
                }


                adapter.setUsersList(it)
                adapter.notifyDataSetChanged()



            } else {
            }
        })
        viewModel.makeApiCall(this@MatchDetails)
    }

    private fun initViewModel2(teamchoice: Int) {
        val userid = mSharedPref.getString("_ID",null)
        val stadeid = stade._id
        adapter.emptyUsersList()
        var viewModel: UsersViewModel = ViewModelProviders.of(this@MatchDetails,viewModelFactory { UsersViewModel(teamchoice,stade._id!!) }).get(UsersViewModel::class.java)
        viewModel.getLiveDataObserver().observe(this, Observer {it as MutableList<User>?
            if(it != null) {


                val userslist = adapter.getusersList()
                if (userslist != null) {
                    for (user in userslist) {
                        if (user._id.equals(userid)) {
                            isjoined = true
                            joinbtn.text = "Quit Team"

                            joinbtn.setOnClickListener {
                                quimatch(this@MatchDetails, stade)
                                startActivity(getIntent())
                            }
                        }
                    }
                }
                adapter.setUsersList(it)

                adapter.notifyDataSetChanged()
            } else {

            }
        })
        viewModel.makeApiCall2(this@MatchDetails)
    }
    override fun onBackPressed() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
    }

    override fun ClickItem(position: Int) {
    var user : User? = null

        var viewModel: UsersViewModel = ViewModelProviders.of(this@MatchDetails,viewModelFactory { UsersViewModel(position,stade._id!!) }).get(UsersViewModel::class.java)
        viewModel.getLiveDataObserver().observe(this@MatchDetails, Observer {
            if(it != null) {
                 user = it[position]


            } else {

            }

        })

        viewModel.makeApiCall(this@MatchDetails)
        dialog(user!!)


    }

    fun dialog(user : User) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Player " + user.firstName + " "+user.lastName )
        builder.setMessage(user.firstName + " "+user.lastName + "\n"+ user.email)


        builder.setPositiveButton("ok") { dialog, which ->
            finish()
            startActivity(getIntent());
        }

        builder.show()
    }

    fun getMatch(context: Context?, stade: Stade)  {
        val apiInterface = RetrofitInstance.api(context)

        apiInterface.findmatchwithstadeid(stade._id.toString()).enqueue(object : Callback<MatchResponseModel> {
            override fun onResponse(
                call: Call<MatchResponseModel>,
                response: Response<MatchResponseModel>
            ) {

                matchresponse = response.body()!!

                if (choice.equals("A")) {
                    jointeam(this@MatchDetails, "teamA", matchresponse)
                    startActivity(getIntent())

                } else if (choice.equals("B")) {
                    jointeam(this@MatchDetails, "teamB", matchresponse)
                    startActivity(getIntent())
                }



            }

            override fun onFailure(call: Call<MatchResponseModel>, t: Throwable) {
                Toast.makeText(context, "Error fetching match Response Model", Toast.LENGTH_SHORT)
                    .show()
                Log.w("get match failure", t.localizedMessage!!.toString())
            }

        })
    }

    fun quimatch(context: Context?, stade: Stade)  {
        val apiInterface = RetrofitInstance.api(context)

        apiInterface.findmatchwithstadeid(stade._id.toString()).enqueue(object : Callback<MatchResponseModel> {
            override fun onResponse(
                call: Call<MatchResponseModel>,
                response: Response<MatchResponseModel>
            ) {

                matchresponse = response.body()!!

                if (choice.equals("A")) {
                    doquit(this@MatchDetails, "teamA", matchresponse)
                    startActivity(getIntent())

                } else if (choice.equals("B")) {
                    doquit(this@MatchDetails, "teamB", matchresponse)
                    startActivity(getIntent())
                }



            }

            override fun onFailure(call: Call<MatchResponseModel>, t: Throwable) {
                Toast.makeText(context, "Error fetching match Response Model", Toast.LENGTH_SHORT)
                    .show()
                Log.w("get match failure", t.localizedMessage!!.toString())
            }

        })
    }


    fun jointeam(context: Context?, teamchoice: String, matchresponse : MatchResponseModel )  {
        val apiInterface = RetrofitInstance.api(context)
        val userid = mSharedPref.getString("_ID",null)
        val model = joinTeamModel()
        model.team = teamchoice
        model.userId = userid
        Log.w("match iddddd", matchresponse._id.toString())
        apiInterface.joinmatch(matchresponse._id.toString(), model).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.w("join response", t.localizedMessage!!.toString())
            }


        })
    }

    fun doquit(context: Context?, teamchoice: String, matchresponse : MatchResponseModel )  {
        val apiInterface = RetrofitInstance.api(context)
        val userid = mSharedPref.getString("_ID",null)
        val model = joinTeamModel()
        model.team = teamchoice
        model.userId = userid
        Log.w("match iddddd", matchresponse._id.toString())
        apiInterface.quitmatch(matchresponse._id.toString(), model).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.w("join response", t.localizedMessage!!.toString())
            }


        })
    }



    fun chat () {
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        val userid = mSharedPref.getString("_ID",null)
        //connect the user
        SendbirdUIKit.connect { user, e ->
            if (e != null) {
                if (user != null) {
                    // The user is offline but you can access user information stored in the local cache.
                } else {
                    // The user is offline and you can't access any user information stored in the local cache.
                }
            } else {
                // The user is online and connected to the server.
                val intent = ChannelActivity.newIntent(this@MatchDetails, stade._id.toString())
                startActivity(intent)
            }
        }



    }







}

inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
    }


