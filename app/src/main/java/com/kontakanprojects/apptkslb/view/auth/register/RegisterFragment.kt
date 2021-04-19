package com.kontakanprojects.apptkslb.view.auth.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.databinding.FragmentRegisterBinding
import com.kontakanprojects.apptkslb.model.kelas.ResultsKelas
import com.kontakanprojects.apptkslb.model.mapel.ResultsMapel
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.view.auth.AuthViewModel
import com.kontakanprojects.apptkslb.view.auth.login.LoginFragmentArgs
import www.sanju.motiontoast.MotionToast

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<AuthViewModel>()

    private var listMapel: List<ResultsMapel>? = null
    private val filteredMapel = ArrayList<ResultsMapel>()

    private var idKelasFromSpinner = 0
    private var idMapelFromSpinner = 0

    private val TAG = RegisterFragment::class.simpleName

    companion object {
        private const val FULLNAME_NOT_NULL = "Nama lengkap tidak boleh kosong!"
        private const val USERNAME_NOT_NULL = "Username tidak boleh kosong!"
        private const val PASSWORD_NOT_NULL = "Password tidak boleh kosong!"
        private const val RETYPE_PASSWORD_NOT_NULL = "Ulangi password tidak boleh kosong!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
        private const val PASSWORD_NOT_MATCH = "Pengulangan password tidak sesuai"
        private const val MUST_INPUT_KELAS = "Harus memilih kelas"
        private const val MUST_INPUT_MAPEL = "Harus memilih mata pelajaran"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = LoginFragmentArgs.fromBundle(arguments as Bundle)
        val idRole = args.role

        with(binding) {
            if (idRole == 1) {
                // get data in view model
                observeKelas()
                observeMapel()
                spinnerMapel.visibility = View.VISIBLE
            } else if (idRole == 2) {
                observeKelas()
            }

            edtFullname.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! == 0) {
                        tiFullname.error = FULLNAME_NOT_NULL
                    } else {
                        tiFullname.error = null
                    }
                }
            })

            edtUsername.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! == 0) {
                        tiUsername.error = USERNAME_NOT_NULL
                    } else {
                        tiUsername.error = null
                    }
                }
            })

            edtPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! < 5) {
                        tiPassword.error = MIN_COUNTER_LENGTH_PASS
                    } else if (s.isNullOrEmpty()) {
                        tiPassword.error = PASSWORD_NOT_NULL
                    } else {
                        tiPassword.error = null
                    }
                }
            })

            edtRetypePassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! < 5) {
                        tiRetypePassword.error = MIN_COUNTER_LENGTH_PASS
                    } else if (s.isNullOrEmpty()) {
                        tiRetypePassword.error = RETYPE_PASSWORD_NOT_NULL
                    } else {
                        tiRetypePassword.error = null
                    }
                }
            })

            btnRegister.setOnClickListener {
                val fullName = edtFullname.text.toString().trim()
                val username = edtUsername.text.toString().trim()
                val password = edtPassword.text.toString().trim()
                val retypePassword = edtRetypePassword.text.toString().trim()

                when {
                    fullName.isEmpty() -> {
                        tiFullname.error = FULLNAME_NOT_NULL
                        return@setOnClickListener
                    }
                    username.isEmpty() -> {
                        tiUsername.error = USERNAME_NOT_NULL
                        return@setOnClickListener
                    }
                    password.isEmpty() -> {
                        tiPassword.error = PASSWORD_NOT_NULL
                        return@setOnClickListener
                    }
                    retypePassword.isEmpty() -> {
                        tiRetypePassword.error = RETYPE_PASSWORD_NOT_NULL
                        return@setOnClickListener
                    }
                    password != retypePassword -> {
                        tiRetypePassword.error = PASSWORD_NOT_MATCH
                        return@setOnClickListener
                    }
                    idKelasFromSpinner == 0 -> {
                        spinnerKelas.errorText = MUST_INPUT_KELAS
                        return@setOnClickListener
                    }
                    idMapelFromSpinner == 0 && idRole == 1 -> {
                        spinnerMapel.errorText = MUST_INPUT_MAPEL
                        return@setOnClickListener
                    }
                    else -> {
                        val params = HashMap<String, Any>()
                        params["nama"] = fullName
                        params["username"] = username
                        params["password"] = password
                        params["id_role"] = idRole

                        when (idRole) {
                            1 -> params["id_mapel"] = idMapelFromSpinner
                            2 -> params["id_kelas"] = idKelasFromSpinner
                        }

                        progressBar.visibility = View.VISIBLE
                        register(params)
                    }
                }
            }

            tvLogin.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun observeKelas() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            viewModel.kelas().observe(viewLifecycleOwner, { response ->
                progressBar.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        setSpinnerKelas(response.results)
                    } else {
                        showMessage(
                            requireActivity(),
                            "Gagal",
                            response.message,
                            MotionToast.TOAST_ERROR
                        )
                    }
                } else {
                    showMessage(requireActivity(), "Gagal", style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun observeMapel() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            viewModel.mapel().observe(viewLifecycleOwner, { response ->
                progressBar.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        listMapel = response.results // set ke listmapel
                    } else {
                        showMessage(
                            requireActivity(),
                            "Gagal",
                            response.message,
                            MotionToast.TOAST_ERROR
                        )
                    }
                } else {
                    showMessage(requireActivity(), "Gagal", style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun register(params: HashMap<String, Any>) {
        viewModel.register(params).observe(viewLifecycleOwner, { response ->
            binding.progressBar.visibility = View.GONE
            if (response != null) {
                if (response.status == 201) {
                    showMessage(
                        requireActivity(),
                        "Sukses",
                        response.message,
                        MotionToast.TOAST_SUCCESS
                    )

                    findNavController().navigateUp()
                } else {
                    showMessage(
                        requireActivity(),
                        "Gagal",
                        response.message,
                        MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(requireActivity(), "Gagal", style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun setSpinnerKelas(itemKelas: List<ResultsKelas>?) {
        with(binding) {
            spinnerKelas.item = itemKelas
            spinnerKelas.onItemSelectedListener = itemSelectedKelas
        }
    }

    // listener selection kelas
    private val itemSelectedKelas = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val itemKelas = parent?.selectedItem as ResultsKelas // casting to resultKelas

            idKelasFromSpinner = itemKelas.idKelas ?: 0

            // filter list mapel
            if (listMapel != null) {
                filteredMapel.clear() // clearkan data

                listMapel!!.forEach { mapel ->
                    if (mapel.idKelas == itemKelas.idKelas) {
                        filteredMapel.add(mapel)
                    }
                }

                setSpinnerMapel(filteredMapel)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

    }

    private fun setSpinnerMapel(itemMapel: List<ResultsMapel>) {
        with(binding) {
            if (itemMapel.isNotEmpty()) {
                spinnerMapel.item = itemMapel
                spinnerMapel.onItemSelectedListener = itemSelectedMapel
            }
        }
    }

    // listener selection mapel
    private val itemSelectedMapel = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val itemMapel = parent?.selectedItem as ResultsMapel // casting to resultKelas

            idMapelFromSpinner = itemMapel.idMapel ?: 0

            Log.d(TAG, "onItemSelected: $idMapelFromSpinner")
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

    }
}