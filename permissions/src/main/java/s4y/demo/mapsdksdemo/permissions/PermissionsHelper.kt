package s4y.demo.mapsdksdemo.permissions

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts

class PermissionsHelper {
    fun requestPermissions(activityResultCaller: ActivityResultCaller, permissions: Array<String>) {
        activityResultCaller.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){
            // TODO
        }
    }

}