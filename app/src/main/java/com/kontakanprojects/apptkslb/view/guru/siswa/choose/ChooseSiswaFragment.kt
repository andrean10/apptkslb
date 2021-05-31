package com.kontakanprojects.apptkslb.view.guru.siswa.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.databinding.FragmentChooseSiswaBinding

class ChooseSiswaFragment : Fragment() {

    private lateinit var binding: FragmentChooseSiswaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseSiswaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle()

        val args = ChooseSiswaFragmentArgs.fromBundle(arguments as Bundle)
        val idKelasArgs = args.idKelas
        val idMapelArgs = args.idMapel

        with(binding) {
            btnLihatSemua.setOnClickListener {
                val toKelas =
                    ChooseSiswaFragmentDirections.actionChooseSiswaFragmentToKelasFragment()
                findNavController().navigate(toKelas)
            }

            btnLihatSiswaPerMapel.setOnClickListener {
                val toShowMySiswa =
                    ChooseSiswaFragmentDirections.actionChooseSiswaFragmentToSiswaFragment().apply {
                        idKelas = idKelasArgs
                        idMapel = idMapelArgs
                    }
                findNavController().navigate(toShowMySiswa)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Lihat Siswa"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}