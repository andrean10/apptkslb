package com.kontakanprojects.apptkslb.view.guru.kelas.mapel.managemapel

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentManageMapelBinding
import com.kontakanprojects.apptkslb.model.mapel.ResultsMapel
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.view.guru.kelas.mapel.MapelViewModel
import www.sanju.motiontoast.MotionToast

class ManageMapelFragment : Fragment() {

    private val viewModel by viewModels<MapelViewModel>()
    private lateinit var binding: FragmentManageMapelBinding

    private var type = 0
    private var idKelas = 0
    private lateinit var resultMapel: ResultsMapel

    private val TAG = ManageMapelFragment::class.simpleName

    companion object {
        const val REQUEST_ADD = 1
        const val REQUEST_EDIT = 2
        private const val MUST_BE_FILLED = "Nama mata pelajaran harus di isi!"
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
        binding = FragmentManageMapelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = ManageMapelFragmentArgs.fromBundle(arguments as Bundle)
        type = args.type
        idKelas = args.idKelas

        lateinit var toolbarTitle: String
        when (type) {
            REQUEST_ADD -> {
                toolbarTitle = "Tambah Mapel"

            }
            REQUEST_EDIT -> {
                toolbarTitle = "Ubah Mapel"
                resultMapel = args.resultsMapel!!
                idKelas = resultMapel.idKelas!!
                prepare()
            }
        }

        with(binding) {
            setToolbarTitle(toolbar, toolbarTitle)

            // handle save
            btnSave.setOnClickListener { save() }
        }
    }

    private fun prepare() {
        binding.edtNamaMapel.setText(resultMapel.namaMapel)
    }

    private fun save() { // handle save
        visibilityProgress(true)

        with(binding) {
            val namaMapel = edtNamaMapel.text.toString().trim()

            if (namaMapel.isEmpty()) {
                tiNamaMapel.error = MUST_BE_FILLED
                return@with
            } else {
                when (type) {
                    REQUEST_ADD -> addMapel(namaMapel, idKelas)
                    REQUEST_EDIT -> editMapel(resultMapel.idMapel ?: 0, namaMapel, idKelas)
                }
            }
        }
    }

    private fun addMapel(namaMapel: String, idKelas: Int) {
        viewModel.addMapels(namaMapel, idKelas).observe(viewLifecycleOwner, { response ->
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

    private fun editMapel(idMapel: Int, namaMapel: String, idKelas: Int) {
        viewModel.editMapels(idMapel, namaMapel, idKelas).observe(viewLifecycleOwner, { response ->
            visibilityProgress(false)
            if (response != null) {
                if (response.status == 200) {
                    visibilityProgress(false, "Done")
                    findNavController().navigateUp()

                    showMessage(requireActivity(), getString(R.string.success), response.message,
                        MotionToast.TOAST_SUCCESS)
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR)
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun deleteMapel(idMapel: Int, idKelas: Int) {
        viewModel.deleteMapels(idMapel, idKelas).observe(viewLifecycleOwner, { response ->
            visibilityProgress(false)
            if (response != null) {
                if (response.status == 200) {
                    visibilityProgress(false, "Done")
                    findNavController().navigateUp()

                    showMessage(requireActivity(), getString(R.string.success), response.message,
                        MotionToast.TOAST_SUCCESS)
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR)
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
            R.id.delete -> showAlertDialog(resultMapel.idMapel ?: 0, idKelas)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog(idMapel: Int, idKelas: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_mapel_title))
            .setMessage(getString(R.string.delete_mapel_body))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                deleteMapel(idMapel, idKelas)
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