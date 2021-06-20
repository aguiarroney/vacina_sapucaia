package com.example.vacinasapucaia.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vacinasapucaia.R
import com.example.vacinasapucaia.databinding.FragmentMainBinding
import com.example.vacinasapucaia.repository.Repository
import com.squareup.picasso.Picasso

class Main : Fragment() {

    private lateinit var _binding: FragmentMainBinding
    private lateinit var _viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val repository = Repository()
        val factory = MainViewModelFactory(repository)

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main, container, false)
        _viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        _viewModel.getCalendar()

        _viewModel.mainCalendar.observe(viewLifecycleOwner, Observer {
            Picasso.with(context)
                .load(it)
                .into(_binding.ivMainCalendar)
        })

        return _binding.root
    }
}