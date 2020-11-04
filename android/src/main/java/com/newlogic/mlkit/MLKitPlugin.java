package com.newlogic.mlkit;

import android.app.Activity;
import android.content.Intent;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.newlogic.mlkitlib.newlogic.MLKitActivity;
import com.newlogic.mlkitlib.newlogic.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

import static com.newlogic.mlkitlib.newlogic.config.Actions.START_MLKIT;

/**
 * Sample capacitor call:
 * ===============================================================================
 * const result = await MLKitPlugin.executeMLKit({
 *         action: 'START_MLKIT',
 *         mode: 'mrz',
 *         mrzFormat: 'MRTD_TD1', // Format MRTD_TD1, but default is MRP
 *         config: {
 *           branding: false, // if true, ID pass branding will appear
 *           background: '#ffc234', // default color gray if empty, will accept hex color values only
 *           font: 'NOTO_SANS_ARABIC',
 *           imageResultType: 'base_64', // default path if empty or not set to base64
 *           label: 'التقط الصورة',
 *           isManualCapture: true // if true, manual capture button will appear
 *         }
 *      }
 *  );
 * ==================================================================================
* */
@NativePlugin(requestCodes = {MLKitPlugin.REQUEST_OP_MLKIT})
public class MLKitPlugin extends Plugin {

    public static final int REQUEST_OP_MLKIT = 1001;

    @PluginMethod
    public void startMLActivity(PluginCall call) {
        String mode = call.getString("mode");
        Timber.d("startMLActivity %s", mode);
        saveCall(call);
        if (mode.equals("mrz")) {
            Intent intent = new Intent(getActivity(), MLKitActivity.class);
            intent.putExtra("mode", mode);
            startActivityForResult(call, intent, REQUEST_OP_MLKIT);
        } else {
            call.error("\"" + mode + "\" is not a recognized mode.");
        }
    }

    @PluginMethod
    public void executeMLKit(PluginCall call) {
        JSONObject config = call.getObject("config");
        String action = call.getString("action");
        String mode = call.getString("mode");
        String mrzFormat = call.getString("mrzFormat");
        saveCall(call);
        if (action.equals(START_MLKIT.getValue())) {
            Timber.d("executeMLKit %s", action);
            Activity activity = getActivity();
            Intent intent = new Intent(activity, MLKitActivity.class);
            try {
                String background = config.getString("background");
                String font = config.getString("font");
                String label = config.getString("label");
                String imageResultType = config.getString("imageResultType");
                boolean branding = config.getBoolean("branding");
                boolean isManualCapture = call.getBoolean("isManualCapture");
                Config readerConfig = new Config(branding, background, font,imageResultType, label, isManualCapture);
                intent.putExtra(MLKitActivity.MODE, mode);
                intent.putExtra(MLKitActivity.MRZ_FORMAT, mrzFormat);
                intent.putExtra(MLKitActivity.CONFIG, readerConfig);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivityForResult(call, intent, REQUEST_OP_MLKIT);
        } else {
            call.error("\"" + action + "\" is not a recognized action.");
        }
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }
        if (requestCode == REQUEST_OP_MLKIT) {
            Timber.d("Plugin post MLKit Activity resultCode %d", resultCode);
            if (resultCode == Activity.RESULT_OK) {
                String returnedResult = data.getStringExtra(MLKitActivity.MLKIT_RESULT);
                Timber.d("Plugin post MLKit Activity result %s", returnedResult);
                try {
                    JSONObject result = new JSONObject(returnedResult);
                    JSObject ret = new JSObject();
                    ret.put(MLKitActivity.MLKIT_RESULT, result);
                    savedCall.success(ret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Timber.d("Plugin post MLKit Activity. RESULT CANCELLED");
                savedCall.error("Scanning Cancelled.");
            } else {
                savedCall.error("Scanning Failed.");
            }
        } else {
            savedCall.error("Unknown Request Code!");
        }
    }
}
