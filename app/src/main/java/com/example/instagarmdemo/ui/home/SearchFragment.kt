package com.example.instagarmdemo.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.FragmentSearchBinding
import com.example.instagarmdemo.ui.adapter.SearchAdapter
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.ui.mvvm.SearchViewModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import timber.log.Timber


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchAdapter
    private val viewModel: SearchViewModel by viewModels()
        var userList= ArrayList<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.searchRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        adapter= SearchAdapter(requireContext(),userList)
        binding.searchRecyclerView.adapter=adapter
        setupSearchView()
        loadAllUsers()
        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                val searchText = newText.trim()
                if (searchText.isBlank()) {
                    loadAllUsers()
                } else {
                    searchUsers(searchText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
    }

    private fun searchUsers(query: String) {
        viewModel.searchUsers(query,
            onSuccess = { userList ->
                binding.searchProgressBar.visibility=View.GONE

                    adapter.updateData(userList)
                    adapter.notifyDataSetChanged()
                   setEmptyView()

            },
            onFailure = {
                // Handle failure, if needed
            }
        )
    }

    private fun loadAllUsers() {
        viewModel.loadAllUsers(
            onSuccess = { userList ->
                binding.searchProgressBar.visibility=View.GONE
                adapter.updateData(userList)
            },
            onFailure = {
                // Handle failure, if needed
            }
        )
    }


    private fun setEmptyView() {
        if (adapter.itemCount == 0) {
            binding.tvNoData?.visibility = View.VISIBLE
        } else {
            binding.tvNoData?.visibility = View.GONE

        }
    }
}




/* binding.searchView.setOnSearchClickListener {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                val searchText = newText.trim()
                if (newText.isBlank()) {
                    loadData()
                } else {

                    val queryText = searchText.toLowerCase()

                    Firebase.firestore.collection(Keys.USER_NODE)
                        .get()
                        .addOnSuccessListener { documents ->
                            val tempList = ArrayList<UserModel>()
                            userList.clear()
                            for (document in documents) {
                                val user: UserModel = document.toObject<UserModel>()
                                val lowercaseName = user.name?.toLowerCase() // Convert name to lowercase
                                if (lowercaseName!!.contains(queryText) && user.email != FirebaseAuth.getInstance().currentUser?.email) {
                                    tempList.add(user)
                                }
                            }
                            userList.addAll(tempList)
                            adapter.notifyDataSetChanged()
                        }
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }
        })
    }*/

