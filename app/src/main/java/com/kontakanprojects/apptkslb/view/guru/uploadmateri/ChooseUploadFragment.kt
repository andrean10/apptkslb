package com.kontakanprojects.apptkslb.view.guru.uploadmateri

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.databinding.FragmentChooseUploadBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultIntroSoal
import com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadintrosoal.UploadIntroSoalActivity
import com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadintrosoal.UploadViewModel

class ChooseUploadFragment : Fragment() {

    private lateinit var binding: FragmentChooseUploadBinding
    private val viewModel by viewModels<UploadViewModel>()
    private lateinit var resultIntroSoal: ResultIntroSoal
    private var idChapter = 0
    private var isAvailableIntroSoal = false
    private val TAG = ChooseUploadFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idChapter = ChooseUploadFragmentArgs.fromBundle(arguments as Bundle).idChapter

        with(binding) {
            setToolbarTitle(toolbar, "Pilih Upload")

            progressBar.visibility = View.GONE
            btnUploadIntroSoal.visibility = View.VISIBLE

            btnUploadIntroSoal.setOnClickListener {
                val intent = Intent(requireContext(), UploadIntroSoalActivity::class.java).apply {
                    putExtra(UploadIntroSoalActivity.EXTRA_ID_CHAPTER, idChapter)
                    if (isAvailableIntroSoal) {
                        putExtra(UploadIntroSoalActivity.EXTRA_DATA, resultIntroSoal)
                        putExtra(UploadIntroSoalActivity.EXTRA_IS_EDITINTRO, true)
                    }
                }
                startActivity(intent)
            }

            btnUploadSoal.setOnClickListener {
                val toSoalLevel =
                    ChooseUploadFragmentDirections.actionChooseUploadFragmentToSoalLevelFragment()
                toSoalLevel.idChapter = idChapter
                findNavController().navigate(toSoalLevel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        viewModel.introSoal(idChapter).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    if (response.result != null) {
                        // assign to edit intro soal
                        resultIntroSoal = response.result

                        isAvailableIntroSoal = true

                        // visible upload soal
                        binding.btnUploadSoal.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setToolbarTitle(toolbar: Toolbar, actionBarTitle: String) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = actionBarTitle
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