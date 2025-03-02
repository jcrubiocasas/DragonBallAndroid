package com.jcrubio.dragonball.Home.ViewController.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.jcrubio.dragonball.Home.Model.Heroe
import com.jcrubio.dragonball.Home.ViewModel.HomeViewModel
import com.jcrubio.dragonball.Home.ViewController.Adapters.FragmentListAdapter
import com.jcrubio.dragonball.Home.ViewController.Adapters.onClickGridItem
import com.jcrubio.dragonball.R
import com.jcrubio.dragonball.databinding.FragmentHeroesListBinding
import kotlinx.coroutines.launch


class HeroesListFragment : Fragment(), onClickGridItem {

    private lateinit var binding: FragmentHeroesListBinding

    val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHeroesListBinding.inflate(inflater)
        setFloatingHealAllHeroesButton()

        val adapter = FragmentListAdapter(homeViewModel.heroesList, this)
        // Numero de columnas
        binding.rvHeroesList.layoutManager = GridLayoutManager(binding.rvHeroesList.context, 2)
        binding.rvHeroesList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.uiState.collect {
                adapter.notifyDataSetChanged()
            }
        }
        return binding.root
    }

    override fun onClick(heroe: Heroe) {
        homeViewModel.selectedHeroToFightClicked(heroe)
    }

    private fun setFloatingHealAllHeroesButton() {
        binding.fabHealAllHeroes.setOnClickListener {
            showHealAllHeroesAlertDialog()
        }
    }

    private fun showHealAllHeroesAlertDialog() {
        val builder = AlertDialog.Builder(binding.fabHealAllHeroes.context)
        val view = layoutInflater.inflate(R.layout.custom_alert_dialog, null)

        builder.setView(view)
        builder.setTitle(R.string.alert_dialog_title)

        builder.setPositiveButton(getString(R.string.alert_dialog_yes)) { dialogInterface, which ->
            alertHealAllHeroesClicked()
        }
        builder.setNegativeButton(getString(R.string.alert_dialog_cancel)) { dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
            ContextCompat.getColor(
                binding.fabHealAllHeroes.context,
                R.color.red_dragon_ball
            )
        )
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
            ContextCompat.getColor(
                binding.fabHealAllHeroes.context,
                R.color.green_dragon_ball
            )
        )
    }

    private fun alertHealAllHeroesClicked() {
        homeViewModel.healAllHeroes()
    }

}