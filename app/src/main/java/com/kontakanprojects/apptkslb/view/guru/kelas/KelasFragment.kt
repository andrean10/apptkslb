package com.kontakanprojects.apptkslb.view.guru.kelas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentKelasBinding
import com.kontakanprojects.apptkslb.model.kelas.ResultsKelas
import com.kontakanprojects.apptkslb.utils.showMessage
import www.sanju.motiontoast.MotionToast

class KelasFragment : Fragment() {

    private val viewModel by viewModels<KelasViewModel>()
    private lateinit var binding: FragmentKelasBinding
    private lateinit var kelasAdapter: KelasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKelasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()
        setAdapter()
        observe()
    }

    private fun setAdapter() {
        // set adapter
        kelasAdapter = KelasAdapter()
        with(binding.rvKelas) {
            layoutManager = LinearLayoutManager(requireContext())
            val dividerItemDecoration = DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
            setHasFixedSize(true)
            this.adapter = kelasAdapter
        }

        kelasAdapter.setOnItemClickCallBack(object : KelasAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsKelas: ResultsKelas) {
                val isFromSiswa = KelasFragmentArgs.fromBundle(arguments as Bundle).isFromSiswa

                val toMapel = KelasFragmentDirections.actionKelasFragmentToMapelFragment()
                toMapel.idKelas = resultsKelas.idKelas ?: 0
                toMapel.isFromSiswa = isFromSiswa
                findNavController().navigate(toMapel)
            }
        })
    }

    private fun observe() {
        viewModel.getKelas().observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    kelasAdapter.setData(result)
                } else {
                    dataNotFound()
                }
            } else {
                showMessage(
                    requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR
                )
            }
        })

        viewModel.isLoading().observe(viewLifecycleOwner, { state ->
            with(binding) {
                if (state) {
                    pbLoadingKelas.visibility = View.VISIBLE
                } else {
                    pbLoadingKelas.visibility = View.GONE
                }
            }
        })
    }

    private fun dataNotFound() {
        with(binding) {
            foundLayout.visibility = View.GONE
            notFoundLayout.visibility = View.VISIBLE
        }
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Data Kelas"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

}