/**
 */
package com.latinad.cordova.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class AutoRestore extends CordovaPlugin {
  private static final String TAG = "AutoRestore";

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    Log.d(TAG, "Initializing AutoRestore");
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if(action.equals("echo")) {
      String phrase = args.getString(0);
      // Echo back the first argument
      Log.d(TAG, phrase);
    } else if(action.equals("enable")) {
      startAutoRestoreService();
    }

    return true;
  }

  private void startAutoRestoreService() {
    Context context = cordova.getActivity().getApplicationContext();
    Log.d("MonitorDebug", "Starting MainActivity Monitor");
    context.startService(new Intent(context, AutoRestoreService.class));
  }
}