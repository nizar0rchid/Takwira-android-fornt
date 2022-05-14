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
import com.esprit.takwira.api.RetrofitInstance.ip
import com.esprit.takwira.models.Stade
import com.esprit.takwira.utis.ClickHandler

class StadeAdapter(val activity: Fragment,private  val clickHandler: ClickHandler):RecyclerView.Adapter<StadeAdapter.StadeViewHolder>() {

    private var stadeList: List<Stade>? = null


    inner class StadeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{
        val stadePic : ImageView = itemView.findViewById(R.id.imageStade)
        val stadeName : TextView = itemView.findViewById(R.id.nomDeStade)
        val stadeNum :TextView = itemView.findViewById(R.id.numDuSatde)


        fun bind(data: Stade) {

            stadeName.text = data.name
            stadeNum.text = data.DateTime
            val image0 = data.image
            val image1 = image0?.replace("\\", "/");
            val image = image1?.substringAfter("upload/images/")
            if (image != null) {
                Log.w("image url", image)
            }

            Glide.with(itemView).load("http://"+ip+":3000/$image").into(stadePic)

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
    fun setStadeList(stadeList: List<Stade>?) {
        this.stadeList = stadeList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StadeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_stade, parent, false)
        return StadeViewHolder(view)

    }

    override fun onBindViewHolder(holder: StadeViewHolder, position: Int) {
        holder.bind(stadeList?.get(position)!!)

    }

    override fun getItemCount(): Int {
        if(stadeList == null)return 0
        else return stadeList?.size!!
    }

}