package com.newlogic.mlkit;

import android.app.Activity;
import android.content.Intent;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.newlogic.mlkitlib.newlogic.MLKitActivity;
import com.newlogic.mlkitlib.newlogic.config.ReaderConfig;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

import static com.newlogic.mlkitlib.newlogic.config.Actions.START_MLKIT;

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
        saveCall(call);
        if (action.equals(START_MLKIT.getValue())) {
            Timber.d("executeMLKit %s", action);
            Activity activity = getActivity();
            Intent intent = new Intent(activity, MLKitActivity.class);
            try {
                String mode = config.getString("mode");
                String label = config.getString("label");
                boolean withFlash = config.getBoolean("withFlash");
                ReaderConfig readerConfig = new ReaderConfig("", "", label, mode, withFlash);
                intent.putExtra(MLKitActivity.MLKIT_CONFIG, readerConfig);
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
                JSObject ret = new JSObject();
                ret.put(MLKitActivity.MLKIT_RESULT, returnedResult);
                savedCall.success(ret);
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
