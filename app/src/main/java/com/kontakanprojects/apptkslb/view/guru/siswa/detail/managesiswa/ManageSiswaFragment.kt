package com.kontakanprojects.apptkslb.view.guru.siswa.detail.managesiswa

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentManageSiswaBinding
import com.kontakanprojects.apptkslb.model.kelas.ResultsKelas
import com.kontakanprojects.apptkslb.model.siswa.ResultDetailSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.utils.showMessage
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ManageSiswaFragment : Fragment(), View.OnClickListener {

    private val viewModel by viewModels<ManageSiswaViewModel>()
    private lateinit var binding: FragmentManageSiswaBinding

    private lateinit var resultDetailSiswa: ResultDetailSiswa
    private var listKelas: List<ResultsKelas>? = null

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View

    private lateinit var tvUiUpdated: TextView
    private var imageFile: File? = null
    private var imageUri: Uri? = null
    private var currentImagePath: String? = null

    private var idSiswa = 0
    private var idKelas = 0
    private var idKelasFromSpinner = 0

    private lateinit var spinnerKelas: SmartMaterialSpinner<*>

    private val TAG = ManageSiswaFragment::class.simpleName

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1
        private const val REQUEST_CODE_CAPTURE_IMAGE = 2
        private const val REQUEST_CODE_SELECT_IMAGE = 3
        private const val OLD_PASSWORD_IS_REQUIRED = "Password Lama Harus Di Isi"
        private const val NEW_PASSWORD_IS_REQUIRED = "Password Baru Harus Di Isi"
        private const val NEWAGAIN_PASSWORD_IS_REQUIRED = "Password Baru Harus Di Isi Ulang"
        private const val WRONG_OLD_PASSWORD = "Password Lama Tidak Sesuai!"
        private const val WRONG_NEW_PASSWORD_AGAIN = "Password Baru Tidak Sesuai!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
        private const val FIELD_MUST_BE_FILLED = "Field Ini Harus Di Isi"
        private const val KELAS_MUST_BE_FILLED = "Pilihan Kelas Harus Di Isi"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageSiswaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()

        val args = ManageSiswaFragmentArgs.fromBundle(arguments as Bundle)
        idSiswa = args.resultsDetailSiswa.id ?: 0
        idKelas = args.resultsDetailSiswa.idKelas ?: 0
        resultDetailSiswa = args.resultsDetailSiswa

        observeMapel()
        prepareSiswa(resultDetailSiswa)

        with(binding) {
            fabUploadImage.setOnClickListener(this@ManageSiswaFragment)
            layoutFullName.setOnClickListener(this@ManageSiswaFragment)
            layoutPassword.setOnClickListener(this@ManageSiswaFragment)
            layoutKelas.setOnClickListener(this@ManageSiswaFragment)
        }
    }

    private fun prepareSiswa(result: ResultDetailSiswa) {
        with(binding) {
            // set images
            Glide.with(requireContext())
                .load(ApiConfig.URL + result.fotoProfile)
                .fallback(R.drawable.no_profile_images)
                .error(R.drawable.no_profile_images)
                .circleCrop()
                .into(imgSiswa)

            tvNamaSiswa.text = result.nama
            tvUsernameSiswa.text = result.username
            tvPasswordSiswa.text = result.password
            tvKelasSiswa.text = result.namaKelas
        }
    }

    private fun observeMapel() {
        viewModel.mapel().observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    listKelas = result
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
            }
        })
    }

    override fun onClick(v: View?) {
        with(binding) {
            when (v?.id) {
                R.id.fabUploadImage -> {
                    permissionUploadImages()
                    showBottomSheet(isEditProfile = false)
                }
                R.id.layoutFullName -> {
                    val itemProfile = tvNamaSiswa.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleName)
                    tvUiUpdated = tvNamaSiswa
                    showBottomSheet(itemProfile, titleSheet, "nama", true)
                }
                R.id.layoutPassword -> {
                    val itemProfile = tvPasswordSiswa.text.toString()
                    tvUiUpdated = tvPasswordSiswa
                    showBottomSheet(
                        itemProfile, param = "password", isEditProfile = true,
                        isEditPassword = true
                    )
                }
                R.id.layoutKelas -> {
                    tvUiUpdated = tvKelasSiswa
                    showBottomSheet(
                        idKelas.toString(), param = "id_kelas", isEditProfile = true,
                        isEditKelas = true
                    )
                }
            }
        }
    }

    private fun showBottomSheet(
        itemProfile: String? = null, titleSheet: String? = null,
        param: String? = null, isEditProfile: Boolean, isEditKelas: Boolean = false,
        isEditPassword: Boolean = false
    ) {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

        if (isEditProfile) {
             if (isEditPassword) { // change password in profile
                bottomSheetView = LayoutInflater.from(requireContext()).inflate(
                    R.layout.bottom_sheet_editprofilepass,
                    activity?.findViewById(R.id.bottomSheetEditProfilePass)
                )
            } else { // change all data profile but no password
                 bottomSheetView = LayoutInflater.from(requireContext()).inflate(
                    R.layout.bottom_sheet_editprofile,
                    activity?.findViewById(R.id.bottomSheetEditProfile)
                )

                // kondisi ketika edit kelas
                if (isEditKelas) {
                    // visible and gone layout
                    bottomSheetView.findViewById<RelativeLayout>(R.id.layoutEditKelas).visibility = View.VISIBLE
                    bottomSheetView.findViewById<TextView>(R.id.titleInputMapel).visibility = View.GONE
                    bottomSheetView.findViewById<SmartMaterialSpinner<*>>(R.id.spinnerMapel).visibility = View.GONE

                    // set list kelas to spinner
                    setSpinnerKelas(listKelas)
                } else {
                    bottomSheetView.findViewById<RelativeLayout>(R.id.layoutEditProfile).visibility = View.VISIBLE
                }
            }
        } else { // ubah foto profile
            bottomSheetView = LayoutInflater.from(requireContext()).inflate(
                R.layout.bottom_sheet_pick,
                activity?.findViewById(R.id.bottomSheetContainer)
            )
        }

        if (isEditProfile) {
            // ubah head bottom sheet selain password
            // set data ke bottomsheet
            var edtInput: TextInputLayout? = null
            if (!isEditPassword) {
                val tvTitle: TextView = bottomSheetView.findViewById(R.id.titleInput)
                edtInput = bottomSheetView.findViewById(R.id.tiEditProfile)
                tvTitle.text = titleSheet
                edtInput.editText?.setText(itemProfile)
            } else {
                val edtOldPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiOldPassword)
                val edtNewPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPassword)
                val edtNewPasswordAgain: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPasswordAgain)

                edtOldPassword.editText?.addTextChangedListener { s ->
                    edtOldPassword.error = if (s?.length!! < 5) {
                        MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }

                edtNewPassword.editText?.addTextChangedListener { s ->
                    edtNewPassword.error = if (s?.length!! < 5) {
                        MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }

                edtNewPasswordAgain.editText?.addTextChangedListener { s ->
                    edtNewPasswordAgain.error = if (s?.length!! < 5) {
                        MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }
            }

            save(edtInput, itemProfile, param, isEditKelas, isEditPassword)

            // cancel bottomsheet
            val btnCancel: Button = bottomSheetView.findViewById(R.id.btnCancel)
            btnCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        } else {
            val chooseImage: ImageView = bottomSheetView.findViewById(R.id.imgCamera)
            chooseImage.setOnClickListener {
                dispatchCaptureImageIntent()
                bottomSheetDialog.dismiss()
            }

            val chooseGaleri: ImageView = bottomSheetView.findViewById(R.id.imgGaleri)
            chooseGaleri.setOnClickListener {
                selectImageIntent()
                bottomSheetDialog.dismiss()
            }

            // cek jika ada gambar visibilitas delete di bottom sheet munculkan
            if (isHasImage()) {
                // delete
                bottomSheetView.findViewById<ImageView>(R.id.imgDelete).visibility = View.VISIBLE

                bottomSheetView.findViewById<ImageView>(R.id.imgDelete).setOnClickListener {
                    // hapus data ke server
                    changeFotoProfile()

                    // hapus value camera, pickimages and result.gambar yang ada di model
                    currentImagePath = null
                }
            }
        }

        // set ke view
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun setSpinnerKelas(itemKelas: List<ResultsKelas>?) {
        if (itemKelas != null) {
            spinnerKelas = bottomSheetView.findViewById(R.id.spinnerKelas)
            spinnerKelas.item = itemKelas

            val idSelectedKelas = listKelas!!.indexOf(
                ResultsKelas(
                    idKelas = resultDetailSiswa.idKelas,
                    namaKelas = resultDetailSiswa.namaKelas
                )
            )
            spinnerKelas.setSelection(idSelectedKelas)
            spinnerKelas.onItemSelectedListener = itemSelectedKelas
        }
    }

    // listener selection kelas
    private val itemSelectedKelas = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val itemKelas = parent?.selectedItem as ResultsKelas // casting to resultKelas

            idKelasFromSpinner = itemKelas.idKelas ?: 0
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    private fun save(edtInput: TextInputLayout?, itemProfile: String?, param: String?,
                     isEditKelas: Boolean = false, isEditPassword: Boolean = false) {
        // save data
        val btnSave: Button = bottomSheetView.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            val parameters = HashMap<String, String>()
            var getInput = ""

            if (isEditPassword) { // cek jika edit password arahkan logic kesini
                val edtOldPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiOldPassword)
                val edtNewPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPassword)
                val edtNewPasswordAgain: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPasswordAgain)

                val oldPassword = edtOldPassword.editText?.text.toString().trim()
                val newPassword = edtNewPassword.editText?.text.toString().trim()
                val newPasswordAgain = edtNewPasswordAgain.editText?.text.toString().trim()

                // cek kondisi field pada password
                when {
                    oldPassword.isEmpty() -> {
                        edtOldPassword.error = OLD_PASSWORD_IS_REQUIRED
                        return@setOnClickListener
                    }
                    newPassword.isEmpty() -> {
                        edtNewPassword.error = NEW_PASSWORD_IS_REQUIRED
                        return@setOnClickListener
                    }
                    newPasswordAgain.isEmpty() -> {
                        edtNewPasswordAgain.error =
                            NEWAGAIN_PASSWORD_IS_REQUIRED
                        return@setOnClickListener
                    }
                    else -> {
                        // kirim data password baru ke viewmodel
                        // apakah password lama sesuai dengan password di textview jika iya teruskan
                        if (oldPassword == itemProfile) {
                            if (newPassword == newPasswordAgain) { // jika password match
                                // get input password
                                getInput = newPassword
                            } else {
                                edtNewPasswordAgain.error = WRONG_NEW_PASSWORD_AGAIN
                                return@setOnClickListener
                            }
                        } else { // password lama tidak cocok
                            edtOldPassword.error = WRONG_OLD_PASSWORD
                            return@setOnClickListener
                        }
                    }
                }
            } else { // ambil inputan jika bukan edit password
                if (isEditKelas) {
                    // check kondisi field pada edit profile dan kelas
                    when (idKelasFromSpinner) {
                        0 -> {
                            spinnerKelas.errorText = KELAS_MUST_BE_FILLED
                            return@setOnClickListener
                        }
                        else -> getInput = idKelasFromSpinner.toString()
                    }
                } else {
                    val inputData = edtInput?.editText?.text.toString().trim()

                    when {
                        inputData.isEmpty() -> {
                            edtInput?.error = FIELD_MUST_BE_FILLED
                        }
                        else -> {
                            getInput = inputData
                        }
                    }
                }
            }

            // set ke partmap
            parameters[param!!] = getInput

            // save dan observe (hide btn save)
            loadingInBottomSheet(true)
            editProfile(getInput, parameters, isEditKelas)
        }
    }

    private fun isHasImage(): Boolean {
        return !resultDetailSiswa.fotoProfile.isNullOrEmpty() || !currentImagePath.isNullOrEmpty()
    }

    // logic menampilkan kamera
    private fun dispatchCaptureImageIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity?.packageManager!!) != null) {
            try {
                imageFile = createImageFile()
            } catch (e: IOException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

            if (imageFile != null) {
                imageUri = FileProvider.getUriForFile(
                    requireActivity().applicationContext,
                    "com.kontakanprojects.apptkslb.fileprovider",
                    imageFile!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
            }
        }
        intent.resolveActivity(activity?.packageManager!!)
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
    }

    private fun selectImageIntent() {
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        if (intent.resolveActivity(activity?.packageManager!!) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    @Throws(Exception::class)
    private fun createImageFile(): File? {
        val fileName =
            "IMAGE_" + SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(Date())
        val directory = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(fileName, ".jpg", directory)
        currentImagePath = imageFile.absolutePath
        return imageFile
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        val filePath: String?
        val cursor = activity?.contentResolver?.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_CAPTURE_IMAGE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    try {
                        imageUri?.let { startCrop(it) }
                    } catch (e: Exception) {
                        Log.e(TAG, "onActivityResult: ${e.message}")
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                    // jadikan value galeri null
                    currentImagePath = null
//                    selectedImageFile = null
                }
            }
            REQUEST_CODE_SELECT_IMAGE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    data?.data?.let {
                        startCrop(it)
                    }
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    currentImagePath = getPathFromUri(UCrop.getOutput(data!!)!!)

                    val result = data.let { UCrop.getOutput(it) }

                    // ubah data ke server
                    changeFotoProfile(result)
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    val cropError = UCrop.getError(data!!)
                    Toast.makeText(requireContext(), cropError?.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "onActivityResult: ${cropError?.message}")
                }
            }
        }
    }

    private fun editProfile(
        newInputData: String? = null,
        newData: HashMap<String, String>? = null,
        isEditKelas: Boolean = false
    ) {
        viewModel.changeDetailProfile(idSiswa, newData)
            .observe(viewLifecycleOwner, { response ->
                if (response != null) {
                    if (response.status == 200) {
                        tvUiUpdated.text = if (isEditKelas) {
                            getString(R.string.kelas, newInputData)
                        } else {
                            newInputData
                        }

                        bottomSheetDialog.dismiss()
                    } else {
                        showMessage(
                            requireActivity(),
                            "Gagal",
                            response.message,
                            MotionToast.TOAST_ERROR
                        )

                        bottomSheetView.findViewById<ProgressBar>(R.id.progressBar).visibility =
                            View.GONE
                        bottomSheetView.findViewById<Button>(R.id.btnSave).visibility =
                            View.VISIBLE
                    }
                } else {
                    showMessage(requireActivity(), "Gagal", style = MotionToast.TOAST_ERROR)
                }
            })
    }

    private fun changeFotoProfile(result: Uri? = null) {
        with(binding) {
            pbLoadingPicture.visibility = View.VISIBLE
            if (result != null) {
                val selectedImageFile = File(getPathFromUri(result)!!)
                val reqFile =
                    selectedImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "foto_profile", selectedImageFile.name, reqFile
                )

                viewModel.changeFotoProfile(idSiswa, body).observe(viewLifecycleOwner, { response ->
                    pbLoadingPicture.visibility = View.GONE
                    if (response != null) {
                        if (response.status == 200) {
//                        observeDetailSiswa()
                            bottomSheetDialog.dismiss()

                            Glide.with(requireContext())
                                .load(result)
                                .into(imgSiswa)
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
            } else {
                val reqFile = ""
                    .toRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part
                    .createFormData("foto_profile", "", reqFile)

                viewModel.changeFotoProfile(idSiswa, body).observe(viewLifecycleOwner, { response ->
                    pbLoadingPicture.visibility = View.GONE
                    if (response != null) {
                        if (response.status == 200) {
                            binding.imgSiswa.setImageResource(R.drawable.no_profile_images)
                            bottomSheetDialog.dismiss()

                            resultDetailSiswa.fotoProfile = null
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
    }

    private fun startCrop(uri: Uri) {
        val uCrop = UCrop.of(uri, Uri.fromFile(createImageFile()))
        uCrop.withAspectRatio(1F, 1F)
        uCrop.withMaxResultSize(640, 640)
        uCrop.withOptions(getCropOptions())
        uCrop.start(requireActivity(), this)
    }

    private fun getCropOptions(): UCrop.Options {
        return UCrop.Options().apply {
            setCompressionQuality(100)
            setHideBottomControls(false)
            setCircleDimmedLayer(true)
            setToolbarTitle("Crop Image")
        }
    }

    // permission camera, write file, read file , and image
    private fun permissionUploadImages() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                dispatchCaptureImageIntent()
            } else if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                selectImageIntent()
            } else {
                showMessage(
                    requireActivity(),
                    "Failed", "Not All Permission Granted!", MotionToast.TOAST_WARNING
                )
            }
        }
    }

    // loading in bottomsheet
    private fun loadingInBottomSheet(isLoading: Boolean) {
        val progressBarSheet: ProgressBar = bottomSheetView.findViewById(R.id.progressBar)
        val btnSave: Button = bottomSheetView.findViewById(R.id.btnSave)

        if (isLoading) {
            btnSave.visibility = View.INVISIBLE
            progressBarSheet.visibility = View.VISIBLE
        } else {
            btnSave.visibility = View.GONE
            progressBarSheet.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Profile"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

}