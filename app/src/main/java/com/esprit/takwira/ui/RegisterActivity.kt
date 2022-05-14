package com.esprit.takwira.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.esprit.takwira.R
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject




class RegisterActivity : AppCompatActivity() {
    lateinit var mSharedPref: SharedPreferences
    lateinit var txtFirstname: TextInputEditText
    lateinit var txtLayoutFirstname: TextInputLayout

    lateinit var txtLastname: TextInputEditText
    lateinit var txtLayoutLastname: TextInputLayout

    lateinit var txtEmail: TextInputEditText
    lateinit var txtLayoutEmail: TextInputLayout

    lateinit var txtPhone: TextInputEditText
    lateinit var txtLayoutPhone: TextInputLayout

    lateinit var txtLocation: TextInputEditText
    lateinit var txtLayoutLocation: TextInputLayout

    lateinit var txtPassword: TextInputEditText
    lateinit var txtLayoutPassword: TextInputLayout

    lateinit var txtConfirmPassword: TextInputEditText
    lateinit var txtLayoutConfirmPassword: TextInputLayout

    lateinit var btnRegister: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        txtEmail = findViewById(R.id.txtEmail)
        txtLayoutEmail = findViewById(R.id.txtLayoutEmail)

        txtFirstname = findViewById(R.id.txtFirstname)
        txtLayoutFirstname = findViewById(R.id.txtLayoutFirstname)

        txtLastname = findViewById(R.id.txtLastname)
        txtLayoutLastname = findViewById(R.id.txtLayoutLastname)

        txtPhone = findViewById(R.id.txtPhone)
        txtLayoutPhone = findViewById(R.id.txtLayoutPhone)

        txtLocation = findViewById(R.id.txtLocation)
        txtLayoutLocation = findViewById(R.id.txtLayoutLocation)

        txtPassword = findViewById(R.id.txtPassword)
        txtLayoutPassword = findViewById(R.id.txtLayoutPassword)

        txtConfirmPassword = findViewById(R.id.txtConfirmPassword)
        txtLayoutConfirmPassword = findViewById(R.id.txtLayoutConfirmPassword)

        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            doRegister()
        }
    }

    private fun doRegister() {
        if (validate()) {
            val apiInterface = RetrofitInstance.api(this)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val user = User()
            user.email = txtEmail.text!!.toString()
            user.firstName = txtFirstname.text!!.toString()
            user.lastName = txtLastname.text!!.toString()
            user.phone = txtPhone.text!!.toString()
            user.location = txtLocation.text!!.toString()
            user.password = txtPassword.text!!.toString()
            user.role = "user"
            user.age = 1

            apiInterface.register(user).enqueue(object : Callback<ResponseBody> {

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val stringResponse = response.body()?.string()
                    val jresponse = JSONObject(stringResponse!!)
                    val userid = jresponse.getString("_id").toString()

                    mSharedPref.edit().apply{
                        putString(ID, userid)
                    }.apply()

                    val status = response.code()
                    Log.w("Status Code", status.toString())


                    if (status == 201) {
                        Toast.makeText(this@RegisterActivity, "Welcome To Takwira ! Thank you for registering", Toast.LENGTH_LONG).show()

                        navigate()
                    } else if (status == 409) {
                        Toast.makeText(this@RegisterActivity, "This Email is Already Registered !", Toast.LENGTH_LONG).show()
                    }

                    //progBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    Toast.makeText(this@RegisterActivity, "Connexion error!", Toast.LENGTH_SHORT).show()

                    //progBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

            })


        }
    }
    private fun navigate(){
        val mainIntent = Intent(this, ProfilePicUpload::class.java)
        startActivity(mainIntent)
    }

    private fun validate(): Boolean {
        txtLayoutEmail.error = null
        txtLayoutPassword.error = null
        txtLayoutFirstname.error = null
        txtLayoutLastname.error = null
        txtLayoutPhone.error = null
        txtLayoutLocation.error = null
        txtLayoutConfirmPassword.error = null

        if (txtEmail.text!!.isEmpty()) {
            txtLayoutEmail.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        if (txtPassword.text!!.isEmpty()) {
            txtLayoutPassword.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        if (txtFirstname.text!!.isEmpty()) {
            txtLayoutFirstname.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        if (txtLastname.text!!.isEmpty()) {
            txtLayoutLastname.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        if (txtLocation.text!!.isEmpty()) {
            txtLayoutLocation.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        if (txtPhone.text!!.toString().isEmpty()) {
            txtLayoutPhone.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        if (txtConfirmPassword.text!!.isEmpty()) {
            txtLayoutConfirmPassword.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        /*if (txtPassword.text!!.equals(txtConfirmPassword.text!!) == false) {
            txtLayoutConfirmPassword.error = getString(R.string.passwordsDontMatch)
            return false
        }*/
        return true
    }


}