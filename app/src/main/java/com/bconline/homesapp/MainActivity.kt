package com.bconline.homesapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bconline.homesapp.sharedSNS.KakaolinkProvider
import com.bconline.homesapp.sharedSNS.LineLinkProvider
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.WebSettings
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.webkit.WebView
import android.webkit.WebChromeClient
import com.bconline.homesapp.service.LocationService
import com.bconline.homesapp.service.PermissionService
import com.bconline.homesapp.service.UploadService
import com.bconline.homesapp.sharedSNS.TwitterProvider


class MainActivity : AppCompatActivity() {

    private val REQUEST_PICK_MEMBER_CODE: Int = 1000
    private val REQUEST_PICK_INQUERY_CODE: Int = 1001

    private var uploadService: UploadService? = null
    private var locationService:LocationService? = null

    //webview bridge로 받을 member정보
    private var grobalMemberId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWebview()
        uploadService = UploadService(webview, this)
        locationService = LocationService(this)
        if( PermissionService.locationPermissionCheck(this) ){
            locationService!!.initLocation()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if ( PermissionService.onRequestPermissionsResult(this, requestCode, permissions, grantResults) ){
            locationService!!.initLocation()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_MEMBER_CODE) {
            if( grobalMemberId == null){
                Toast.makeText(this,"잠시후 다시 시도해주세요.(member miss)", Toast.LENGTH_LONG).show()
            }else {
                uploadService!!.uploadMemberPhoto(data!!.data, grobalMemberId!!)
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_INQUERY_CODE){
            uploadService!!.uploadInqueryPhoto(data!!.data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun pickImageFromGallery(reqeustCode:Int){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,reqeustCode)
    }

    private fun pickMultiImageFromGallery(reqeustCode:Int){
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent,"이미지선택"),reqeustCode)
    }

    override fun onBackPressed() {
        Log.d("MainActivity","onBackPressed canGoBack=" + webview.canGoBack() + ", url="+webview.url)
        if( isLastedPage() ){
            showExitDialog()
        } else {
            webview.goBack()
        }
    }

    private fun isLastedPage():Boolean{
        if( webview.getUrl().endsWith("/login")
            || webview.getUrl().endsWith("/join")
            || webview.getUrl().endsWith("/map")
            || webview.getUrl().endsWith("/contents")
            || webview.getUrl().endsWith("/mlike")
            || webview.getUrl().endsWith("/recent")
            || webview.getUrl().endsWith("/clike")
            || webview.getUrl().endsWith("/mypage")
            || webview.getUrl().endsWith("/notice")
            || webview.getUrl().endsWith("/event")
            || webview.getUrl().endsWith("/faq")
            || webview.getUrl().endsWith("/inquiry")
            || webview.getUrl().endsWith("/member/edit")
            || webview.getUrl().endsWith("/member/setting")
            || !webview.canGoBack() ){
            return true
        }
        return false
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
                val url:String = (if (request != null) request.getUrl() else null).toString()
                if("sms".equals(url.substring(0,3))){
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                    startActivity(intent)
                    return true
                } else if("tel".equals(url.substring(0,3))){
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                    return true
                }
                Log.d("MainActivity","shouldOverrideUrlLoading " + url)
                view?.loadUrl(url)
                return true
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                webview.loadUrl("file:///android_asset/error.html")
                super.onReceivedError(view, request, error)
            }
        }
        webview.webChromeClient = object : WebChromeClient(){
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                val builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.Theme_AppCompat_Light_Dialog))
                builder.setMessage(message)
                builder.setPositiveButton("확인") { _, _ ->
                    result!!.cancel()
                }
                builder.show()
                result!!.cancel()
                return true
            }

            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                val builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.Theme_AppCompat_Light_Dialog))
                builder.setMessage(message)
                builder.setPositiveButton("확인") { _, _ ->
                    result!!.confirm()
                }
                builder.setNegativeButton("취소") { _, _ ->
                    result!!.cancel()
                }
                builder.show()
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                Toast.makeText(this@MainActivity,"잠시후 다시 시도해주세요.(member miss)", Toast.LENGTH_LONG).show()
                val newWebView = WebView(this@MainActivity)
                val settings = newWebView.settings
                settings.javaScriptEnabled=true
                val dialog = Dialog(this@MainActivity)
                dialog.setContentView(newWebView)
                dialog.show()
                newWebView.webChromeClient = object: WebChromeClient(){
                    override fun onCloseWindow(window: WebView?) {
                        Log.i("WebSettings", "new webview onCloseWindow")
                        dialog.dismiss()
                    }
                }
                val wvt :WebView.WebViewTransport = resultMsg!!.obj as WebView.WebViewTransport
                wvt.webView = newWebView
                Log.i("WebSettings", "new webview attach")
                resultMsg.sendToTarget()
                return true
            }
        }
        val setting = webview.settings
        setting.javaScriptEnabled = true
        setting.javaScriptCanOpenWindowsAutomatically = true
        if(Build.VERSION.SDK_INT > 21) {
            setting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        } else {
            try {
                val m = WebSettings::class.java.getMethod(
                    "setMixedContentMode",
                    Int::class.javaPrimitiveType
                )
                m.invoke(setting, 2) // 2 = MIXED_CONTENT_COMPATIBILITY_MODE
                Log.i("WebSettings", "Successfully set MIXED_CONTENT_COMPATIBILITY_MODE")
            } catch (ex: Exception) {
                Log.e("WebSettings", "Error calling setMixedContentMode: " + ex.message, ex)
            }
        }
        webview.addJavascriptInterface(JavascriptInterface(),"HomesAppMobile")

        val url:String = "https://homesapp.co.kr?app=android&version="+applicationContext.packageManager.getPackageInfo(packageName,0).versionName
        Log.d("MainActivity","loadUrl " + url)
        webview.loadUrl(url)
    }

    private fun reload(){
        webview.goBack()
    }

    private inner class JavascriptInterface{

        @android.webkit.JavascriptInterface
        fun getMyPosition(): String{
            Log.d("JavascriptInterface", "getMyPosition ")
            return locationService!!.getLocationString()
        }

        @android.webkit.JavascriptInterface
        fun showToastMessage(message: String){
            Toast.makeText(this@MainActivity,message, Toast.LENGTH_SHORT).show()
            Log.d("JavascriptInterface", "showToastMessage message="+message)
        }

        @android.webkit.JavascriptInterface
        fun shareKakao(url: String, imageUrl: String,title: String,content: String){
            KakaolinkProvider.sendKakaoLink(this@MainActivity,imageUrl, title, content,url)
        }

        @android.webkit.JavascriptInterface
        fun shareLine(url: String){
            //LineLinkProvider.sendLink(this@MainActivity,"https://homesapp.co.kr")
            TwitterProvider.share(this@MainActivity,"test", url)
        }

        @android.webkit.JavascriptInterface
        fun memberUpload(memberId:String){
            grobalMemberId = memberId
            pickImageFromGallery(REQUEST_PICK_MEMBER_CODE)
        }

        @android.webkit.JavascriptInterface
        fun inquiryUpload(){
            pickImageFromGallery(REQUEST_PICK_INQUERY_CODE)
        }

        @android.webkit.JavascriptInterface
        fun onReLoad(){
            webview.post(object: Runnable{
                override fun run() {
                    reload()
                }
            })
        }
    }

}
