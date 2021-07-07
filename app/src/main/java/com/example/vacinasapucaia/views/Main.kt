package com.example.vacinasapucaia.views

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vacinasapucaia.R
import com.example.vacinasapucaia.databinding.FragmentMainBinding
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

        _viewModel.inflateMainCalendar()

        _viewModel.refreshTime.observe(viewLifecycleOwner, {
            _binding.tvDate.text =
                getString(R.string.refresh_label, _viewModel.refreshTime.value)
        })

        _viewModel.mainCalendar.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                _binding.cvMainCalendar.isVisible = true
                _binding.ivMainCalendar.isVisible = true
                _binding.pgProgressBar.isVisible = false
                _binding.tvDate.isVisible = true

                Picasso.with(context)
                    .load(it)
                    .into(_binding.ivMainCalendar)

            } else {
                _viewModel.getCalendar()
            }
        })

        _viewModel.mainBoletim.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                Picasso.with(context)
                    .load(it)
                    .into(_binding.ivMainBoletim)
            }
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

        createChannel(getString(R.string.notification_channel_id), getString(R.string.app_name))
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
                _binding.pgProgressBar.isVisible = true
                _binding.ivMainCalendar.isVisible = false
                _binding.tvDate.isVisible = false
                _viewModel.getCalendar()
            }
        }
        return true
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Alteração no calendario"

            val notificationManager =
                requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}