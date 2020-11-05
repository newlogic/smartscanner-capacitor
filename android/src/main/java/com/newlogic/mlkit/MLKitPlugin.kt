package com.newlogic.mlkit

import android.app.Activity
import android.content.Intent
import com.getcapacitor.*
import com.newlogic.mlkitlib.newlogic.MLKitActivity
import com.newlogic.mlkitlib.newlogic.config.Actions
import com.newlogic.mlkitlib.newlogic.config.Config
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 * Sample capacitor call:
 * ===============================================================================
 * const result = await MLKitPlugin.executeMLKit({
 *         action: 'START_MLKIT',
 *         mode: 'mrz',
 *         mrzFormat: 'MRTD_TD1', // Format MRTD_TD1, but default is MRP
 *         config: {
 *           background: '#ffc234', // default color gray if empty, will accept hex color values only
 *           font: 'NOTO_SANS_ARABIC',
 *           imageResultType: 'base_64', // default path if empty or not set to base64
 *           label: 'التقط الصورة',
 *           branding: false, // if true, ID pass branding will appear
 *           isManualCapture: true // if true, manual capture button will appear
 *         }
 *      }
 *  );
 * ==================================================================================
 * */
@NativePlugin(requestCodes = [MLKitPlugin.REQUEST_OP_MLKIT])
class MLKitPlugin : Plugin() {
    @PluginMethod
    fun startMLActivity(call: PluginCall) {
        val mode = call.getString("mode")
        Timber.d("startMLActivity %s", mode)
        saveCall(call)
        if (mode == "mrz") {
            val intent = Intent(activity, MLKitActivity::class.java)
            intent.putExtra("mode", mode)
            startActivityForResult(call, intent, REQUEST_OP_MLKIT)
        } else {
            call.error("\"$mode\" is not a recognized mode.")
        }
    }

    @PluginMethod
    fun executeMLKit(call: PluginCall) {
        val config: JSONObject = call.getObject("config")
        val action = call.getString("action")
        saveCall(call)
        if (action == Actions.START_MLKIT.value) {
            Timber.d("executeMLKit %s", action)
            val activity: Activity = activity
            val intent = Intent(activity, MLKitActivity::class.java)
            val mode = call.getString("mode")
            val mrzFormat = call.getString("mrzFormat")
            try {
                val background = config.getString("background")
                val font = config.getString("font")
                val label = config.getString("label")
                val imageResultType = config.getString("imageResultType")
                val branding = config.getBoolean("branding")
                val isManualCapture = config.getBoolean("isManualCapture")
                val readerConfig = Config(background = background, font = font, imageResultType = imageResultType, label = label, branding = branding, isManualCapture = isManualCapture)
                intent.putExtra(MLKitActivity.MODE, mode)
                intent.putExtra(MLKitActivity.MRZ_FORMAT, mrzFormat)
                intent.putExtra(MLKitActivity.CONFIG, readerConfig)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            startActivityForResult(call, intent, REQUEST_OP_MLKIT)
        } else {
            call.error("\"$action\" is not a recognized action.")
        }
    }

    override fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.handleOnActivityResult(requestCode, resultCode, data)
        val savedCall = savedCall ?: return
        if (requestCode == REQUEST_OP_MLKIT) {
            Timber.d("Plugin post MLKit Activity resultCode %d", resultCode)
            if (resultCode == Activity.RESULT_OK) {
                val returnedResult = data.getStringExtra(MLKitActivity.MLKIT_RESULT)
                Timber.d("Plugin post MLKit Activity result %s", returnedResult)
                try {
                    val result = JSONObject(returnedResult)
                    val ret = JSObject()
                    ret.put(MLKitActivity.MLKIT_RESULT, result)
                    savedCall.success(ret)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Timber.d("Plugin post MLKit Activity. RESULT CANCELLED")
                savedCall.error("Scanning Cancelled.")
            } else {
                savedCall.error("Scanning Failed.")
            }
        } else {
            savedCall.error("Unknown Request Code!")
        }
    }

    companion object {
        const val REQUEST_OP_MLKIT = 1001
    }
}
