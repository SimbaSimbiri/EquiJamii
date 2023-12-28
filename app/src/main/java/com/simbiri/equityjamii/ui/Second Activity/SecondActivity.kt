package com.simbiri.equityjamii.ui


import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simbiri.equityjamii.databinding.SecondActivityBinding

import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.simbiri.equityjamii.R

@RequiresApi(Build.VERSION_CODES.M)
class SecondActivity : AppCompatActivity() {


    private lateinit var binding: SecondActivityBinding
    private var imageUri: Uri? = null
    var storagePerms: Array<String>? = null
    var clickedProfile = false
    var clickedBackG = false
    private lateinit var cropProfileContractOptions: CropImageContractOptions
    private lateinit var cropBackGContractOptions: CropImageContractOptions

    val openLastPicker = registerForActivityResult(CropImageContract()) { result ->

        if (result.isSuccessful) {
            imageUri = result.uriContent
            if (clickedProfile) {
                Glide.with(this).load(result.uriContent).into(binding.profileImage)
                clickedProfile = false
            } else if (clickedBackG) {
                Glide.with(this).load(result.uriContent).into(binding.imageBackGround)
                clickedBackG = false
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = SecondActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        storagePerms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        binding.profileImage.setImageURI(imageUri)


        binding.profileImage.setOnClickListener {
            clickedProfile = true
            clickedBackG = false
            showImagePicker()

        }

        binding.imageBackGround.setOnClickListener {
            clickedBackG = true
            clickedProfile = false
            showImagePicker()

        }



    }

    fun showImagePicker() {

        val profileHeight = windowManager.defaultDisplay.height
        val backGroundHeight = binding.imageBackGround.height

        cropProfileContractOptions = CropImageContractOptions(
            null, CropImageOptions(
                true,
                true,
                CropImageView.CropShape.OVAL,
                cropCornerRadius = 8.0F,
                cropMenuCropButtonTitle = "Done",
                showCropLabel = true,
                activityTitle = "CROP IMAGE",
                activityBackgroundColor = this.resources.getColor(R.color.black),
                toolbarColor = this.resources.getColor(R.color.black),
                progressBarColor = this.resources.getColor(R.color.karbBackgrndtint),
                guidelines = CropImageView.Guidelines.OFF, aspectRatioX = 1, aspectRatioY = 1,
                )
        )

        cropBackGContractOptions = CropImageContractOptions(
            null, CropImageOptions(
                true,
                true,
                CropImageView.CropShape.RECTANGLE,
                cropCornerRadius = 8.0F,
                cropMenuCropButtonTitle = "Done",
                showCropLabel = true,
                activityTitle = "CROP IMAGE",
                activityBackgroundColor = this.resources.getColor(R.color.black),
                toolbarColor = this.resources.getColor(R.color.black),
                progressBarColor = this.resources.getColor(R.color.karbBackgrndtint),
                guidelines = CropImageView.Guidelines.OFF, aspectRatioX = 16, aspectRatioY = 9
            )
        )
        if (!checkStoragePermission()) {
            requestStoragePermission()

        } else {
            if (clickedProfile) {
                openLastPicker.launch(cropProfileContractOptions)

            } else if (clickedBackG) {
                openLastPicker.launch(cropBackGContractOptions)
            }

        }


    }


    private fun requestStoragePermission() {


        requestPermissions(storagePerms!!, 200)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults.size > 0) {
                val writeStorageIsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (writeStorageIsAccepted) {
                    if (clickedProfile) {
                        openLastPicker.launch(cropProfileContractOptions)
                    } else if (clickedBackG) {
                        openLastPicker.launch(cropBackGContractOptions)
                    }
                }
            } else {
                Toast.makeText(this, "Please enable access to Gallery", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun checkStoragePermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


}
