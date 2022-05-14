package com.esprit.takwira.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esprit.takwira.R
import com.esprit.takwira.api.RetrofitInstance
import com.esprit.takwira.models.User
import com.esprit.takwira.ui.MatchDetails
import com.esprit.takwira.utis.ClickHandler

class UsersAdapter(val activity: MatchDetails, private val clickHandler: ClickHandler): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private var usersList: MutableList<User>? = null


    inner class UsersViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{


        val userPic : ImageView = itemView.findViewById(R.id.imageUser)
        val userName : TextView = itemView.findViewById(R.id.usernom)
        val userPhone :TextView = itemView.findViewById(R.id.userphone)





        fun bind(data: User) {
            userName.text = data.firstName + " " + data.lastName
            userPhone.text = data.phone
            val image0 = data.profilePic
            val image1 = image0?.replace("\\", "/");
            val image = image1?.substringAfter("upload/images/")
            if (image != null) {
                Log.w("image url", image)
            }

            Glide.with(itemView).load("http://" + RetrofitInstance.ip + ":3000/$image")
                .into(userPic)
        }
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                clickHandler.ClickItem(position)
            }
        }



    }
    fun setUsersList(stadeList: MutableList<User>?) {

        this.usersList = stadeList
    }

    fun emptyUsersList() {
        usersList?.clear()
    }
    fun getusersList() : MutableList<User>? {
        return usersList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_users_adapter, parent, false)
        return UsersViewHolder(view)

    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(usersList?.get(position)!!)

    }

    override fun getItemCount(): Int {
        if(usersList == null)return 0
        else return usersList?.size!!
    }

}