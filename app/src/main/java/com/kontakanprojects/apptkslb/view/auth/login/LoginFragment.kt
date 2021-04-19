package com.kontakanprojects.apptkslb.view.auth.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.databinding.FragmentLoginBinding
import com.kontakanprojects.apptkslb.db.Login
import com.kontakanprojects.apptkslb.db.User
import com.kontakanprojects.apptkslb.session.UserPreference
import com.kontakanprojects.apptkslb.view.auth.AuthViewModel
import com.kontakanprojects.apptkslb.view.auth.ChooseLoginFragment
import com.kontakanprojects.apptkslb.view.guru.GuruActivity
import com.kontakanprojects.apptkslb.view.siswa.SiswaActivity


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<AuthViewModel>()
    private var idRole = 0
    private val loginValid = true

    companion object {
        private const val USERNAME_NOT_NULL = "Username tidak boleh kosong!"
        private const val PASSWORD_NOT_NULL = "Password tidak boleh kosong!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = LoginFragmentArgs.fromBundle(arguments as Bundle)
        idRole = args.role

        with(binding) {
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
                        binding.tiUsername.error = USERNAME_NOT_NULL
                    } else {
                        binding.tiUsername.error = null
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
                        binding.tiPassword.error = MIN_COUNTER_LENGTH_PASS
                    } else if (s.isNullOrEmpty()) {
                        binding.tiPassword.error = PASSWORD_NOT_NULL
                    } else {
                        binding.tiPassword.error = null
                    }
                }
            })

            btnLogin.setOnClickListener {
                val username = edtUsername.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                when {
                    username.isEmpty() -> tiUsername.error = USERNAME_NOT_NULL
                    password.isEmpty() -> tiPassword.error = PASSWORD_NOT_NULL
                    else -> {
                        progressBar.visibility = View.VISIBLE
                        // clear message error
                        tvFailLogin.visibility = View.GONE

                        val params = HashMap<String, Any>()
                        params["username"] = username
                        params["password"] = password
                        params["id_role"] = idRole

                        viewModel.login(params).observe(viewLifecycleOwner, { result ->
                            progressBar.visibility = View.GONE
                            if (result != null) {
                                if (result.status == 200) {
                                    UserPreference(requireContext()).apply {
                                        setUser(
                                            User(
                                                idUser = result.results?.id,
                                                idRole = result.results?.idRole,
                                                namaUser = result.results?.nama
                                            )
                                        )
                                        setLogin(Login(loginValid))
                                    }

                                    when (idRole) {
                                        ChooseLoginFragment.ROLE_GURU -> {
                                            val intent = Intent(
                                                requireContext(),
                                                GuruActivity::class.java
                                            )
                                            startActivity(intent)
                                            activity?.finish()
                                        }
                                        ChooseLoginFragment.ROLE_SISWA -> {
                                            val intent = Intent(
                                                requireContext(),
                                                SiswaActivity::class.java
                                            )
                                            startActivity(intent)
                                            activity?.finish()
                                        }
                                    }
                                } else {
                                    with(binding) {
                                        tvFailLogin.text = result.message
                                        tvFailLogin.visibility = View.VISIBLE
                                    }
                                }
                            }
                        })

                        // hide keyboard
                        hideKeyboard(requireActivity())
                    }
                }
            }

            tvRegister.setOnClickListener {
                val toRegister = LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                    idRole
                )
                findNavController().navigate(toRegister)
            }
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}