package com.kontakanprojects.apptkslb.view.guru.siswa

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
import com.kontakanprojects.apptkslb.databinding.FragmentSiswaBinding
import com.kontakanprojects.apptkslb.db.Siswa
import com.kontakanprojects.apptkslb.model.guru.ResultsSiswa
import com.kontakanprojects.apptkslb.utils.showMessage
import www.sanju.motiontoast.MotionToast

class SiswaFragment : Fragment() {

    private lateinit var binding: FragmentSiswaBinding
    private val viewModel by viewModels<SiswaViewModel>()
    private lateinit var siswaAdapter: SiswaAdapter

    private var idKelas = 0
    private var idMapel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSiswaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = SiswaFragmentArgs.fromBundle(arguments as Bundle)
        val request = args.isShowAllSiswa
        idKelas = args.idKelas
        idMapel = args.idMapel

        setToolbarTitle()

        with(binding) {
            // set adapter
            siswaAdapter = SiswaAdapter()
            with(rvSiswa) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                val dividerItemDecoration = DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
                addItemDecoration(dividerItemDecoration)
                this.adapter = siswaAdapter
            }

            if (request) {
                observeSiswa()
            } else {
                observeMySiswa(idKelas, idMapel)
            }

            siswaAdapter.setOnItemClickCallBack(object : SiswaAdapter.OnItemClickCallBack {
                override fun onItemClicked(resultsSiswa: ResultsSiswa) {
                    // send to db parcel
                    val dataSiswa = Siswa(
                        resultsSiswa.id!!,
                        resultsSiswa.nama,
                        resultsSiswa.namaKelas,
                        resultsSiswa.fotoProfile,
                        idMapel
                    )

                    val toDetailSiswa =
                        SiswaFragmentDirections.actionSiswaFragmentToDetailSiswaFragment(dataSiswa)
                    findNavController().navigate(toDetailSiswa)
                }
            })
        }
    }

    private fun observeSiswa() {
        with(binding) {
            viewModel.getSiswa().observe(viewLifecycleOwner, { response ->
                progressBar.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        siswaAdapter.setData(result)
                    } else {
                        dataSiswaNotFound()
                    }
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun observeMySiswa(idKelas: Int, idMapel: Int) {
        with(binding) {
            viewModel.getMySiswa(idKelas, idMapel).observe(viewLifecycleOwner, { response ->
                progressBar.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        siswaAdapter.setData(result)
                    } else {
                        dataSiswaNotFound()
                    }
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dataSiswaNotFound() {
        with(binding) {
            foundLayout.visibility = View.GONE
            notFoundLayout.visibility = View.VISIBLE
        }
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Data Siswa"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}