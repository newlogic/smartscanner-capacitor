package org.idpass.smartscanner.capacitor

import android.app.Activity
import android.content.Intent
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.google.gson.Gson
import org.idpass.smartscanner.lib.SmartScannerActivity
import org.idpass.smartscanner.lib.scanner.config.ScannerOptions
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

@CapacitorPlugin(
    name = "SmartScanner",
    requestCodes = [SmartScannerPlugin.REQUEST_OP_SCANNER],
    permissions = {
        @Permission(
          strings = { Manifest.permission.ACCESS_NETWORK_STATE },
          alias = "network"
        ),
        @Permission(strings = { Manifest.permission.INTERNET }, alias = "internet"),
        @Permission(
          strings = { Manifest.permission.CAMERA },
          alias = "camera"
        )
    }
 )
class SmartScannerPlugin : Plugin() {

    companion object {
        const val REQUEST_OP_SCANNER = 1001
    }

    @PluginMethod
    fun executeScanner(call: PluginCall) {
        val action = call.getString("action")
        val options: JSONObject = call.getObject("options")
        saveCall(call)
        if (action == "START_SCANNER") {
            Timber.d("executeScanner %s", action)
            val intent = Intent(context, SmartScannerActivity::class.java)
            val scannerOptions = Gson().fromJson(options.toString(), ScannerOptions::class.java)
            intent.putExtra(SmartScannerActivity.SCANNER_OPTIONS, scannerOptions)
            startActivityForResult(call, intent, REQUEST_OP_SCANNER)
        } else {
            call.reject("\"$action\" is not a recognized action.")
        }
    }

    override fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.handleOnActivityResult(requestCode, resultCode, data)
        val savedCall = savedCall ?: return
        if (requestCode == REQUEST_OP_SCANNER) {
            try {
                Timber.d("Plugin post SmartScannerActivity resultCode %d", resultCode)
                if (resultCode == Activity.RESULT_OK) {
                    val returnedResult = data?.getStringExtra(SmartScannerActivity.SCANNER_RESULT)
                    Timber.d("Plugin post SmartScannerActivity result %s", returnedResult)
                    try {
                        if (returnedResult != null) {
                            val result = JSONObject(returnedResult)
                            val ret = JSObject()
                            ret.put(SmartScannerActivity.SCANNER_RESULT, result)
                            savedCall.resolve(ret)
                        } else {
                            savedCall.reject("Scanning result is null.")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Timber.d("Plugin post SmartScannerActivity RESULT CANCELLED")
                    savedCall.reject("Scanning Cancelled.")
                } else {
                    savedCall.reject("Scanning Failed.")
                }
            } catch (exception: Exception) {
                Timber.e(exception)
                exception.printStackTrace()
            }
        } else {
            savedCall.reject("Unknown Request Code!")
        }
    }
}