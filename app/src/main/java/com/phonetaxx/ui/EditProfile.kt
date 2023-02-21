package com.phonetaxx.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.firebase.storage.FirebaseStorage
import com.phonetaxx.R
import com.phonetaxx.extension.hideProgressDialog
import com.phonetaxx.extension.showProgressDialog
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseUserHelper
import com.phonetaxx.firebase.model.UsersDbModel
import com.phonetaxx.utils.PreferenceHelper
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class EditProfile : AppCompatActivity(), View.OnClickListener {

    private var name: EditText? = null
    private var email: EditText? = null
    private var businessName: EditText? = null
    private var naicsCode: EditText? = null
    private var location: EditText? = null
    private var superVisor: EditText? = null
    private var eincode: EditText? = null
    private var userImage: CircleImageView? = null
    private var editImage: ImageView? = null
    private var back: ImageView? = null
    private var save: TextView? = null
    private var phoneNumber: AppCompatEditText? = null
    private var selectedCountryCode = "+1"
    private var options: Options? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        inItView()
        setOnClickListner()
    }

    private fun inItView() {
        name = findViewById(R.id.ep_name)
        email = findViewById(R.id.ep_email)
        businessName = findViewById(R.id.ep_business_name)
        phoneNumber = findViewById(R.id.ep_etPhoneNumber)
        naicsCode = findViewById(R.id.ep_naics_code)
        location = findViewById(R.id.ep_location)
        superVisor = findViewById(R.id.ep_supervisor_email)
        eincode = findViewById(R.id.ep_ein)
        userImage = findViewById(R.id.ep_image)
        editImage = findViewById(R.id.ep_edit_image)
        back = findViewById(R.id.ep_back)
        save = findViewById(R.id.ep_save)

        name?.setText(PreferenceHelper.getInstance().getProfileData()!!.fullName)
        email?.setText(PreferenceHelper.getInstance().getProfileData()!!.email)
        businessName?.setText(PreferenceHelper.getInstance().getProfileData()!!.businessName)
        phoneNumber?.setText(PreferenceHelper.getInstance().getProfileData()!!.phoneNumber)
        naicsCode?.setText(PreferenceHelper.getInstance().getProfileData()!!.naicscode)
        location?.setText(PreferenceHelper.getInstance().getProfileData()!!.location)
        superVisor?.setText(PreferenceHelper.getInstance().getProfileData()!!.supervisoremail)
        eincode?.setText(PreferenceHelper.getInstance().getProfileData()!!.eincode)


        Glide.with(this@EditProfile)
            .load(PreferenceHelper.getInstance().getProfileData()!!.profileUrl).into(userImage!!)

        ep_ccp.typeFace = ResourcesCompat.getFont(this, R.font.rubik_regular)
        var countryCode = PreferenceHelper.getInstance().getProfileData()?.countryCode
        var cCode = countryCode?.replace("+", "")
        ep_ccp.setCountryForPhoneCode(cCode!!.toInt())
        ep_ccp.setOnCountryChangeListener { selectedCountry ->
            selectedCountryCode = "+" + selectedCountry.phoneCode
        }


        options = Options.init()
            .setRequestCode(101)
            .setCount(1)
            .setFrontfacing(false)
            .setExcludeVideos(false)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
    }

    private fun setOnClickListner() {
        editImage?.setOnClickListener(this)
        back?.setOnClickListener(this)
        save?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ep_back -> {
                super.onBackPressed()
            }
            R.id.ep_edit_image -> {
                Pix.start(this@EditProfile, options)
            }
            R.id.ep_save -> {
                if (validForm()) {
                    updateUser()
                }
            }
        }
    }

    private fun updateUser() {
        showProgressDialog()

        FireBaseUserHelper.getInstance()
            .updateName(name?.text.toString().trim(), object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }
                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updateCountryCode(selectedCountryCode, object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }
                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updatePhoneNumber(phoneNumber?.text.toString().trim(), object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }
                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updateBusinessName(businessName?.text.toString().trim(), object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }
                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updateNaicsCode(naicsCode?.text.toString().trim(), object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }
                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updateLocation(location?.text.toString().trim(), object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }
                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updateSupervisorEmail(superVisor?.text.toString().trim(), object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }
                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        FireBaseUserHelper.getInstance()
            .updateEincode(eincode?.text.toString().trim(), object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {

                }
                override fun onFail(errorMessage: String, exception: Exception?) {
                }
            })

        hideProgressDialog()

    }

    private fun validForm(): Boolean {

        if (name?.text.isNullOrEmpty()) {
            showToast(getString(R.string.err_full_name))
            return false
        }
        if (email?.text.isNullOrEmpty()) {
            showToast(getString(R.string.err_enter_email))
            return false
        }
        if (businessName?.text.isNullOrEmpty()) {
            showToast("Enter business name")
            return false
        }
        if (phoneNumber?.text.isNullOrEmpty()) {
            showToast("Enter phone number")
            return false
        }
        if (naicsCode?.text.isNullOrEmpty()) {
            showToast("Enter naics code")
            return false
        }
        if (location?.text.isNullOrEmpty()) {
            showToast("Enter location")
            return false
        }
        if (superVisor?.text.isNullOrEmpty()) {
            showToast("Enter supervisor email")
            return false
        }
        if (superVisor?.text.isNullOrEmpty()) {
            showToast("Enter ein code")
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && resultCode == Activity.RESULT_OK && requestCode == 101) {
            val resultArray =
                data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!
            for (i in resultArray.indices) {
                val path = resultArray[i]
                val file = File(path)
                val imageUri =
                    Uri.fromFile(File(file.absolutePath))
                CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this@EditProfile)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                try {
                    val bmp = MediaStore.Images.Media.getBitmap(
                        Objects.requireNonNull(this).getContentResolver(), resultUri
                    )
                    Glide.with(this@EditProfile).load(bmp).into(userImage!!)
                    uploadImage(resultUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {

        showProgressDialog()

        val path =
            "user-images/" + PreferenceHelper.getInstance().getProfileData()?.uuId + "/0.jpg"

        val imageRef = FirebaseStorage.getInstance().getReference(path)


        if (imageUri != null) {

            imageRef.putFile(imageUri!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        imageRef.downloadUrl.addOnCompleteListener(this) { secondTask ->
                            if (secondTask.isSuccessful) {
                                var profileUrl = secondTask.result.toString()
                                AddToFirebase(profileUrl);
                            }
                        }

                    } else {
                        Toast.makeText(
                            this@EditProfile,
                            "Unable to update profile at this time.  Please check your internet connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                this@EditProfile,
                "Unable to process profile photo.  Please choose another one or try again.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun AddToFirebase(profileUrl: String) {

        FireBaseUserHelper.getInstance()
            .updateProfileImage(profileUrl, object : FirebaseDatabaseListener<UsersDbModel> {
                override fun onSuccess(data: UsersDbModel) {
                    hideProgressDialog()

                }

                override fun onFail(errorMessage: String, exception: Exception?) {
                    hideProgressDialog()
                }
            })
    }

    private fun persistImage(bitmap: Bitmap): File? {
        val filesDir: File = this@EditProfile.getFilesDir()
        val imageFile =
            File(filesDir, System.currentTimeMillis().toString() + ".jpg")
        val outputStream: OutputStream
        try {
            outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }
        return imageFile
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, options)
                } else {
                    Toast.makeText(
                        this,
                        "Approve permissions to open Pix ImagePicker",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}



