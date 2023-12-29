package com.simbiri.equityjamii.ui


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.simbiri.equityjamii.databinding.SecondActivityBinding
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.simbiri.equityjamii.R

class SecondActivity : AppCompatActivity() {


    private lateinit var binding: SecondActivityBinding
    private var imageProfileUri: Uri? = null
    private var imageBackgUri: Uri? = null
    var storagePerms: Array<String>? = null
    var clickedProfile = false
    var clickedBackG = false
    var userId = FirebaseAuth.getInstance().currentUser!!.uid
    var firebaseAuth =FirebaseAuth.getInstance()
    private lateinit var cropProfileContractOptions: CropImageContractOptions
    private lateinit var cropBackGContractOptions: CropImageContractOptions
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    val openLastPicker = registerForActivityResult(CropImageContract()) { result ->

        if (result.isSuccessful) {
            if (clickedProfile) {
                imageProfileUri = result.uriContent
                Glide.with(this).load(imageProfileUri).into(binding.profileImage)
            } else if (clickedBackG) {
                imageBackgUri = result.uriContent
                Glide.with(this).load(imageBackgUri).into(binding.imageBackGround)
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = SecondActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        binding.progressBar.isVisible =  true

        var layoutParamsProfileCardOut = binding.materialCardView.layoutParams
        var layoutParamsProfileCardIn = binding.materialCardViewIn.layoutParams
        var layoutParamsBackG = binding.imageBackGround.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        layoutParamsProfileCardOut.width = screenWidth/3 + 80
        layoutParamsProfileCardIn.width = screenWidth/3 - 10 + 80

        layoutParamsProfileCardOut.height = screenWidth/3 + 80
        layoutParamsProfileCardIn.height = screenWidth/3 - 10 + 80
        layoutParamsBackG.height =  screenWidth/3 + 100
        layoutParamsBackG.width =  screenWidth


        binding.materialCardView.layoutParams = layoutParamsProfileCardOut
        binding.materialCardViewIn.layoutParams = layoutParamsProfileCardIn
        binding.imageBackGround.layoutParams = layoutParamsBackG



        storagePerms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        binding.profileImage.setImageURI(imageProfileUri)

        firestore.collection("Users").document(userId).get().addOnCompleteListener { taskDocSnapShot ->
            if (taskDocSnapShot.isSuccessful){
                if (taskDocSnapShot.result.exists()){
                    val nameRetrieved =  taskDocSnapShot.result.getString("name")
                    val designRetrieved = taskDocSnapShot.result.getString("designation")
                    val branchRetrieved = taskDocSnapShot.result.getString("branch")
                    val profileRetrieved = taskDocSnapShot.result.getString("profileUri")
                    val backGRetrieved = taskDocSnapShot.result.getString("backGUri")
                    imageProfileUri = Uri.parse(profileRetrieved)
                    imageBackgUri =  Uri.parse(backGRetrieved)

                    binding.nameProfileEdit.setText(nameRetrieved)
                    binding.designationProfileEdit.setText(designRetrieved)
                    binding.branchProfileEdit.setText(branchRetrieved)
                    if (profileRetrieved != "null") {
                        Glide.with(this).load(Uri.parse(profileRetrieved)).into(binding.profileImage)
                    }
                    if (backGRetrieved != "null"){
                    Glide.with(this).load(Uri.parse(backGRetrieved)).into(binding.imageBackGround)}

                    Toast.makeText(this, "Profile updated with most recent info", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Profile data unchanged from app restart", Toast.LENGTH_LONG).show()
            }

            binding.progressBar.isVisible =  false

        }



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

        binding.saveProfileButton.setOnClickListener {
            saveProfileInfo()
        }
        binding.signOutProfileBtn.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this@SecondActivity,SignInActivity::class.java )
            startActivity(intent)
            finish()
        }


    }

    private fun saveProfileInfo() {
        binding.progressBar.isVisible = true

        val name = binding.nameProfileEdit.text!!.toString()
        val designation = binding.designationProfileEdit.text!!.toString()
        val branch = binding.branchProfileEdit.text!!.toString()
        val imageProfileReference = storageReference.child("Profile_pics").child("$userId.jpg")
        val backGReference = storageReference.child("BackG_pics").child("$userId.jpg")

        if (clickedProfile && !clickedBackG) {
            if (imageProfileUri != null || !name.isNotEmpty() || !designation.isNotEmpty() || !branch.isNotEmpty()) {
                //upload image profile uri only
                imageProfileReference.putFile(imageProfileUri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageProfileReference.downloadUrl.addOnSuccessListener { profileUri ->
                            saveToFireStore(
                                task,
                                name,
                                designation,
                                branch,
                                profileUri,
                                imageBackgUri
                            )
                        }
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                binding.progressBar.isVisible = false

            }
        } else if (clickedBackG && !clickedProfile) {
            if (imageBackgUri != null || !name.isNotEmpty() || !designation.isNotEmpty() || !branch.isNotEmpty()) {
                //upload image profile uri only
                backGReference.putFile(imageBackgUri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        backGReference.downloadUrl.addOnSuccessListener { backGUri ->
                            saveToFireStore(
                                task,
                                name,
                                designation,
                                branch,
                                imageProfileUri,
                                backGUri
                            )

                        }
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                binding.progressBar.isVisible = false

            }
        }
        else {
            saveToFireStore(null, name, designation, branch, imageProfileUri, imageBackgUri)
        }

    }

    private fun saveToFireStore(
        task: Task<UploadTask.TaskSnapshot>?,
        name: String,
        designation: String,
        branch: String,
        profileUri: Uri?,
        imageBackgUri: Uri?,
    ) {
        val mapToFirestore = HashMap<String, String>()
        mapToFirestore["name"] = name
        mapToFirestore["designation"] = designation
        mapToFirestore["branch"] = branch
        mapToFirestore["profileUri"] = profileUri.toString()
        mapToFirestore["backGUri"] = imageBackgUri.toString()

        firestore.collection("Users").document(userId).set(mapToFirestore)
            .addOnCompleteListener { taskUpload ->
                if (taskUpload.isSuccessful) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false

                } else {
                    Toast.makeText(this, taskUpload.exception.toString(), Toast.LENGTH_SHORT).show()
                    Log.i("Error saving to firestore", taskUpload.exception.toString())
                }
            }


    }

    fun showImagePicker() {

        val profileHeight = windowManager.defaultDisplay.height
        val backGroundHeight = binding.imageBackGround.height

        if (clickedProfile){

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
        )}

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
                openLastPicker.launch(cropBackGContractOptions)

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
