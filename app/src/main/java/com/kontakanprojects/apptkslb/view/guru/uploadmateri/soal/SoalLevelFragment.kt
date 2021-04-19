package com.kontakanprojects.apptkslb.view.guru.uploadmateri.soal

import android.content.Intent
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
import com.kontakanprojects.apptkslb.databinding.FragmentSoalLevelBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultsSoalByChapter
import com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadsoal.UploadSoalActivity

class SoalLevelFragment : Fragment() {

    private lateinit var binding: FragmentSoalLevelBinding
    private val viewModel by viewModels<SoalLevelViewModel>()
    private lateinit var soalAdapter: SoalLevelAdapter
    private var idChapter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSoalLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idChapter = SoalLevelFragmentArgs.fromBundle(arguments as Bundle).idChapter

        setToolbarTitle()
        setAdapter()
        observe(idChapter)

        // onclick fab
        binding.fabUploadSoal.setOnClickListener {
            val toChooseUploadSoal =
                SoalLevelFragmentDirections.actionSoalLevelFragmentToChooseUploadSoalFragment()
            toChooseUploadSoal.idChapter = idChapter
            findNavController().navigate(toChooseUploadSoal)
        }
    }

    private fun setAdapter() {
        soalAdapter = SoalLevelAdapter()
        with(binding.rvSoalLevel) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            val dividerItemDecoration =
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            addItemDecoration(dividerItemDecoration)
            this.adapter = soalAdapter
        }

        // adapter click
        soalAdapter.setOnItemClickCallBack(object : SoalLevelAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsSoalByChapter: ResultsSoalByChapter) {
                // intent filter video dan gambar
                when {
                    resultsSoalByChapter.video != null -> {
                        moveIntent(UploadSoalActivity.REQUEST_EDIT_VIDEO, resultsSoalByChapter)
                    }
                    resultsSoalByChapter.gambar != null -> {
                        moveIntent(UploadSoalActivity.REQUEST_EDIT_IMAGE, resultsSoalByChapter)
                    }
                }
            }
        })
    }

    private fun moveIntent(type: Int, resultsSoalByChapter: ResultsSoalByChapter) {
        val intent = Intent(requireContext(), UploadSoalActivity::class.java).apply {
            putExtra(UploadSoalActivity.TYPE, type)
            putExtra(UploadSoalActivity.EXTRA_ID_CHAPTER, idChapter)
            putExtra(UploadSoalActivity.EXTRA_DATA, resultsSoalByChapter)
        }
        startActivity(intent)
    }

    private fun observe(idChapter: Int) {
        viewModel.getSoalLevel(idChapter).observe(viewLifecycleOwner, { response ->
            binding.pbLoading.visibility = View.GONE
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    if (result != null) {
                        soalAdapter.setData(result)
                    }

                    isDataAvailable(true)
                } else {
                    isDataAvailable(false, response.message)
                }
            } else {
                isDataAvailable(false)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        observe(idChapter)
    }

    private fun isDataAvailable(
        state: Boolean,
        message: String = "Cek Koneksi Internet Dan Coba Lagi!"
    ) {
        with(binding) {
            if (state) {
                layoutFound.visibility = View.VISIBLE
                notFoundLayout.visibility = View.GONE
            } else {
                layoutFound.visibility = View.GONE
                notFoundLayout.visibility = View.VISIBLE
                messageNotFound.text = message
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
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Level Soal"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}