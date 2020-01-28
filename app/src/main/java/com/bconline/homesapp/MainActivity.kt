package com.bconline.homesapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private var locationManager : LocationManager? = null
    private val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: Int = 100
    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWebview()
        locationPermissionCheck()
    }

    private fun locationPermissionCheck(){
        if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android. Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
            }
        }else if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android. Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
        } else {
            initLocation()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                Log.d("MainActivity","onRequestPermissionsResult MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION")
                locationPermissionCheck()
            }
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                Log.d("MainActivity","onRequestPermissionsResult MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION")
                locationPermissionCheck()
            }
            else -> {
                Log.d("MainActivity","onRequestPermissionsResult else")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        }catch(ex: SecurityException){
            Log.d("MainActivity","security exception" + ex)
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("MainActivity","gps=" + location.longitude + ":" + location.latitude)
            Toast.makeText(this@MainActivity,"gps=" + location.longitude + ":" + location.latitude, Toast.LENGTH_SHORT).show()
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onBackPressed() {
        Log.d("MainActivity","onBackPressed canGoBack=" + webview.canGoBack() + ", url="+webview.url)
        if( webview.canGoBack() ){
            webview.goBack()
        } else {
            showExitDialog()
            //super.onBackPressed()
        }
    }

    private fun showExitDialog(){
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.Theme_AppCompat_Light_Dialog))
        builder.setMessage("앱을 종료하시겠습니까?")
        builder.setPositiveButton("종료") { _, _ ->
            finish()
        }
        builder.setNegativeButton("취소") { _, _ ->
        }
        builder.show()
    }

    private fun initWebview(){
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url : String  = (if (request != null) request.getUrl() else null).toString()
                Log.d("MainActivity","shouldOverrideUrlLoading " + url)
                view?.loadUrl((if (request != null) request.getUrl() else null).toString())
                return true
            }
        }
        val setting = webview.settings
        setting.javaScriptEnabled = true
        webview.addJavascriptInterface(JavascriptInterface(),"HomesAppMobile")

        val url:String = "https://homesapp.co.kr?app=android&versionName="+applicationContext.packageManager.getPackageInfo(packageName,0).versionName
        Log.d("MainActivity","loadUrl " + url)
        webview.loadUrl(url)
    }

    private inner class JavascriptInterface{

        @android.webkit.JavascriptInterface
        fun getMyPosition(): String{
            Log.d("JavascriptInterface", "getMyPosition ")
            Toast.makeText(this@MainActivity,"getMyPosition ", Toast.LENGTH_SHORT).show()
            return "test"
        }

        @android.webkit.JavascriptInterface
        fun showToastMessage(message: String){
            Toast.makeText(this@MainActivity,message, Toast.LENGTH_SHORT).show()
            Log.d("JavascriptInterface", "showToastMessage message="+message)
        }

        @android.webkit.JavascriptInterface
        fun shareKakao(url: String){
            Toast.makeText(this@MainActivity,"shareKakao " + url, Toast.LENGTH_SHORT).show()
            Log.d("JavascriptInterface", "shareKakao url="+url)
        }
    }

}
