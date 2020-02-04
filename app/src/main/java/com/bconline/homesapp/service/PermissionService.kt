package com.bconline.homesapp.service

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionService {

    private const val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: Int = 100
    private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 101
    private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: Int = 102

    fun locationPermissionCheck(activity:Activity):Boolean{
        if( ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(android. Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
            }
        }else if( ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(android. Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
        }else if( ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(android. Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            }
        } else {
            return true
        }
        return false
    }

    fun onRequestPermissionsResult(activity:Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean {
        var result:Boolean = false;
        when(requestCode){
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                Log.d("PermissionService","onRequestPermissionsResult MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION")
                result = locationPermissionCheck(activity)
            }
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                Log.d("PermissionService","onRequestPermissionsResult MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION")
                result = locationPermissionCheck(activity)
            }
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE ->{
                Log.d("PermissionService","onRequestPermissionsResult MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE")
                result = locationPermissionCheck(activity)
            }
            else -> {
                Log.d("PermissionService","onRequestPermissionsResult else")
            }
        }
        return result
    }
}