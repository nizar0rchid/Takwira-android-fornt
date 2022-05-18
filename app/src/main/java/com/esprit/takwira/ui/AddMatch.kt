package com.esprit.takwira.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.view.View
import android.widget.Button
import java.util.*
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.Toast
import com.esprit.takwira.R
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.models.Match
import com.esprit.takwira.models.Stade
import com.esprit.takwira.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelParams
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import java.text.SimpleDateFormat
import java.util.logging.Logger

class AddMatch : AppCompatActivity() {
    lateinit var txtStadeName: TextInputEditText
    lateinit var txtLayoutStadeName: TextInputLayout

    lateinit var txtStadeLocation: TextInputEditText
    lateinit var txtLayoutStadeLocation: TextInputLayout

    lateinit var txtStadeCapacity: TextInputEditText
    lateinit var txtLayoutStadeCapacity: TextInputLayout

    lateinit var txtStadePrice: TextInputEditText
    lateinit var txtLayoutStadePrice: TextInputLayout

    lateinit var pickmap : Button
    lateinit var addmatchbtn : Button

    lateinit var mSharedPref: SharedPreferences


    var button_date: Button? = null
    var textview_date: TextView? = null
    var cal = Calendar.getInstance()
    companion object {
        const val START_ACTIVITY_3_REQUEST_CODE = 0
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_match)

        textview_date = findViewById(R.id.text_view_date_1)
        button_date = findViewById(R.id.button_date_1)

        txtStadeName = findViewById(R.id.txtStadeName)
        txtLayoutStadeName = findViewById(R.id.txtLayoutStadeName)

        txtStadeLocation = findViewById(R.id.txtStadeLocation)
        txtLayoutStadeLocation = findViewById(R.id.txtLayoutStadeLocation)

        txtStadeCapacity = findViewById(R.id.txtStadeCapacity)
        txtLayoutStadeCapacity = findViewById(R.id.txtLayoutStadeCapacity)

        txtStadePrice = findViewById(R.id.txtStadePrice)
        txtLayoutStadePrice = findViewById(R.id.txtLayoutStadePrice)

        addmatchbtn = findViewById(R.id.btnAddStade)
        pickmap = findViewById(R.id.pickLocationBtn)

        pickmap.setOnClickListener {
            val intent = Intent(this, mapChoice::class.java)
            startActivityForResult(intent, START_ACTIVITY_3_REQUEST_CODE)
        }

        textview_date!!.text = "--/--/----"

        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@AddMatch,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        addmatchbtn.setOnClickListener { addStade() }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_ACTIVITY_3_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val longitude = data!!.getStringExtra("longitude")
                val latitude = data!!.getStringExtra("latitude")
                txtStadeLocation.setText(latitude+","+longitude)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }





    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
        textview_date!!.text = sdf.format(cal.getTime())
    }

    private fun addStade() {
        if (validate()) {
            val apiInterface = RetrofitInstance.api(this)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val stade = Stade()
            stade.name = txtStadeName.text!!.toString()
            stade.location = txtStadeLocation.text!!.toString()
            stade.capacity = txtStadeCapacity.text!!.toString().toInt()
            stade.price = txtStadePrice.text!!.toString().toFloat()
            stade.DateTime = textview_date!!.text.toString()

            apiInterface.addStade(stade).enqueue(object : Callback<ResponseBody> {

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val stringResponse = response.body()?.string().toString()
                    //val jresponse = JSONObject(stringResponse!!)
                    val stadeid1 = stringResponse.drop(1)
                    val stadeid = stadeid1.dropLast(1)


                    val status = response.code()
                    Log.w("Status Code", status.toString())
                    Log.w("Response", stringResponse.toString())
                    Log.w("Stade id", stadeid)

                    mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                    //TODO 3 "Test in the SharedPreferences if there's data"
                    val id = mSharedPref.getString(ID,"").toString()

                    createMatchAndJoin(id, stadeid, txtStadeCapacity.text!!.toString().toInt())


                    if (status == 200) {
                        Toast.makeText(this@AddMatch, "Please proceed to adding a picture !", Toast.LENGTH_LONG).show()
                        createGroupChat(stade.name.toString(), stadeid)
                        navigate(stadeid.toString())
                    } else if (status != 200) {
                        Toast.makeText(this@AddMatch, "Some error occured !", Toast.LENGTH_LONG).show()
                    }

                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    Toast.makeText(this@AddMatch, "Connexion error!", Toast.LENGTH_SHORT).show()

                    //progBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

            })


        }
    }


    private fun createMatchAndJoin(userId: String, stadeId: String, teamCapacity : Int) {
        val apiInterface = RetrofitInstance.api(this)
        val capacity  = (teamCapacity/2)

        val match = Match()

        match.stadeId = stadeId
        match.userId = userId
        match.teamCapacity = capacity

        apiInterface.createMatchAndJoin(match).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.w("match created","Match created and joined")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

        })
    }

    private fun navigate(stadeid : String) {
        val intent = Intent(this, StadePicUpload::class.java)
        intent.putExtra("stadeid", stadeid)
        startActivity(intent)
    }

    private fun validate(): Boolean {
        txtLayoutStadeName.error = null
        txtLayoutStadeLocation.error = null
        txtLayoutStadeCapacity.error = null
        txtLayoutStadePrice.error = null


        if (txtStadeName.text!!.isEmpty()) {
            txtLayoutStadeName.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        if (txtStadeLocation.text!!.isEmpty()) {
            txtLayoutStadeLocation.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        if (txtStadeCapacity.text!!.isEmpty()) {
            txtLayoutStadeCapacity.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        if (txtStadePrice.text!!.isEmpty()) {
            txtLayoutStadePrice.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        return true
    }


    private fun createGroupChat(stadename : String, stadeid : String) {
        //create channel
        val users: MutableList<String> = ArrayList()


        val operators: MutableList<String> = ArrayList()

        val params = GroupChannelParams()
            .setPublic(true)
            .setEphemeral(false)
            .setDistinct(false)
            .setSuper(false)
            .setName(stadename)
            .setChannelUrl(stadeid) // In a group channel, you can create a new channel by specifying its unique channel URL in a 'GroupChannelParams' object.


        GroupChannel.createChannel(params
        ) { groupChannel, p1 ->  };
    }



}
