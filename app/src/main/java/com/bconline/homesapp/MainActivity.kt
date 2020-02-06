package com.bconline.homesapp

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.WebSettings
import android.net.Uri
import android.provider.MediaStore
import android.webkit.WebView
import android.webkit.WebChromeClient
import com.bconline.homesapp.Util.WebViewUtil
import com.bconline.homesapp.service.ImageService
import com.bconline.homesapp.service.LocationService
import com.bconline.homesapp.service.PermissionService
import com.bconline.homesapp.service.UploadService
import com.bconline.homesapp.sharedSNS.*


class MainActivity : AppCompatActivity() {
    companion object{
        private const val REQUEST_PICK_MEMBER_CODE: Int = 1000
        private const val REQUEST_PICK_INQUERY_CODE: Int = 1001
    }

    private var uploadService: UploadService? = null
    private var locationService:LocationService? = null

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
            val pref = this@MainActivity.getPreferences(0)
            if( pref.getString("MEMBER_ID",null) == null){
                Toast.makeText(this,"잠시후 다시 시도해주세요.(member miss)", Toast.LENGTH_LONG).show()
            }else {
                uploadService!!.uploadMemberPhoto(data!!.data, pref.getString("MEMBER_ID",null)!!)
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_INQUERY_CODE){
            uploadService!!.uploadInqueryPhoto(data!!.data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        Log.d("MainActivity","onBackPressed canGoBack=" + webview.canGoBack() + ", url="+webview.url)
        if( WebViewUtil.isLastedPage(webview) ){
            showExitDialog()
        } else {
            webview.goBack()
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
        }
        val setting = webview.settings
        setting.userAgentString = WebViewUtil.userAgent(this)
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
        webview.loadUrl(WebViewUtil.BASE_URL)
    }

    private fun reload(){
        webview.goBack()
    }

    inner class JavascriptInterface{

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
            LineLinkProvider.share(this@MainActivity,url)
        }

        @android.webkit.JavascriptInterface
        fun shareTwitter(url: String, text:String){
            TwitterProvider.share(this@MainActivity, url,text)
        }

        @android.webkit.JavascriptInterface
        fun shareKakaoStory(url: String, text:String){
            KakaoStoryProvider.share(this@MainActivity,url,text)
        }

        @android.webkit.JavascriptInterface
        fun shareFacebook(url: String){
            FacebookProvider.share(this@MainActivity, url)
        }

        @android.webkit.JavascriptInterface
        fun memberUpload(memberId:String){
            val pref = this@MainActivity.getPreferences(0)
            val editor = pref.edit()
            editor.putString("MEMBER_ID",memberId).apply()
            ImageService.pickImageFromGallery(this@MainActivity, REQUEST_PICK_MEMBER_CODE)
        }

        @android.webkit.JavascriptInterface
        fun inquiryUpload(){
            ImageService.pickImageFromGallery(this@MainActivity,REQUEST_PICK_INQUERY_CODE)
        }

        @android.webkit.JavascriptInterface
        fun openPopup(url:String){
            Log.d("JavascriptInterface", "openPopup url=$url")
            val intent = Intent(this@MainActivity,PopupActivity::class.java)
            intent.putExtra("URL",url)
            startActivity(intent)
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
