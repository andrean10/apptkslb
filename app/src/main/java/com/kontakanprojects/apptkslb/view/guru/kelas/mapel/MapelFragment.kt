package com.kontakanprojects.apptkslb.view.guru.kelas.mapel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentMapelBinding
import com.kontakanprojects.apptkslb.model.mapel.ResultsMapel
import com.kontakanprojects.apptkslb.utils.showMessage
import www.sanju.motiontoast.MotionToast

class MapelFragment : Fragment() {

    private val viewModel by viewModels<MapelViewModel>()
    private lateinit var binding: FragmentMapelBinding
    private lateinit var mapelAdapter: MapelAdapter

    private var idKelas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = MapelFragmentArgs.fromBundle(arguments as Bundle)
        idKelas = args.idKelas

        setToolbarTitle()
        setAdapter()
        observe(idKelas)
    }

    private fun observe(idKelas: Int) {
        viewModel.getMapels(idKelas)
        viewModel.mapel.observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    mapelAdapter.setData(result)

                    isDataExist(true)
                } else {
                    isDataExist(false)
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
                    pbLoadingMapel.visibility = View.VISIBLE
                } else {
                    pbLoadingMapel.visibility = View.GONE
                }
            }
        })
    }

    private fun setAdapter() {
        // set adapter
        mapelAdapter = MapelAdapter()
        with(binding.rvMapel) {
            layoutManager = LinearLayoutManager(requireContext())
            val dividerItemDecoration = DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
            setHasFixedSize(true)
            this.adapter = mapelAdapter
        }

        mapelAdapter.setOnItemClickCallBack(object : MapelAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsMapel: ResultsMapel) {
                val toShowListSiswa = MapelFragmentDirections.actionMapelFragmentToSiswaFragment()
                toShowListSiswa.idKelas = idKelas
                toShowListSiswa.idMapel = resultsMapel.idMapel ?: 0
                findNavController().navigate(toShowListSiswa)
            }
        })
    }

    private fun isDataExist(state: Boolean) {
        with(binding) {
            if (state) {
                foundLayout.visibility = View.VISIBLE
                notFoundLayout.visibility = View.GONE
            } else {
                foundLayout.visibility = View.GONE
                notFoundLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Data Mata Pelajaran"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

}