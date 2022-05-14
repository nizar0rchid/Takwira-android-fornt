package com.esprit.takwira.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esprit.takwira.MainActivity
import com.esprit.takwira.R
import com.esprit.takwira.ui.adapters.StadeAdapter
import com.esprit.takwira.utis.ClickHandler
import com.esprit.takwira.viewmodels.mainActitvityViewModel
import android.app.Activity
import com.esprit.takwira.models.Stade
import com.esprit.takwira.ui.AddMatch
import com.esprit.takwira.ui.MatchDetails
import com.google.android.material.floatingactionbutton.FloatingActionButton


lateinit var adapter: StadeAdapter
lateinit var id_Stade : String
lateinit var recyclerViewStade: RecyclerView
lateinit var addMatchbtn: FloatingActionButton


class HomeFragment : Fragment()  , ClickHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        addMatchbtn = rootView.findViewById(R.id.addMatchbtn)

        addMatchbtn.setOnClickListener {
            navigate()
        }
        recyclerViewStade = rootView.findViewById(R.id.recycle_stade)
        adapter = StadeAdapter(this,this)

        recyclerViewStade.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        recyclerViewStade.adapter = adapter
        initViewModel()
        return  rootView
    }

    private fun navigate() {
        val i = Intent(activity, AddMatch::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(0, 0)

    }

    private fun navigateDetails(stade: Stade) {
        val i = Intent(activity, MatchDetails::class.java)
        i.putExtra("stade object", stade)
        startActivity(i)

    }

    private fun initViewModel() {
        val viewModel: mainActitvityViewModel = ViewModelProvider(this).get(mainActitvityViewModel::class.java)
        viewModel.getLiveDataObserver().observe(viewLifecycleOwner, Observer {
            if(it != null) {
                adapter.setStadeList(it)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "Error in getting list", Toast.LENGTH_SHORT).show()

            }
        })
        viewModel.makeApiCall(context)
    }

    override fun ClickItem(position: Int) {

        var stadium : Stade? = null
        val viewModel: mainActitvityViewModel = ViewModelProvider(this).get(mainActitvityViewModel::class.java)
        viewModel.getLiveDataObserver().observe(viewLifecycleOwner, Observer {
            if(it != null) {
                id_Stade = it[position]._id!!
                stadium = it[position]


            } else {
                Toast.makeText(context, "Error in getting list", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.makeApiCall(context)
        navigateDetails(stadium!!)

        //val action = stadeDirections.actionIcStadeToDetailStade(id_Stade)
        //findNavController().navigate(action)


    }


}