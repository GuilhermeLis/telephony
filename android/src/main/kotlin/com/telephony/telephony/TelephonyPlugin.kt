package com.telephony.telephony

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import android.telephony.ServiceState




/** TelephonyPlugin */
class TelephonyPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  private lateinit var applicationContext: Context ;
  private lateinit var  activity: Activity;
  private val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1995
  private val MY_PERMISSIONS_REQUEST_LOCATION = 99


  /// The MethodChannel that wilLouanel the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "telephony")
    applicationContext = flutterPluginBinding.applicationContext;
    channel.setMethodCallHandler(this)
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {



      val telephonyManager = applicationContext.getSystemService(TELEPHONY_SERVICE) as TelephonyManager;
      val resultObject = hashMapOf<String, Any?>();
//      val serviceState = ServiceState()


      // if(Build.VERSION.SDK_INT >= 26 && (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_STATE) == PERMISSION_GRANTED ||
      //         ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)){
      //   ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_PHONE_STATE), MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
      //  resultObject["meid"] = telephonyManager.meid;
      // }

//        resultObject["simSerialNumber"] = telephonyManager.simSerialNumber;
      if (Build.VERSION.SDK_INT >= 30) {
          resultObject["activeModemCount"] = telephonyManager.activeModemCount; //
          resultObject["phoneCount"]= telephonyManager.activeModemCount; //
      }
      if (Build.VERSION.SDK_INT >= 28){
        resultObject["simCarrierId"] = telephonyManager.simCarrierId; //
        resultObject["simCarrierIdName"] = telephonyManager.simCarrierIdName; //
      }
      if(Build.VERSION.SDK_INT >= 26 && onPermission(READ_PHONE_STATE)){
//        resultObject["imei"]= telephonyManager.imei;
        resultObject["networkSpecifier"]= telephonyManager.networkSpecifier;
      }
      if(Build.VERSION.SDK_INT >= 24 && onPermission(READ_PHONE_STATE) ){
        resultObject["networkType"]= telephonyManager.dataNetworkType;
      }
      if (onPermission(READ_PHONE_STATE)){
        resultObject["deviceSoftwareVersion"] = telephonyManager.deviceSoftwareVersion;
        resultObject["isDataEnabled"] = telephonyManager.isDataEnabled;
      }
      if(onPermission(READ_SMS) || onPermission(READ_PHONE_NUMBERS) || onPermission(READ_PHONE_STATE)){
        resultObject["line1Number"] = telephonyManager.line1Number;
      }
//      if (Build.VERSION.SDK_INT >= 17 && onPermission(ACCESS_FINE_LOCATION)) {
        // resultObject["allCellInfo"] = telephonyManager.allCellInfo;
//      }
//      if (onPermission(ACCESS_COARSE_LOCATION)) {
//        resultObject["serviceState"] = telephonyManager.serviceState;
//      }
      resultObject["networkCountryIso"]= telephonyManager.networkCountryIso;
      resultObject["networkOperator"]= telephonyManager.networkOperator;
      resultObject["networkOperatorName"]= telephonyManager.networkOperatorName;
      resultObject["simCountryIso"]= telephonyManager.simCountryIso;
      resultObject["phoneType"] = telephonyManager.phoneType;
//      resultObject["callState"] = telephonyManager.callState;
      resultObject["simOperatorName"] = telephonyManager.simOperatorName;
      resultObject["simOperator"] = telephonyManager.simOperator;
      resultObject["isSmsCapable"] = telephonyManager.isSmsCapable;
      resultObject["isVoiceCapable"] = telephonyManager.isVoiceCapable;


//      resultObject["cellInfo"] = telephonyManager.allCellInfo;

      result.success(resultObject);
    } else {
      result.notImplemented();
    }
  }
    private fun  onPermission(permission: String): Boolean {
    return when(permission){
      ACCESS_FINE_LOCATION -> {
        return if(ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED){
          true;
        }else {
          ActivityCompat.requestPermissions(activity, arrayOf(ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION);
          ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        }
      }
      READ_PHONE_STATE -> {
        return if (ActivityCompat.checkSelfPermission(activity, READ_PHONE_STATE) == PERMISSION_GRANTED){
          true;
        }else{
          ActivityCompat.requestPermissions(activity, arrayOf(READ_PHONE_STATE), MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
          ActivityCompat.checkSelfPermission(activity, READ_PHONE_STATE) == PERMISSION_GRANTED;
        }
      }
      READ_SMS -> {
        return if (ActivityCompat.checkSelfPermission(activity, READ_SMS) == PERMISSION_GRANTED) {
          true;
        } else{
          ActivityCompat.requestPermissions(activity, arrayOf(READ_SMS), 2);
          ActivityCompat.checkSelfPermission(activity, READ_SMS) == PERMISSION_GRANTED;
        }
      }
      READ_PHONE_NUMBERS ->{
        return if (ActivityCompat.checkSelfPermission(activity, READ_PHONE_NUMBERS) == PERMISSION_GRANTED){
          true;
        }else{
          ActivityCompat.requestPermissions(activity, arrayOf(READ_PHONE_NUMBERS), 1);
          ActivityCompat.checkSelfPermission(activity, READ_PHONE_NUMBERS) == PERMISSION_GRANTED
        }
      }
      ACCESS_COARSE_LOCATION -> ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;
      else -> false;
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activity = binding.activity;
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

  }

  override fun onDetachedFromActivity() {

  }
}
