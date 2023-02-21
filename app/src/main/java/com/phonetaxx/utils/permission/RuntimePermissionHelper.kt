import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.phonetaxx.R
import java.util.*

class RuntimePermissionHelper : DialogFragment() {
    private var mAppPermissionListener: PermissionCallbacks? = null
    private var permissions: Array<String>? = null
    private var permissionCode = 0
    private var rationalMessage: String? = null
    fun setAppPermissionListener(mAppPermissionListener: PermissionCallbacks?) {
        this.mAppPermissionListener = mAppPermissionListener
    }

    private fun hasPermissions(
        context: Context?,
        permissions: Array<String?>?
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (permissions == null) {
            return false
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun verifyPermissions(grantResults: IntArray): Boolean { // At least one result must be checked.
        if (grantResults.size < 1) {
            return false
        }
        // Verify that each required permission has been granted, otherwise return false.
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun show(fragmentManager: FragmentManager) {
        if (fragmentManager.findFragmentByTag(TAG) == null) {
            fragmentManager.beginTransaction().add(this, TAG)
                .commitAllowingStateLoss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (targetFragment is PermissionCallbacks) {
            mAppPermissionListener = targetFragment as PermissionCallbacks?
        }
        if (arguments != null) {
            permissions =
                arguments!!.getStringArray(REQUEST_CODE)
            permissionCode = arguments!!.getInt(PERMISSION)
            rationalMessage =
                arguments!!.getString(RATIONAL_MESSAGE)
        }
        checkPermission()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PermissionCallbacks) {
            mAppPermissionListener = context
        }
        if (parentFragment != null && parentFragment is PermissionCallbacks) {
            mAppPermissionListener = parentFragment as PermissionCallbacks?
        }
    }

    override fun onDetach() {
        super.onDetach()
        mAppPermissionListener = null
    }

    private fun getNonGrantedPermissions(permissions: Array<String>?): Array<String?> {
        val permissionList =
            ArrayList<String>()
        for (permission in permissions!!) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(permission)
            }
        }
        if (permissionList.size > 0) {
            val permissionArray =
                arrayOfNulls<String>(permissionList.size)
            permissionList.toArray(permissionArray)
            return permissionArray
        }
        return arrayOf()
    }

    fun checkPermission() {
        val nonGrantedPermissions =
            getNonGrantedPermissions(permissions)
        if (nonGrantedPermissions.isNotEmpty() && !hasPermissions(
                context,
                nonGrantedPermissions
            )
        ) {
            requestPermissions(
                nonGrantedPermissions,
                REQUEST_PERMISSION
            )
            return
        }
        if (mAppPermissionListener != null) {
            mAppPermissionListener!!.onPermissionAllow(permissionCode)
        }
        closeDialog()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            if (verifyPermissions(grantResults)) {
                if (mAppPermissionListener != null) {
                    mAppPermissionListener!!.onPermissionAllow(permissionCode)
                }
                closeDialog()
            } else {
                for (permission in permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            permission
                        )
                    ) {
                        showRationaleDialog()
                        break;
                    } else {
                        showSettingsDialog()
                        break;
                    }
                }
            }
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.declined))
            .setMessage(rationalMessage)
            .setPositiveButton(getString(R.string.retry)) { dialogInterface, _ ->
                checkPermission()
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialogInterface, _ ->
                mAppPermissionListener!!.onPermissionDeny(permissionCode)
                closeDialog()
                dialogInterface.dismiss()
            }.create().show()
    }

    private fun closeDialog() {
        dismissAllowingStateLoss()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.app_name))
            .setMessage(rationalMessage)
            .setPositiveButton(
                getString(R.string.settings)
            ) { dialogInterface, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    context!!.applicationContext.packageName,
                    null
                )
                intent.data = uri
                startActivity(intent)
                dialogInterface.dismiss()
                closeDialog()
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialogInterface, _ ->
                mAppPermissionListener!!.onPermissionDeny(permissionCode)
                dialogInterface.dismiss()
                closeDialog()
            }.create().show()
    }

    interface PermissionCallbacks {
        fun onPermissionAllow(permissionCode: Int)
        fun onPermissionDeny(permissionCode: Int)
    }

    companion object {
        private val TAG = RuntimePermissionHelper::class.java.simpleName
        private const val REQUEST_PERMISSION = 1
        private const val PERMISSION = "PERMISSION"
        private const val REQUEST_CODE = "REQUEST_CODE"
        private const val RATIONAL_MESSAGE = "RATIONAL_MESSAGE"
        fun newInstance(
            permission: String,
            permissionCode: Int,
            rationalMessage: String?
        ): RuntimePermissionHelper {
            val permissions = arrayOf(permission)
            val args = Bundle()
            args.putStringArray(REQUEST_CODE, permissions)
            args.putInt(PERMISSION, permissionCode)
            args.putString(RATIONAL_MESSAGE, rationalMessage)
            val fragment = RuntimePermissionHelper()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(
            permissions: Array<String?>?,
            permissionCode: Int,
            rationalMessage: String?
        ): RuntimePermissionHelper {
            val args = Bundle()
            args.putStringArray(REQUEST_CODE, permissions)
            args.putInt(PERMISSION, permissionCode)
            args.putString(RATIONAL_MESSAGE, rationalMessage)
            val fragment = RuntimePermissionHelper()
            fragment.arguments = args
            return fragment
        }
    }
}