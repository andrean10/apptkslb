package com.kontakanprojects.apptkslb.view.guru.kelas.mapel.chapter.managechapter

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentManageChapterBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultChapter
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.view.guru.chapter.ChapterViewModel
import www.sanju.motiontoast.MotionToast

class ManageChapterFragment : Fragment() {

    private lateinit var binding: FragmentManageChapterBinding
    private val viewModel by viewModels<ChapterViewModel>()

    private var type = 0
    private var idChapter = 0
    private var idMapel = 0
    private lateinit var resultChapter: ResultChapter

    private val TAG = ManageChapterFragment::class.simpleName

    companion object {
        const val REQUEST_ADD = 1
        const val REQUEST_EDIT = 2
        private const val MUST_BE_FILLED = "Nama chapter harus di isi!"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentManageChapterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = ManageChapterFragmentArgs.fromBundle(arguments as Bundle)
        type = args.type
        idMapel = args.idMapel

        lateinit var toolbarTitle: String
        when (type) {
            REQUEST_ADD -> toolbarTitle = "Tambah Chapter"
            REQUEST_EDIT -> {
                toolbarTitle = "Ubah Chapter"
                resultChapter = args.resultChapter!!
                idChapter = resultChapter.idChapter
                prepare()
            }
        }

        with(binding) {
            setToolbarTitle(toolbar, toolbarTitle)
            btnSave.setOnClickListener { save() }
        }
    }

    private fun prepare() {
        binding.edtNamaChapter.setText(resultChapter.namaChapter)
    }

    private fun save() { // handle save
        visibilityProgress(true)

        with(binding) {
            val namaChapter = edtNamaChapter.text.toString().trim()

            if (namaChapter.isEmpty()) {
                tiNamaChapter.error = MUST_BE_FILLED
                return@with
            } else {
                when (type) {
                    REQUEST_ADD -> addChapter(namaChapter, idMapel)
                    REQUEST_EDIT -> editChapter(resultChapter.idChapter, namaChapter, idMapel)
                }
            }
        }
    }

    private fun addChapter(namaChapter: String, idMapel: Int) {
        viewModel.addChapters(namaChapter, idMapel).observe(viewLifecycleOwner, { response ->
            visibilityProgress(false)

            if (response != null) {
                if (response.status == 201) {
                    visibilityProgress(false, "Done")
                    findNavController().navigateUp()

                    showMessage(
                        requireActivity(),
                        getString(R.string.success),
                        response.message,
                        MotionToast.TOAST_SUCCESS
                    )
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        response.message,
                        MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun editChapter(idChapter: Int?, namaChapter: String, idMapel: Int) {
        viewModel.editChapters(idChapter!!, namaChapter, idMapel).observe(viewLifecycleOwner, { response ->
            visibilityProgress(false)
            if (response != null) {
                if (response.status == 200) {
                    visibilityProgress(false, "Done")
                    findNavController().navigateUp()

                    showMessage(
                        requireActivity(),
                        getString(R.string.success),
                        response.message,
                        MotionToast.TOAST_SUCCESS
                    )
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        response.message,
                        MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun deleteChapter(idMapel: Int, idChapter: Int) {
        viewModel.deleteChapters(idMapel, idChapter).observe(viewLifecycleOwner, { response ->
            visibilityProgress(false)
            if (response != null) {
                if (response.status == 200) {
                    visibilityProgress(false, "Done")
                    findNavController().navigateUp()

                    showMessage(
                        requireActivity(),
                        getString(R.string.success),
                        response.message,
                        MotionToast.TOAST_SUCCESS
                    )
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        response.message,
                        MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (type == REQUEST_EDIT) {
            inflater.inflate(R.menu.delete, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.delete -> showAlertDialog(idMapel, idChapter)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog(idMapel: Int, idChapter: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_chapter_title))
            .setMessage(getString(R.string.delete_chapter_body))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                deleteChapter(idMapel, idChapter)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun visibilityProgress(isUpload: Boolean, text: String = getString(R.string.save)) {
        with(binding) {
            if (isUpload) {
                pbSave.visibility = View.VISIBLE
                tvSave.visibility = View.GONE
            } else {
                pbSave.visibility = View.GONE
                tvSave.visibility = View.VISIBLE
                tvSave.text = text
            }
        }
    }

    private fun setToolbarTitle(toolbar: Toolbar, actionBarTitle: String?) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = actionBarTitle
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}