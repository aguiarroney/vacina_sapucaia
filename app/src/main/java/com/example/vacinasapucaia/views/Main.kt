package com.example.vacinasapucaia.views

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vacinasapucaia.R
import com.example.vacinasapucaia.databinding.FragmentMainBinding
import com.example.vacinasapucaia.databinding.ScreenItemBinding
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

        _viewModel.inflateMainScreen()

        _viewModel.mainCalendarModel.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val layoutItem: ScreenItemBinding =
                    DataBindingUtil.inflate(inflater, R.layout.screen_item, container, false)

                layoutItem.itemScreen = it

                Picasso.with(context)
                    .load(it.calendarUrl)
                    .into(layoutItem.ivMainImg)

                _binding.llViews.removeAllViews()
                _binding.llViews.addView(layoutItem.root)

            } else {
                _viewModel.getCalendar()
            }
        })

        //todo think of a way to show the snackbar
//        _viewModel.snackBarControll.observe(viewLifecycleOwner, {
//            if (it) {
//                _binding.pgProgressBar.isVisible = false
//                _snackBar.setAction("Ok") {
//                    _viewModel.restoreSnackBarState()
//                }
//                _snackBar.show()
//            }
//        })

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