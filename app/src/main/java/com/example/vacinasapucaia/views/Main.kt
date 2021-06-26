package com.example.vacinasapucaia.views

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vacinasapucaia.R
import com.example.vacinasapucaia.databinding.FragmentMainBinding
import com.example.vacinasapucaia.repository.Repository
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class Main : Fragment() {

    private lateinit var _binding: FragmentMainBinding
    private lateinit var _viewModel: MainViewModel
    private lateinit var _snackBar: Snackbar

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main, container, false)
        _viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        _snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            "Problemas de conexão com a internet",
            Snackbar.LENGTH_INDEFINITE
        )

        _viewModel.getCalendar()

        _viewModel.mainCalendar.observe(viewLifecycleOwner, Observer {
            _binding.cvMainCalendar.isVisible = true
            _binding.pgProgressBar.isVisible = false
            Picasso.with(context)
                .load(it)
                .into(_binding.ivMainCalendar)
            _viewModel.readFromDataStore(it)
            _viewModel.saveToDataStore(it)
        })

        _viewModel.snackBarControll.observe(viewLifecycleOwner, {
            if (it) {
                _binding.pgProgressBar.isVisible = false
                _snackBar.setAction("Ok") {
                    _viewModel.restoreSnackBarState()
                }
                _snackBar.show()
            }
        })


        setHasOptionsMenu(true)
        return _binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                Log.i("refresh", "refresh")
                _binding.pgProgressBar.isVisible = true
                _viewModel.getCalendar()
            }
        }
        return true
    }
}