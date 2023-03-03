package org.idpass.smartscanner.capacitor

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.getcapacitor.JSObject
import com.getcapacitor.Logger
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import com.google.gson.Gson
import org.idpass.smartscanner.lib.SmartScannerActivity
import org.idpass.smartscanner.lib.scanner.config.ScannerOptions
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

@CapacitorPlugin(name = "SmartScanner")
class SmartScannerPlugin : Plugin() {

    @PluginMethod
    fun executeScanner(call: PluginCall) {
        try {
            val action = call.getString("action")
            val options: JSONObject = call.getObject("options")
            if (action == "START_SCANNER") {
                Timber.d("executeScanner %s", action)
                val intent = Intent(context, SmartScannerActivity::class.java)
                val scannerOptions = Gson().fromJson(options.toString(), ScannerOptions::class.java)
                intent.putExtra(SmartScannerActivity.SCANNER_OPTIONS, scannerOptions)
                startActivityForResult(call, intent, "requestScannerResult")
            } else {
                call.reject("\"$action\" is not a recognized action.")
            }
        } catch (e: Exception) {
            call.reject(e.toString() ,e)
        }
    }

    @ActivityCallback
    private fun requestScannerResult(call: PluginCall, result: ActivityResult) {
        try {
            if (result.resultCode == Activity.RESULT_CANCELED) {
                Timber.d("SmartScanner: RESULT CANCELLED")
                call.reject("Scanning Cancelled.")
            } else {
                if (result.resultCode == Activity.RESULT_OK) {
                    val returnedResult = result.data?.getStringExtra(SmartScannerActivity.SCANNER_RESULT)
                    try {
                        if (returnedResult != null) {
                            Timber.d("SmartScanner: RESULT OK -- $returnedResult")
                            val scannerResult = JSONObject(returnedResult)
                            val ret = JSObject()
                            ret.put(SmartScannerActivity.SCANNER_RESULT, scannerResult)
                            call.resolve(ret)
                        } else {
                            Timber.d("SmartScanner: RESULT SCANNING NULL")
                            call.reject("Scanning result is null.")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        call.reject(e.toString() ,e)
                    }
                } else {
                    Timber.d("SmartScanner: RESULT SCANNING FAILED")
                    call.reject("Scanning Failed.")
                }
            }
        } catch (e: Exception) {
            call.reject(e.toString() ,e)
        }
    }
}
