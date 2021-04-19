package com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadsoal.choose

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.databinding.FragmentChooseUploadSoalBinding
import com.kontakanprojects.apptkslb.view.guru.uploadmateri.ChooseUploadFragmentArgs
import com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadsoal.UploadSoalActivity

class ChooseUploadSoalFragment : Fragment() {

    private lateinit var binding: FragmentChooseUploadSoalBinding

    private var idChapter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseUploadSoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()

        idChapter = ChooseUploadFragmentArgs.fromBundle(arguments as Bundle).idChapter

        with(binding) {
            btnUploadSoalVideo.setOnClickListener { uploadVideo() }
            btnUploadSoalGambar.setOnClickListener { uploadGambar() }
        }
    }

    private fun uploadVideo() {
        moveIntent(UploadSoalActivity.REQUEST_ADD_VIDEO)
    }

    private fun uploadGambar() {
        moveIntent(UploadSoalActivity.REQUEST_ADD_IMAGE)
    }

    private fun moveIntent(type: Int) {
        val intent = Intent(requireContext(), UploadSoalActivity::class.java).apply {
            putExtra(UploadSoalActivity.TYPE, type)
            putExtra(UploadSoalActivity.EXTRA_ID_CHAPTER, idChapter)
        }
        startActivity(intent)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Pilih Tipe Upload Soal"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}