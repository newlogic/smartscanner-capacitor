package org.idpass.smartscanner.capacitor

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.getcapacitor.JSObject
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

@CapacitorPlugin
class SmartScannerPlugin : Plugin() {

    companion object {
        const val handleSmartScannerResult = "handleSmartScannerResult"
    }

    @PluginMethod
    fun executeScanner(call: PluginCall) {
        val action = call.getString("action")
        val options: JSONObject = call.getObject("options")
        call.setKeepAlive(true)
        if (action == "START_SCANNER") {
            Timber.d("SmartScannerPlugin -- executeScanner $action")
            val intent = Intent(context, SmartScannerActivity::class.java)
            val scannerOptions = Gson().fromJson(options.toString(), ScannerOptions::class.java)
            intent.putExtra(SmartScannerActivity.SCANNER_OPTIONS, scannerOptions)
            startActivityForResult(call, intent, handleSmartScannerResult)
        } else {
            call.reject("\"$action\" is not a recognized action.")
        }
    }

    @ActivityCallback
    fun handleSmartScannerResult(call: PluginCall, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Timber.d("SmartScannerPlugin -- RESULT CANCELLED")
            call.reject("Scanning Cancelled.")
        } else {
            try {
                Timber.d("SmartScannerPlugin -- resultCode ${result.resultCode}")
                if (result.resultCode == Activity.RESULT_OK) {
                    val returnedResult = result.data?.getStringExtra(SmartScannerActivity.SCANNER_RESULT)
                    try {
                        Timber.d("SmartScannerPlugin -- result $returnedResult")
                        if (returnedResult != null) {
                            val ret = JSONObject(returnedResult)
                            val scannedResult = JSObject()
                            scannedResult.put(SmartScannerActivity.SCANNER_RESULT, ret)
                            call.resolve(scannedResult)
                        } else {
                            call.reject("Scanning result is null.")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    call.reject("Scanning Failed.")
                }
            } catch (exception: Exception) {
                Timber.e(exception)
                exception.printStackTrace()
            }
        }
    }
}