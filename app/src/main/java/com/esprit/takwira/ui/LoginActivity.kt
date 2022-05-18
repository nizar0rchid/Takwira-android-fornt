package com.esprit.takwira.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import com.esprit.takwira.MainActivity
import com.esprit.takwira.R
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import org.json.JSONObject







const val PREF_NAME = "LOGIN_PREF_TAKWIRA"
const val EMAIL = "EMAIL"
const val PASSWORD = "PASSWORD"
const val IS_REMEMBRED = "IS_REMEMBRED"
const val ID = "_ID"

class LoginActivity : AppCompatActivity() {
    lateinit var txtEmail: TextInputEditText
    lateinit var txtLayoutEmail: TextInputLayout

    lateinit var txtPassword: TextInputEditText
    lateinit var txtLayoutPassword: TextInputLayout

    lateinit var cbRememberMe: CheckBox

    lateinit var btnLogin: MaterialButton
    lateinit var btnRegister: MaterialButton
    lateinit var mSharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtEmail = findViewById(R.id.txtLogin)
        txtLayoutEmail = findViewById(R.id.txtLayoutLogin)

        txtPassword = findViewById(R.id.txtPassword)
        txtLayoutPassword = findViewById(R.id.txtLayoutPassword)

        cbRememberMe = findViewById(R.id.cbRememberMe)

        btnLogin = findViewById(R.id.btnRegister)

        btnRegister = findViewById(R.id.registerButton)

        //TODO 2 "Initialize the var of SharedPreferences"
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        //TODO 3 "Test in the SharedPreferences if there's data"
        if (mSharedPref.getBoolean(IS_REMEMBRED, false)){
            navigate()
        }

        btnRegister.setOnClickListener {
            navigateToRegister()
        }

        btnLogin.setOnClickListener {
            doLogin()
        }
    }


    private fun navigateToRegister(){
        val mainIntent = Intent(this, RegisterActivity::class.java)
        startActivity(mainIntent)
    }

    private fun navigate(){
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }



    private fun doLogin() {
        if (validate()) {
            val apiInterface = RetrofitInstance.api(this)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val user = User()
            user.email = txtEmail.text.toString()
            user.password = txtPassword.text.toString()
            apiInterface.login(user).enqueue(object : Callback<User> {

                override fun onResponse(call: Call<User>, response: Response<User>) {

                    val status = response.code()
                    Log.w("Code", status.toString())
                    //Log.w("Response", Gson().toJson(response))


                    if (status == 200) {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        user._id = jsonObject.getString("_id").toString()
                        Log.w("userID", user._id!!.toString())
                        mSharedPref.edit().apply{
                            putString(ID, user._id!!.toString())
                        }.apply()
                        if (cbRememberMe.isChecked){
                            mSharedPref.edit().apply{
                                putBoolean(IS_REMEMBRED, true)
                                putString(EMAIL, user.email)
                                putString(PASSWORD, user.password)
                            }.apply()

                        }else{
                            mSharedPref.edit().clear().apply()
                        }
                        Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                        navigate()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                    }

                    //progBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {

                    Toast.makeText(this@LoginActivity, "Connexion error!", Toast.LENGTH_SHORT).show()

                    //progBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

            })

        }
    }


    private fun validate(): Boolean {
        txtLayoutEmail.error = null
        txtLayoutPassword.error = null

        if (txtEmail.text!!.isEmpty()) {
            txtLayoutEmail.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        if (txtPassword.text!!.isEmpty()) {
            txtLayoutPassword.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        return true
    }

    override fun onBackPressed() {
        finishAffinity();
        finish();
    }
}